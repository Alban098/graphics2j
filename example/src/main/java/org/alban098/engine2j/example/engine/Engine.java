/*
 * Copyright (c) 2022-2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package org.alban098.engine2j.example.engine;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;

import org.alban098.engine2j.common.Cleanable;
import org.alban098.engine2j.common.Window;
import org.alban098.engine2j.common.utils.Timer;
import org.alban098.engine2j.input.MouseInputManager;

/**
 * This class is the entry point of the Rendering engine, it will run a {@link Logic}, update and
 * render everything inside at the right time and handle user inputs
 */
public final class Engine implements Runnable, Cleanable {

  /** The Window the Engine runs in */
  private final Window window;
  /** A Timer used to sync the Engine with the target FPS */
  private final Timer timer;
  /** The {@link Logic} ran by the Engine */
  private final Logic logic;
  /** The Manager responsible for mouse input capture */
  private final MouseInputManager mouseInputManager;
  /** The Rendering Options of the Engine */
  private final Options renderingOptions;
  /** The duration of the last frame in seconds */
  private double lastFrameTime;
  /** The number of update during the current frame */
  private double nbUpdate;

  /**
   * Create a new instance of an Engine
   *
   * @param windowTitle the Window title
   * @param width window width in pixels
   * @param height window height in pixels
   * @param logic the Logic to run
   * @param options options to initialize the Engine from
   */
  public Engine(String windowTitle, int width, int height, Logic logic, Options options) {
    this.window = new Window(windowTitle, width, height);
    this.mouseInputManager = new MouseInputManager();
    this.logic = logic;
    this.timer = new Timer();
    this.renderingOptions = options;
  }

  /** The core code of the engine initialize window and all then run the game loop */
  @Override
  public void run() {
    init();
    loop();
    cleanUp();
  }

  /** Initialize the Engine */
  private void init() {
    window.init();
    timer.init();
    mouseInputManager.linkCallbacks(window);
    logic.initInternal(window, this);
  }

  /** The main Engine loop */
  private void loop() {
    double accumulator = 0f;
    double interval;

    // While the engine is running
    while (!window.windowShouldClose()) {
      glClear(GL_COLOR_BUFFER_BIT);

      window.newFrame();

      // Calculate an update duration and get the elapsed time since last loop
      interval = 1f / renderingOptions.targetTps;
      lastFrameTime = timer.getElapsedTime();
      accumulator += lastFrameTime;

      // Handle user inputs
      input();
      nbUpdate = 0;
      // Update the logic as many times as needed to respect the number of updates per second
      while (accumulator >= interval) {
        logic.process(interval);
        accumulator -= interval;
        nbUpdate++;
      }

      render();

      // Draw the frame
      window.endFrame();

      sync();
    }
  }

  /** Cleanup the Engine and its modules from memory */
  @Override
  public void cleanUp() {
    mouseInputManager.cleanUp();
    logic.cleanUp();
    window.cleanUp();
  }

  /** Sync the framerate with {@link Options#targetFps} */
  private void sync() {
    float loopSlot = 1f / renderingOptions.targetFps;
    double endTime = timer.getLastFrameTime() + loopSlot;
    while (timer.getTime() < endTime) {
      try {
        Thread.sleep(1);
      } catch (InterruptedException ignored) {
      }
    }
  }

  /** Handle user inputs */
  private void input() {
    mouseInputManager.update();
    logic.input();
  }

  /** Render the frame, called once every frame */
  private void render() {
    logic.render();
  }

  /**
   * Returns the duration of the last frame in seconds
   *
   * @return the duration of the last frame in seconds
   */
  public double getFrameTime() {
    return lastFrameTime;
  }

  /**
   * Returns the {@link Logic} currently ran by the Engine
   *
   * @return the {@link Logic} currently ran by the Engine
   */
  public Logic getLogic() {
    return logic;
  }

  /**
   * Returns the average TPS over the current frame
   *
   * @return the average TPS over the current frame
   */
  public double getTPS() {
    return nbUpdate / lastFrameTime;
  }

  /**
   * Returns the {@link Options} of the {@link Engine}
   *
   * @return the {@link Options} of the {@link Engine}
   */
  public Options getOptions() {
    return renderingOptions;
  }

  /**
   * Returns the linked {@link MouseInputManager}
   *
   * @return the linked {@link MouseInputManager}
   */
  public MouseInputManager getMouseInputManager() {
    return mouseInputManager;
  }

  /** Just a wrapper class providing configuration for the {@link Engine} */
  public static final class Options {

    /** The number of frames per seconds the {@link Engine} aims to run at */
    private final int targetFps;
    /** The number of updates per seconds the {@link Engine} aims to run at */
    private final int targetTps;

    /**
     * Initialize a new instance of Options
     *
     * @param targetFps the number of frames per seconds the {@link Engine} aims to run at
     * @param targetTps the number of updates per seconds the {@link Engine} aims to run at
     */
    public Options(int targetFps, int targetTps) {
      this.targetFps = targetFps;
      this.targetTps = targetTps;
      if (targetTps < targetFps) {
        throw new IllegalArgumentException(
            "TPS must be greater than FPS, (TPS:" + targetTps + ", FPS" + targetFps + ")");
      }
    }

    /**
     * Returns the target FPS of the {@link Engine}
     *
     * @return the target FPS of the {@link Engine}
     */
    public int getTargetFps() {
      return targetFps;
    }

    /**
     * Returns the target TPS of the {@link Engine}
     *
     * @return the target TPS of the {@link Engine}
     */
    public int getTargetTps() {
      return targetTps;
    }
  }
}
