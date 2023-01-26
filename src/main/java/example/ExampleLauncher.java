/*
 * Copyright (c) 2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package example;

import org.alban098.engine2j.core.Engine;
import org.apache.log4j.PropertyConfigurator;

public class ExampleLauncher {

  public static void main(String[] args) {
    PropertyConfigurator.configure("log4j.properties");
    new Engine("example", 640, 480, new ExampleLogic(), new Engine.Options(false, 60, 120)).run();
  }
}
