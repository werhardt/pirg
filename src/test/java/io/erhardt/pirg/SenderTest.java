package io.erhardt.pirg;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import okhttp3.Response;
import io.erhardt.pirg.Message;
import io.erhardt.pirg.Sender;
import io.erhardt.pirg.server.SampleServer;

public class SenderTest {

   private static final Logger LOGGER = LoggerFactory.getLogger(SenderTest.class);

   private static int PORT = 8086;
   private static String BASE_URL = "http://localhost:" + PORT;
   private static String URL = BASE_URL + "/pirg";

   private static SampleServer server = new SampleServer(PORT);

   @BeforeClass
   public static void startup() throws IOException {
      server.run();
   }

   @AfterClass
   public static void shutdown() {
      server.stop();
   }

   @Before
   public void before() {
      server.resetRequestCounter();
   }

   private void sleep(int seconds) {
      try {
         Thread.sleep(seconds * 1000);
      } catch (InterruptedException e) {
      }
   }

   @Test
   public void testSending() {

      Message msg = Message.build().url(URL).body("test");

      Sender sender = new Sender();
      sender.send(msg);
      this.sleep(3);
      assertEquals(1, server.getRequestCounter());
   }

   @Test
   public void testSendingMulti() {

      Sender sender = new Sender();
      for (int i = 0; i < 100; i++) {
         Message msg = Message.build().url(URL).body("test - " + i);
         sender.send(msg);
      }
      this.sleep(3);
      assertEquals(100, server.getRequestCounter());
   }
}
