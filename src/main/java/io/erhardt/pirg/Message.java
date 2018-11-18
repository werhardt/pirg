package io.erhardt.pirg;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


public class Message {

   public static final String HTTP_METHOD_GET = "GET";
   public static final String HTTP_METHOD_POST = "POST";
   public static final String HTTP_METHOD_PUT = "PUT";
   public static final String HTTP_METHOD_DELETE = "DELETE";

   private Map<String, String> headers = new HashMap<>();
   private String httpMethod = HTTP_METHOD_POST;
   private String body;
   private String url;
   private String id;
   private long timestamp;

   public Map<String, String> getHeaders() {
      return headers;
   }

   public void setHeaders(Map<String, String> headers) {
      this.headers = headers;
   }

   public String getHttpMethod() {
      return httpMethod;
   }

   public void setHttpMethod(String httpMethod) {
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

   public long getTimestamp() {
      return this.timestamp;
   }

   /******** BUILDER Methods ********/

   public static Message build() {
      Message msg = new Message();
      msg.id = UUID.randomUUID().toString();
      msg.timestamp = System.currentTimeMillis();
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

   public Message method(String httpMethod) {
      this.httpMethod = httpMethod;
      return this;
   }

   public Message url(String url) {
      this.url = url;
      return this;
   }

}
