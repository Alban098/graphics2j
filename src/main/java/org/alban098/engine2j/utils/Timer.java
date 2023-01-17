/*
 * Copyright (c) 2022-2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package org.alban098.engine2j.utils;

/** This class represent an accurate timer used to calculate update and frame times */
public final class Timer {

  /** The time at which the last frame has ended */
  private double lastFrameTime;

  /** Initialize the timer */
  public void init() {
    lastFrameTime = getTime();
  }

  /**
   * Get the current tim in seconds, accurate to nanoseconds
   *
   * @return the current time in seconds
   */
  public double getTime() {
    return System.nanoTime() / 1_000_000_000.0;
  }

  /**
   * Get the elapsed time since the last call of this method
   *
   * @return elapsed time since last call, in seconds
   */
  public double getElapsedTime() {
    double time = getTime();
    double elapsedTime = (time - lastFrameTime);
    lastFrameTime = time;
    return elapsedTime;
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
