/*
 * Copyright (c) 2022, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering;

/** This class represent an accurate timer used to calculate update and frame times */
public class Timer {

  private double lastLoopTime;

  /** Initialize the timer */
  public void init() {
    lastLoopTime = getTime();
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
    double elapsedTime = (time - lastLoopTime);
    lastLoopTime = time;
    return elapsedTime;
  }

  /**
   * Get the time of the last call to getElapsedTime() in seconds
   *
   * @return the time of the last call to getElapsedTime() in seconds
   */
  public double getLastLoopTime() {
    return lastLoopTime;
  }
}
