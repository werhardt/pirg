package pirg;

import java.io.IOException;

import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Sends data to a configured server. 
 * 
 * @author Waldemar Erhardt
 *
 */
public class Sender {

   public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

   private OkHttpClient client = new OkHttpClient();

   private String url;

   public Sender() {
   }

   public Sender(String url) {
      this.url = url;
   }

   public Response send(Message msg) throws IOException {
      RequestBody body = RequestBody.create(JSON, msg.getBody());
      Headers headers = Headers.of(msg.getHeaders());

      Request request = new Request.Builder().headers(headers).url(msg.getUrl() != null ? msg.getUrl() : this.url).post(body).build();

      Response response = client.newCall(request).execute();
      return response;
   }
}
