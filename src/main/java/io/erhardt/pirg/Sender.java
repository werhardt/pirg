package io.erhardt.pirg;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.List;

import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.squareup.tape2.ObjectQueue;
import com.squareup.tape2.ObjectQueue.Converter;
import com.squareup.tape2.QueueFile;

import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import io.erhardt.pirg.config.Config;

/**
 * Sends data to a configured server.
 *
 * @author Waldemar Erhardt
 */
public class Sender {

    private static final Logger LOGGER = LoggerFactory.getLogger(Sender.class);

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private boolean started = false;

    private SenderThread senderThread;
    private Config config;

    private ObjectQueue<Message> queue;

    private boolean autoStart;
    private String url;
    private int sendInterval;
    private int sendSize;

    public Sender() {
        this(null);
    }


    public Sender(Config config) {

        if (config == null) {
            config = this.readConfig("/pirg.config");
        }

        if (!init(config)) {
            return;
        }

        if (!started) {
            LOGGER.info("Starting sender..");
            this.senderThread = new SenderThread();
            if (this.autoStart) {
                this.start();
                LOGGER.info("Sender started.");
            } else {
                LOGGER.info(
                    "Sender not started, autostart is deactivated (configuration 'pirg.autostart')");
            }
        }
    }

    private Config readConfig(String filename) {
        try {
            return new Config(!filename.startsWith("/") ? "/" + filename : filename);
        } catch (Exception e) {
            LOGGER.error("pirg could not be initialized: {}", e.getMessage());
            return null;
        }
    }

    private boolean init(Config config) {

        if (config == null) {
            return false;
        }

        this.config = config;

        this.autoStart = config.getBoolean("pirg.autostart", true);

        url = this.config.getString("pirg.url");
        if (url == null || url.trim().isEmpty()) {
            LOGGER.warn("No url is configured. Please make sure to set the url in the messages or configure 'pirg.url'");
        }

        boolean isPersistent = config.getBoolean("pirg.persist", false);

        if (!isPersistent) {
            LOGGER.debug("Creating in memory queue");
            queue = ObjectQueue.createInMemory();
        } else {
            LOGGER.debug("Creating file queue");
            String path = config.getString("pirg.queuepath", null);
            if (path == null) {
                LOGGER.error("Persistent mode is configured but no path is set (check configuration 'pirg.queuepath')");
                return false;
            }
            File file = new File(path);
            QueueFile queueFile;
            try {
                queueFile = new QueueFile.Builder(file).build();
            } catch (IOException e) {
                LOGGER.error("Error while building persistent queue on path={}. Error: {}", path, e);
                return false;
            }
            queue = ObjectQueue.create(queueFile, new GsonConverter(new Gson(), Message.class));


        }

        sendInterval = config.getInt("pirg.sendinterval", 10);
        sendInterval *= 1000;

        sendSize = config.getInt("pirg.sendsize", 100);

        return true;
    }

    public void start() {
        if(!this.started) {
            this.senderThread.start();
            started = true;
        }
    }

    public void send(Message msg) {
        try {
            LOGGER.debug("Adding message to queue (id={}, queuesize={})", msg.getId(),
                (queue.size() + 1));
            queue.add(msg);
        } catch (IOException e) {
            LOGGER.error("Could not add message to queue (id={}). Error: ", msg.getId(), e);
        }
    }

    private class SenderThread extends Thread {

        private OkHttpClient okHttpClient = new OkHttpClient();


        public void send(Message msg) {
            RequestBody body = RequestBody.create(JSON, msg.getBody());
            Headers headers = Headers.of(msg.getHeaders());

            String targetUrl = msg.getUrl() != null ? msg.getUrl() : url;
            if (targetUrl == null || targetUrl.trim().isEmpty()) {
                LOGGER.error("No url is configured. Could not send Message (id={})", msg.getId());
                return;
            }

            try {
                Request request;
                // TODO handle other http metods
                if(msg.getHttpMethod().equalsIgnoreCase(Message.HTTP_METHOD_GET)) {
                    request = new Request.Builder().headers(headers).url(targetUrl).get().build();
                } else {
                    request = new Request.Builder().headers(headers).url(targetUrl).post(body).build();
                }
                Response response = okHttpClient.newCall(request).execute();
                if(response.code() != 200) {
                    LOGGER.error("Response is not 200. Response={}", response);
                }
            } catch (Exception e) {
                try {
                    queue.add(msg);
                } catch (Exception e1) {
                    LOGGER.error("Could not add message to queue (id={}). Error: ", msg.getId(), e1);
                }
                LOGGER.error("Error while sending message (id={}). "
                    + "Adding message back to queue. Error: {}", msg.getId(), e.getMessage());
            }
        }

        @Override
        public void run() {
            LOGGER.info("Running SenderThread..");
            while (true) {
                try {
                    LOGGER.debug("Fetching messages from queue");
                    List<Message> messages = queue.peek(sendSize);
                    LOGGER.debug("Sending {} messages to server.", messages.size());
                    // TODO combine the messages and send them as a package in one http call
                    //  (how to handle the response for each message?)
                    messages.forEach(this::send);
                    queue.remove(messages.size());
                    LOGGER.debug("Removing {} messages from the queue.", messages.size());
                } catch (Exception e) {
                    LOGGER.error("Error while fetching messages form the queue: {}", e.getMessage());
                }

                try {
                    Thread.sleep(sendInterval);
                } catch (InterruptedException e) {
                }
            }
        }
    }

    /**
     * Converter which uses GSON to serialize instances of class T to disk.
     */
    public class GsonConverter<T> implements Converter<T> {

        private final Gson gson;
        private final Class<T> type;

        public GsonConverter(Gson gson, Class<T> type) {
            this.gson = gson;
            this.type = type;
        }

        @Override
        public T from(byte[] bytes) {
            Reader reader = new InputStreamReader(new ByteArrayInputStream(bytes));
            return gson.fromJson(reader, type);
        }

        @Override
        public void toStream(T object, OutputStream bytes) throws IOException {
            Writer writer = new OutputStreamWriter(bytes);
            gson.toJson(object, writer);
            writer.close();
        }
    }
}