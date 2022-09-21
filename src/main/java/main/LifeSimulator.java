/*
 * Copyright (c) 2022, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package main;

import initializer.LoggerInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LifeSimulator {

  private static final Logger LOGGER = LoggerFactory.getLogger(LifeSimulator.class);

  public static void main(String[] args) {
    init();
    LOGGER.info("Started at {} ms", System.currentTimeMillis());
  }

  public static void init() {
    LoggerInitializer.initialize();
  }
}
