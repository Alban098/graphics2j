/*
 * Copyright (c) 2022, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package main;

import initializer.LoggerInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rendering.Renderer;

public class GameLauncher {

  private static final Logger LOGGER = LoggerFactory.getLogger(GameLauncher.class);

  public static void main(String[] args) {
    init();
    LOGGER.info("Started at {} ms", System.currentTimeMillis());
    Renderer renderer = new Renderer();
    renderer.init();
    renderer.run();
  }

  public static void init() {
    LoggerInitializer.initialize();
  }
}
