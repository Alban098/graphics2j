/*
 * Copyright (c) 2022-2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering;

import static org.lwjgl.opengl.GL11.*;

import java.util.Collection;
import rendering.debug.Debugger;
import rendering.debug.ImGuiLayer;
import rendering.fonts.FontManager;
import rendering.renderers.RegisterableRenderer;
import rendering.renderers.Renderer;
import rendering.renderers.RendererManager;
import rendering.scene.entities.Entity;

/**
 * This class is the entry point of the Rendering engine, it will run a {@link Logic}, update and
 * render everything inside at the right time and handle user inputs
 */
public final class Engine implements Runnable {

  /** The Window the Engine runs in */
  private final Window window;
  /** A Timer used to sync the Engine with the target FPS */
  private final Timer timer;
  /** The {@link Logic} ran by the Engine */
  private final Logic logic;
  /** A Wrapper containing the state of the mouse */
  private final MouseInput mouseInput;
  /** The Manager responsible for all {@link Renderer}s of the Engine */
  private final RendererManager rendererManager;
  /** The Rendering Options of the Engine */
  private final Options renderingOptions;
  /** The Layer responsible for rendering the Debugger */
  private ImGuiLayer debugLayers;
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
   * @param gameLogic the Logic to run
   */
  public Engine(String windowTitle, int width, int height, Logic gameLogic, Options options) {
    this.window = new Window(windowTitle, width, height);
    this.mouseInput = new MouseInput();
    this.logic = gameLogic;
    this.timer = new Timer();
    this.rendererManager = new RendererManager();
    this.renderingOptions = options;
    if (this.renderingOptions.debug) {
      this.logic.initDebugger();
    }
  }

  /** The core code of the engine initialize window and all then run the game loop */
  @Override
  public void run() {
    init();
    loop();
    cleanup();
  }

  /** Initialize the Engine */
  private void init() {
    window.init();
    FontManager.registerFont("Candara");
    FontManager.registerFont("Calibri");
    FontManager.registerFont("Arial");
    timer.init();
    mouseInput.linkCallbacks(window);
    rendererManager.init(window);
    logic.initInternal(window, this);
    if (this.renderingOptions.debug) {
      debugLayers = new Debugger(this);
    }
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
        update(interval);
        accumulator -= interval;
        nbUpdate++;
      }

      render();
      if (renderingOptions.debug && debugLayers != null) {
        debugLayers.render();
      }

      // Draw the frame
      window.endFrame();

      sync();
    }
  }

  /** Cleanup the Engine and its modules from memory */
  private void cleanup() {
    mouseInput.cleanUp();
    logic.cleanUp();
    rendererManager.cleanUp();
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
    mouseInput.update();
    logic.input();
  }

  /**
   * Update the Logic, called once every update
   *
   * @param interval elapsed time in seconds
   */
  private void update(double interval) {
    logic.getScene().updateInterfaces(interval);
    logic.process(interval);
    logic.getScene().end();
  }

  /** Render the frame, called once every frame */
  private void render() {
    logic.getScene().updateCamera(window, mouseInput);
    rendererManager.render(window, logic.getScene());
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
   * Returns the {@link RendererManager} of the Engine
   *
   * @return the {@link RendererManager} of the Engine
   */
  public RendererManager getRendererManager() {
    return rendererManager;
  }

  /**
   * Maps a {@link Renderer} with a type of {@link Entity}
   *
   * @param type the class type of {@link Entity}
   * @param renderer the {@link Renderer} to link
   * @param <T> the type of {@link Entity}
   */
  public <T extends Entity> void mapEntityRenderer(
      Class<T> type, RegisterableRenderer<T> renderer) {
    this.rendererManager.mapEntityRenderer(type, renderer);
  }

  /**
   * Retrieves the {@link Renderer} associated with a type of {@link Entity}
   *
   * @param type the class type of {@link Entity} to retrieve the {@link Renderer} of
   * @return the {@link Renderer} associated with the type, null if not registered
   * @param <T> the type of {@link Entity}
   */
  public <T extends Entity> RegisterableRenderer<T> getRendererManager(Class<T> type) {
    return (RegisterableRenderer<T>) rendererManager.getRenderer(type);
  }

  /**
   * Returns a Collection of all {@link Renderer}s ran by the Engine
   *
   * @return a Collection of all {@link Renderer}s ran by the Engine
   */
  public Collection<Renderer> getRenderers() {
    return rendererManager.getRenderers();
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
   * Returns the linked {@link MouseInput}
   *
   * @return the linked {@link MouseInput}
   */
  public MouseInput getMouseInput() {
    return mouseInput;
  }

  /** Just a wrapper class providing configuration for the {@link Engine} */
  public static final class Options {

    /** A flag indicating if the {@link Engine} need debugging capabilities */
    private final boolean debug;
    /** The number of frames per seconds the {@link Engine} aims to run at */
    private final int targetFps;
    /** The number of updates per seconds the {@link Engine} aims to run at */
    private final int targetTps;

    /**
     * Initialize a new instance of Options
     *
     * @param debug does the {@link Engine} need debugging capabilities
     * @param targetFps the number of frames per seconds the {@link Engine} aims to run at
     * @param targetTps the number of updates per seconds the {@link Engine} aims to run at
     */
    public Options(boolean debug, int targetFps, int targetTps) {
      this.debug = debug;
      this.targetFps = targetFps;
      this.targetTps = targetTps;
      if (targetTps < targetFps) {
        throw new IllegalArgumentException(
            "TPS must be greater than FPS, (TPS:" + targetTps + ", FPS" + targetFps + ")");
      }
    }

    /**
     * Returns whether the {@link Engine} has debugging capabilities or not
     *
     * @return whether the {@link Engine} has debugging capabilities or not
     */
    public boolean isDebug() {
      return debug;
    }

    /**
     * Returns the target FPS of the {@link Engine
     *
     * @return the target FPS of the {@link Engine}
     */
    public int getTargetFps() {
      return targetFps;
    }

    /**
     * Returns the target TPS of the {@link Engine
     *
     * @return the target TPS of the {@link Engine}
     */
    public int getTargetTps() {
      return targetTps;
    }
  }
}
