/*
 * Copyright (c) 2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package example;

import org.alban098.engine2j.core.Engine;
import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExampleLauncher {

  private static final Logger LOGGER = LoggerFactory.getLogger(ExampleLauncher.class);

  public static void main(String[] args) {
    String log4jConfPath = "engine2j/log4j.properties";
    PropertyConfigurator.configure(log4jConfPath);
    LOGGER.info("Started at {} ms", System.currentTimeMillis());
    try {
      ExampleLogic logic = new ExampleLogic();
      Engine engine = new Engine("example", 640, 480, logic, new Engine.Options(false, 60, 120));
      engine.run();
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(-1);
    }
  }
}
