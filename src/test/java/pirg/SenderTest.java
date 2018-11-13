package pirg;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import okhttp3.Response;
import pirg.Message;
import pirg.Sender;
import pirg.server.SampleServer;

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

   @Test
   public void testSending() throws IOException {

      Message msg = Message.build().url(URL).body("test");

      Sender sender = new Sender();
      Response response = sender.send(msg);
      assertEquals(200, response.code());
      LOGGER.info("Client - response from server: {}", new String(response.body().bytes()));

   }

   @Test
   public void testSendingMulti() throws IOException {

      Sender sender = new Sender();
      for (int i = 0; i < 100; i++) {
         Message msg = Message.build().url(URL).body("test - " + i);
         Response response = sender.send(msg);
         assertEquals(200, response.code());
         LOGGER.info("Client - response from server: {}", new String(response.body().bytes()));
      }

   }
}
