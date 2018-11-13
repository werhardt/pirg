package pirg.server;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;

/**
 * Sample Server for testing purposes.
 * 
 * @author Waldemar Erhardt
 *
 */
public class SampleServer extends Dispatcher {

   private static final Logger LOGGER = LoggerFactory.getLogger(SampleServer.class);

   private MockWebServer server = new MockWebServer();

   private final int port;
   private int requestCount = 0;

   public SampleServer(int port) {
      this.port = port;
   }

   public void run() throws IOException {
      server.setDispatcher(this);
      server.start(port);
      LOGGER.info("Server - listening on port {}", this.port);
   }

   public void stop() {
      LOGGER.info("Server - stopping.. (handled {} requests)", this.requestCount);
      try {
         server.close();
         LOGGER.info("Server - stopped!");
      } catch (IOException e) {
         LOGGER.error("Error while stopping server.", e);
      }
   }

   @Override
   public MockResponse dispatch(RecordedRequest request) {

      this.requestCount++;
      String path = request.getPath();
      try {
         if (!path.startsWith("/") || path.contains(".."))
            throw new FileNotFoundException();

         String body = request.getBody().readUtf8();
         LOGGER.info("Server - body: {}", body);
         return createResponse(200, "ok");
      } catch (FileNotFoundException e) {
         return createResponse(404, "NOT FOUND: " + path);
      }
   }

   private MockResponse createResponse(int statusCode, String body) {
      return new MockResponse().setStatus("HTTP/1.1 " + statusCode).addHeader("content-type: text/plain; charset=utf-8").setBody(body);
   }

   public static void main(String[] args) throws Exception {

      SampleServer server = new SampleServer(8086);
      server.run();
   }

}