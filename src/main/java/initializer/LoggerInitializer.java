/*
 * Copyright (c) 2022, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package initializer;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.PatternLayout;

/** Just an utility class to initialize the logging library */
public class LoggerInitializer {

  /** Private constructor because the class can't be instantiated */
  private LoggerInitializer() {}

  /** Initialize Reload4J */
  public static void initialize() {
    BasicConfigurator.configure(
        new ConsoleAppender(
            new PatternLayout("%d{dd-MM-yyyy HH:mm:ss} - [%p] - [%t] - %c %M() : %m%n")));
  }
}
