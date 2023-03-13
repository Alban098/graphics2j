/*
 * Copyright (c) 2022-2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package org.alban098.graphics2j.example;

/** This class represent an accurate timer used to calculate update and frame times */
public final class Timer {

  /** The time at which the last frame has ended */
  private long lastFrameTime;

  /**
   * Creates and initializes a new Timer
   */
  public Timer() {
    lastFrameTime = getTime();
  }

  /**
   * Get the current tim in seconds, accurate to nanoseconds
   *
   * @return the current time in seconds
   */
  public long getTime() {
    return System.nanoTime();
  }

  /**
   * Get the elapsed time since the last call of this method
   *
   * @return elapsed time since last call, in seconds
   */
  public double getElapsedTime() {
    long time = getTime();
    long elapsedTime = (time - lastFrameTime);
    lastFrameTime = time;
    return elapsedTime / 1_000_000_000.0;
  }

  /**
   * Get the time of the last call to getElapsedTime() in seconds
   *
   * @return the time of the last call to getElapsedTime() in seconds
   */
  public double getLastFrameTime() {
    return lastFrameTime;
  }
}
