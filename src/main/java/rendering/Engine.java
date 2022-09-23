/*
 * Copyright (c) 2022, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;

public class Engine implements Runnable {

  private static final int TARGET_FPS = 60;
  private static final int TARGET_UPS = 120;
  private final Window window;

  private final Timer timer;
  private final ILogic gameLogic;
  private final MouseInput mouseInput;

  /**
   * Create a new instance of an Engine
   *
   * @param windowTitle the Window title
   * @param width window width in pixels
   * @param height window height in pixels
   * @param gameLogic the ILogic to run
   */
  public Engine(String windowTitle, int width, int height, ILogic gameLogic) {
    window = new Window(windowTitle, width, height);
    mouseInput = new MouseInput();
    this.gameLogic = gameLogic;
    timer = new Timer();
  }

  /** The core code of the engine initialize window and all then run the game loop */
  @Override
  public void run() {
    try {
      init();
      loop();
    } catch (Exception ex) {
      ex.printStackTrace();
    } finally {
      cleanup();
    }
  }

  /**
   * Initialize the Engine
   *
   * @throws Exception if the ILogic fail to initialize
   */
  protected void init() throws Exception {
    window.init();
    timer.init();
    mouseInput.init(window);
    gameLogic.init(window);
  }

  /** The main Engine loop */
  protected void loop() {
    double elapsedTime;
    double accumulator = 0f;
    double interval;

    // While the engine is running
    while (!window.windowShouldClose()) {
      glClear(GL_COLOR_BUFFER_BIT);
      // Calculate an update duration and get the elapsed time since last loop
      interval = 1f / TARGET_UPS;
      elapsedTime = timer.getElapsedTime();
      accumulator += elapsedTime;

      // Handle user inputs
      input();

      // Update the logic as many times as needed to respect the number of updates per second
      while (accumulator >= interval) {
        update(interval);
        accumulator -= interval;
      }

      render();

      // Draw the frame
      window.update();

      sync();
    }
  }

  /** Cleanup the Engine and its modules from memory */
  protected void cleanup() {
    gameLogic.cleanup();
  }

  /** Sync the framerate with TARGET_FPS */
  private void sync() {
    float loopSlot = 1f / TARGET_FPS;
    double endTime = timer.getLastLoopTime() + loopSlot;
    while (timer.getTime() < endTime) {
      try {
        Thread.sleep(1);
      } catch (InterruptedException ignored) {
      }
    }
  }

  /** Handle user inputs */
  protected void input() {
    mouseInput.input(window);
    gameLogic.input(window, mouseInput);
  }

  /**
   * Update the ILogic, called once every update
   *
   * @param interval elapsed time in seconds
   */
  protected void update(double interval) {
    gameLogic.process(window, interval);
  }

  /** Render the frame, called once every frame */
  protected void render() {
    gameLogic.updateCamera(window, mouseInput);
    gameLogic.render(window);
  }

  /** Reset the frame timer */
  public void resetFrameTimer() {
    timer.getElapsedTime();
  }
}
