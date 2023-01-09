/*
 * Copyright (c) 2022-2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering;

import static org.lwjgl.glfw.GLFW.*;

import org.joml.Vector2f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rendering.interfaces.InterfaceManager;
import rendering.renderers.RenderingMode;
import rendering.scene.Camera;
import rendering.scene.Scene;

/** This class implements base methods of a Logic that can be run by the engine */
public abstract class AbstractLogic implements ILogic {

  private static final Logger LOGGER = LoggerFactory.getLogger(AbstractLogic.class);

  /** The {@link Camera} used to render the Scene */
  protected final Camera camera;
  /** The {@link Engine} running the Logic */
  protected Engine engine;
  /** The {@link Scene} of the Logic */
  protected Scene scene;
  /** The current state of the mouse inputs */
  protected MouseInput mouseInput;
  /**
   * The {@link InterfaceManager} managing all {@link rendering.interfaces.UserInterface}s of the
   * Logic
   */
  protected InterfaceManager interfaceManager;

  /** A flag indicating if the Logic is paused or not */
  private boolean paused = false;

  /** Create a new Logic Initialize Camera and Renderer */
  public AbstractLogic() {
    camera = new Camera(new Vector2f());
  }

  /**
   * Initialize the Logic
   *
   * @param window the Window to render to
   * @param engine the Engine running the logic
   * @param mouseInput the mouse input to link
   */
  @Override
  public void init(Window window, Engine engine, MouseInput mouseInput) {
    this.engine = engine;
    this.mouseInput = mouseInput;
    camera.adjustProjection(window.getAspectRatio());
    scene = new Scene(engine.getRenderer());
    interfaceManager = new InterfaceManager(engine.getRenderer(), mouseInput);
  }

  /**
   * Methods used to check to user inputs
   *
   * @param window the Window where the scene is renderer to
   */
  @Override
  public void input(Window window) {
    if (window.isKeyPressed(GLFW_KEY_UP)) {
      engine.getRenderer().setRenderingMode(RenderingMode.FILL);
    }
    if (window.isKeyPressed(GLFW_KEY_DOWN)) {
      engine.getRenderer().setRenderingMode(RenderingMode.WIREFRAME);
    }
    interfaceManager.processUserInput();
  }

  /**
   * Update the Camera's position and scale
   *
   * @param window the windows where the scene is rendered to
   * @param mouseInput the MouseInput to use for camera rotation
   */
  @Override
  public void updateCamera(Window window, MouseInput mouseInput) {
    if (window.isResized()) {
      camera.adjustProjection(window.getAspectRatio());
    }

    if (mouseInput.canTakeControl(camera)) {
      if (mouseInput.isLeftButtonPressed()) {
        mouseInput.halt(camera);
        Vector2f pan =
            mouseInput.getDisplacementVector().div(window.getHeight()).mul(camera.getZoom());
        pan.x = -pan.x;
        camera.move(pan);
      }

      if (mouseInput.isRightButtonPressed()) {
        mouseInput.halt(camera);
        float rotation = mouseInput.getDisplacementVector().y;
        camera.rotate((float) (rotation / Math.PI / 128f));
      }

      if (mouseInput.getScrollOffset() != 0) {
        mouseInput.halt(camera);
        camera.zoom(1 - mouseInput.getScrollOffset() / 10);
      }
    }
    if (mouseInput.hasControl(camera)
        && !mouseInput.isLeftButtonPressed()
        && !mouseInput.isRightButtonPressed()
        && mouseInput.getScrollOffset() == 0) {
      mouseInput.release();
    }
  }

  /**
   * Update the simulation, called once every (1/TARGET_UPS sec)
   *
   * @param window the Window where the simulation is rendered
   * @param elapsedTime time elapsed since last update in seconds
   */
  @Override
  public final void process(Window window, double elapsedTime) {
    // If the simulation is running, update all objects
    if (!paused) {
      prepare(window, elapsedTime);
      update(window, elapsedTime);
      finalize(window, elapsedTime);
    }
  }

  /**
   * Called before all the scene element will be updated, may be called multiple time per frame
   * Entities and components are automatically updated after this call
   *
   * @param window the Window where the simulation is rendered
   * @param elapsedTime time elapsed since last update in seconds
   */
  protected abstract void prepare(Window window, double elapsedTime);

  /**
   * Update all element of the scene, is called between {@link AbstractLogic#prepare(Window,
   * double)} and {@link AbstractLogic#finalize(Window, double)} is called once every 1/{@link
   * Engine#TARGET_TPS}s
   *
   * @param window the Window where the simulation is rendered
   * @param elapsedTime time elapsed since last update in seconds
   */
  protected abstract void update(Window window, double elapsedTime);

  /**
   * Called after all the scene element have been updated, may be called multiple time per frame
   * Entities and components are automatically updated before this call
   *
   * @param window the Window where the simulation is rendered
   * @param elapsedTime time elapsed since last update in seconds
   */
  protected abstract void finalize(Window window, double elapsedTime);

  /** Pause the simulation */
  @Override
  public void pause() {
    LOGGER.debug("Logic paused");
    paused = true;
  }

  /** Resume the simulation */
  @Override
  public void resume() {
    LOGGER.debug("Logic resumed");
    paused = false;
  }

  /** Clear the memory used by the scene, and it's meshes */
  @Override
  public void cleanUp() {
    scene.cleanUp();
    interfaceManager.cleanUp();
  }

  /**
   * Returns the {@link Scene} of the Logic
   *
   * @return the {@link Scene} of the Logic
   */
  @Override
  public Scene getScene() {
    return scene;
  }

  /**
   * Returns the {@link Camera} of the Logic
   *
   * @return the {@link Camera} of the Logic
   */
  @Override
  public Camera getCamera() {
    return camera;
  }
}
