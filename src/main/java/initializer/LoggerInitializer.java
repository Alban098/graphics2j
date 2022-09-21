/*
 * Copyright (c) 2022, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package initializer;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.PatternLayout;

public class LoggerInitializer {

  private LoggerInitializer() {}

  public static void initialize() {
    BasicConfigurator.configure(
        new ConsoleAppender(
            new PatternLayout("%d{dd-MM-yyyy HH:mm:ss} - [%p] - [%t] - %c %M() : %m%n")));
  }
}
