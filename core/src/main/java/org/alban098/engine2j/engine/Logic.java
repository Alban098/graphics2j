/*
 * Copyright (c) 2022-2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package org.alban098.engine2j.engine;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_DOWN;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_UP;

import org.alban098.engine2j.common.RenderingMode;
import org.alban098.engine2j.common.Window;
import org.alban098.engine2j.common.components.Camera;
import org.alban098.engine2j.entities.Entity;
import org.alban098.engine2j.entities.EntityRenderingManager;
import org.alban098.engine2j.fonts.FontManager;
import org.alban098.engine2j.interfaces.InterfaceRenderingManager;
import org.alban098.engine2j.interfaces.windows.UserInterface;
import org.joml.Vector2f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** This class implements base methods of a Logic that can be run by the engine */
public abstract class Logic {

  /** Just a Logger to log events */
  private static final Logger LOGGER = LoggerFactory.getLogger(Logic.class);

  /** The {@link Engine} running the Logic */
  private Engine engine;
  /** The {@link Window} the Logic runs in */
  private Window window;
  /** The Manager responsible for all {@link Entity}s of the Engine */
  protected EntityRenderingManager entityManager;
  /** The Manager responsible for all {@link UserInterface}s of the Engine */
  protected InterfaceRenderingManager interfaceManager;

  private Camera camera;
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
    this.entityManager = new EntityRenderingManager();
    this.interfaceManager = new InterfaceRenderingManager(window, engine.getMouseInputManager());
    this.camera = new Camera(window, new Vector2f());
    this.initFontManager();
    this.init();
  }

  /** Methods used to check to user inputs */
  final void input() {
    if (window.isKeyPressed(GLFW_KEY_UP)) {
      entityManager.setRenderingMode(RenderingMode.FILL);
    }
    if (window.isKeyPressed(GLFW_KEY_DOWN)) {
      entityManager.setRenderingMode(RenderingMode.WIREFRAME);
    }
    interfaceManager.processUserInput();
  }

  /**
   * Update the simulation, called once every (1/TARGET_UPS sec)
   *
   * @param elapsedTime time elapsed since last update in seconds
   */
  final void process(double elapsedTime) {
    // If the simulation is running, update all objects
    interfaceManager.update(elapsedTime);
    if (!paused) {
      prepare(elapsedTime);
      update(elapsedTime);
      end(elapsedTime);
    }
    interfaceManager.end();
  }

  /** Clear the memory used by the scene, and it's meshes */
  final void cleanUp() {
    entityManager.cleanUp();
    interfaceManager.cleanUp();
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

  public final void render() {
    camera.update(window, engine.getMouseInputManager());
    entityManager.render(window, camera);
    interfaceManager.render();
  }
  /**
   * Returns the {@link Camera} of the Logic
   *
   * @return the {@link Camera} of the Logic
   */
  public final Camera getCamera() {
    return camera;
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

  /**
   * Initializes the {@link FontManager} by registering necessary fonts, not that none are provided
   * by the Engine by default
   */
  protected abstract void initFontManager();

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
   * Logic#end(double)} is called once every 1/{@link Engine.Options#getTargetTps()}
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
