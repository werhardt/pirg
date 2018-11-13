package pirg;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


public class Message {

   public static final int HTTP_METHOD_GET = 0;
   public static final int HTTP_METHOD_POST = 1;
   public static final int HTTP_METHOD_PUT = 2;
   public static final int HTTP_METHOD_DELETE = 3;

   private Map<String, String> headers = new HashMap<>();
   private int httpMethod = HTTP_METHOD_POST;
   private String body;
   private String url;
   private String id;

   public Map<String, String> getHeaders() {
      return headers;
   }

   public void setHeaders(Map<String, String> headers) {
      this.headers = headers;
   }

   public int getHttpMethod() {
      return httpMethod;
   }

   public void setHttpMethod(int httpMethod) {
      this.httpMethod = httpMethod;
   }

   public String getBody() {
      return body;
   }

   public void setBody(String body) {
      this.body = body;
   }

   public String getUrl() {
      return url;
   }

   public void setUrl(String url) {
      this.url = url;
   }

   public String getId() {
      return this.id;
   }

   /******** BUILDER Methods ********/

   public static Message build() {
      Message msg = new Message();
      msg.id = UUID.randomUUID().toString();
      return msg;
   }

   public Message header(String key, String value) {
      this.headers.put(key, value);
      return this;
   }

   public Message body(String body) {
      this.body = body;
      return this;
   }

   public Message method(int httpMethod) {
      this.httpMethod = httpMethod;
      return this;
   }

   public Message url(String url) {
      this.url = url;
      return this;
   }

}
