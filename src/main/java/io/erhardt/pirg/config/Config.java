package io.erhardt.pirg.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.Properties;

/**
 * Helper class to read configurations from a file. 
 * 
 * @author Waldemar Erhardt
 *
 */
public class Config {
   
   private Properties props = new Properties();

   public Config(String configFile) throws Exception {
      URL url = this.getClass().getResource(configFile);
      if (url == null) {
         throw new FileNotFoundException("'" + configFile + "' not found.");
      }

      props.load(new FileInputStream(new File(url.toURI())));
   }

   public String getString(String key) {
      return this.props.getProperty(key);
   }

   public String getString(String key, String defaultValue) {
      return this.props.getProperty(key, defaultValue);
   }

   public int getInt(String key) {
      return Integer.valueOf(this.props.getProperty(key));
   }

   public int getInt(String key, int defaultValue) {
      return Integer.valueOf(this.props.getProperty(key, String.valueOf(defaultValue)));
   }

   public boolean getBoolean(String key) {
      return Boolean.valueOf(this.props.getProperty(key));
   }

   public boolean getBoolean(String key, boolean defaultValue) {
      return Boolean.valueOf(this.props.getProperty(key, String.valueOf(defaultValue)));
   }
}
