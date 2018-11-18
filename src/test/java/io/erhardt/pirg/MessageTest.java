package io.erhardt.pirg;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class MessageTest {

   @Test
   public void testMessageBuilding() {
      String body = "Body of the message";
      String url = "https://test.url";
      String httpMethod = Message.HTTP_METHOD_PUT;
      String headerKey1 = "key1";
      String headerValue1 = "value1";
      String headerKey2 = "key2";
      String headerValue2 = "value2";
      Message msg = Message.build()
          .url(url)
          .method(httpMethod)
          .header(headerKey1, headerValue1)
          .header(headerKey2, headerValue2)
          .body(body);

      assertEquals(url, msg.getUrl());
      assertEquals(body, msg.getBody());
      assertEquals(httpMethod, msg.getHttpMethod());
      assertEquals(headerValue1, msg.getHeaders().get(headerKey1));
      assertEquals(headerValue2, msg.getHeaders().get(headerKey2));
   }
}
