/*
 * Copyright (c) 2022, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package main;

import initializer.LoggerInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rendering.Engine;
import simulation.Simulation;

public class Launcher {

  private static final Logger LOGGER = LoggerFactory.getLogger(Launcher.class);

  public static void main(String[] args) {
    init();
    LOGGER.info("Started at {} ms", System.currentTimeMillis());
    try {
      Simulation logic = new Simulation();
      Engine engine = new Engine("Life Simulator", 1920, 1080, logic);
      engine.run();
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(-1);
    }
  }

  public static void init() {
    LoggerInitializer.initialize();
  }
}
