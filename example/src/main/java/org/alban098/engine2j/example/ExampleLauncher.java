/*
 * Copyright (c) 2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package org.alban098.engine2j.example;

import org.alban098.engine2j.engine.Engine;
import org.apache.log4j.PropertyConfigurator;

public class ExampleLauncher {
  public static void main(String[] args) {
    PropertyConfigurator.configure("./log4j.properties");
    new Engine("example", 1200, 600, new ExampleLogic(), new Engine.Options(60, 120)).run();
  }
}
