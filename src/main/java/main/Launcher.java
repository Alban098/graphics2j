/*
 * Copyright (c) 2022-2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package main;

import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rendering.Engine;
import simulation.Simulation;

public class Launcher {

  private static final Logger LOGGER = LoggerFactory.getLogger(Launcher.class);

  public static void main(String[] args) {
    String log4jConfPath = "./log4j.properties";
    PropertyConfigurator.configure(log4jConfPath);
    LOGGER.info("Started at {} ms", System.currentTimeMillis());
    try {
      Simulation logic = new Simulation();
      Engine engine = new Engine("Engine ALPHA 0.1", 1280, 960, logic, true);
      engine.run();
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(-1);
    }
  }
}
