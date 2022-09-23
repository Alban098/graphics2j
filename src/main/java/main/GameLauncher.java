/*
 * Copyright (c) 2022, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package main;

import initializer.LoggerInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rendering.Window;

public class GameLauncher {

  private static final Logger LOGGER = LoggerFactory.getLogger(GameLauncher.class);

  public static void main(String[] args) {
    init();
    LOGGER.info("Started at {} ms", System.currentTimeMillis());
    Window window = new Window();
    window.init();
    window.run();
  }

  public static void init() {
    LoggerInitializer.initialize();
  }
}
