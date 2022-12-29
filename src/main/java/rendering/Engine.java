/*
 * Copyright (c) 2022, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering;

import static org.lwjgl.opengl.GL11.*;

import java.util.Collection;
import rendering.debug.Debugger;
import rendering.debug.ImGuiLayer;
import rendering.entities.Entity;
import rendering.renderers.MasterRenderer;
import rendering.renderers.Renderable;
import rendering.renderers.Renderer;

public class Engine implements Runnable {

  public static final int TARGET_FPS = 100;
  public static final int TARGET_TPS = 200;
  private final Window window;
  private final Timer timer;
  private final ILogic gameLogic;
  private final MouseInput mouseInput;
  private final MasterRenderer renderer;
  private final boolean debug;
  private ImGuiLayer layer;
  private double lastFrameTime;
  private double nbUpdate;

  /**
   * Create a new instance of an Engine
   *
   * @param windowTitle the Window title
   * @param width window width in pixels
   * @param height window height in pixels
   * @param gameLogic the ILogic to run
   */
  public Engine(String windowTitle, int width, int height, ILogic gameLogic, boolean debug) {
    this.window = new Window(windowTitle, width, height);
    this.mouseInput = new MouseInput();
    this.gameLogic = gameLogic;
    this.timer = new Timer();
    this.renderer = new MasterRenderer();
    this.debug = debug;
    if (debug) {
      this.gameLogic.initDebugger();
    }
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
    mouseInput.linkCallbacks(window);
    renderer.init(window);
    gameLogic.init(window, this);
    if (debug) {
      layer = new Debugger(this);
    }
  }

  /** The main Engine loop */
  protected void loop() {
    double accumulator = 0f;
    double interval;

    // While the engine is running
    while (!window.windowShouldClose()) {
      glClear(GL_COLOR_BUFFER_BIT);

      window.newFrame();

      // Calculate an update duration and get the elapsed time since last loop
      interval = 1f / TARGET_TPS;
      lastFrameTime = timer.getElapsedTime();
      accumulator += lastFrameTime;

      // Handle user inputs
      input();
      nbUpdate = 0;
      // Update the logic as many times as needed to respect the number of updates per second
      while (accumulator >= interval) {
        update(interval);
        accumulator -= interval;
        nbUpdate++;
      }

      render();
      if (layer != null) {
        layer.render();
      }

      // Draw the frame
      window.endFrame();

      sync();
    }
  }

  /** Cleanup the Engine and its modules from memory */
  protected void cleanup() {
    mouseInput.cleanUp();
    gameLogic.cleanUp();
    renderer.cleanUp();
    window.cleanUp();
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
    mouseInput.update();
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
    renderer.render(window, gameLogic);
  }

  public double getFrameTime() {
    return lastFrameTime;
  }

  public MasterRenderer getRenderer() {
    return renderer;
  }

  public <T extends Entity> void mapEntityRenderer(Class<T> type, Renderer<T> renderer) {
    this.renderer.mapEntityRenderer(type, renderer);
  }

  public <T extends Entity> Renderer<T> getRenderer(Class<T> type) {
    return (Renderer<T>) renderer.getRenderer(type);
  }

  public Collection<Renderer<? extends Renderable>> getRenderers() {
    return renderer.getRenderers();
  }

  public ILogic getLogic() {
    return gameLogic;
  }

  public double getNbUpdates() {
    return nbUpdate / lastFrameTime;
  }
}
