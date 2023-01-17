/*
 * Copyright (c) 2022-2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package org.alban098.engine2j.core;

import static org.lwjgl.glfw.GLFW.*;

import org.alban098.engine2j.renderers.RenderingMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** This class implements base methods of a Logic that can be run by the engine */
public abstract class Logic {

  private static final Logger LOGGER = LoggerFactory.getLogger(Logic.class);

  /** The {@link Engine} running the Logic */
  private Engine engine;
  /** The {@link Scene} of the Logic */
  private Scene scene;
  /** The {@link Window} the Logic runs in */
  private Window window;
  /** A flag indicating if the Logic is paused or not */
  private boolean paused = false;

  /** Create a new Logic Initialize Camera and Renderer */
  public Logic() {}

  /**
   * Initialize the Logic
   *
   * @param window the Window to render to
   * @param engine the Engine running the logic
   */
  final void initInternal(Window window, Engine engine) {
    this.engine = engine;
    this.window = window;
    scene = new Scene(engine.getRenderer(), window, engine.getMouseInputManager());
    this.init();
  }

  /** Methods used to check to user inputs */
  final void input() {
    if (window.isKeyPressed(GLFW_KEY_UP)) {
      engine.getRenderer().setRenderingMode(RenderingMode.FILL);
    }
    if (window.isKeyPressed(GLFW_KEY_DOWN)) {
      engine.getRenderer().setRenderingMode(RenderingMode.WIREFRAME);
    }
    scene.processUserInput();
  }

  /**
   * Update the simulation, called once every (1/TARGET_UPS sec)
   *
   * @param elapsedTime time elapsed since last update in seconds
   */
  final void process(double elapsedTime) {
    // If the simulation is running, update all objects
    if (!paused) {
      prepare(elapsedTime);
      update(elapsedTime);
      end(elapsedTime);
    }
  }

  /** Clear the memory used by the scene, and it's meshes */
  final void cleanUp() {
    scene.cleanUp();
  }

  /** Pause the simulation */
  public final void pause() {
    LOGGER.debug("Logic paused");
    paused = true;
  }

  /** Resume the simulation */
  public final void resume() {
    LOGGER.debug("Logic resumed");
    paused = false;
  }

  /**
   * Returns the {@link Scene} of the Logic
   *
   * @return the {@link Scene} of the Logic
   */
  public final Scene getScene() {
    return scene;
  }

  /**
   * Returns the {@link Engine} running the Logic
   *
   * @return the {@link Engine} running the Logic
   */
  public final Engine getEngine() {
    return engine;
  }

  /**
   * Returns the {@link Window} the Logic runs in
   *
   * @return the {@link Window} the Logic runs in
   */
  public final Window getWindow() {
    return window;
  }

  /** Initializes the debugger if debug is enabled */
  protected abstract void initDebugger();

  /**
   * Called after internal Logic initialization, allow for further initialization by derived classes
   */
  protected abstract void init();

  /**
   * Called before all the scene element will be updated, may be called multiple time per frame
   * Entities and components are automatically updated after this call
   *
   * @param elapsedTime time elapsed since last update in seconds
   */
  protected abstract void prepare(double elapsedTime);

  /**
   * Update all element of the scene, is called between {@link Logic#prepare( double)} and {@link
   * Logic#end(double)} is called once every 1/{@link Engine.Options#targetUps}
   *
   * @param elapsedTime time elapsed since last update in seconds
   */
  protected abstract void update(double elapsedTime);

  /**
   * Called after all the scene element have been updated, may be called multiple time per frame
   * Entities and components are automatically updated before this call
   *
   * @param elapsedTime time elapsed since last update in seconds
   */
  protected abstract void end(double elapsedTime);
}
