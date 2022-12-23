/*
 * Copyright (c) 2022, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering;

import static org.lwjgl.glfw.GLFW.*;

import org.joml.Vector2f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rendering.renderers.RenderingMode;
import rendering.scene.Camera;
import rendering.scene.Scene;

/** This class implements base methods of a Logic that can be run by the engine */
public abstract class AbstractLogic implements ILogic {

  private static final Logger LOGGER = LoggerFactory.getLogger(AbstractLogic.class);

  protected final Camera camera;

  protected Engine engine;
  protected Scene scene;

  private boolean paused = false;

  /** Create a new Logic Initialize Camera and Renderer */
  public AbstractLogic() {
    camera = new Camera(new Vector2f());
  }

  /**
   * Initialize the Logic by creating the scene, the lights and the skybox
   *
   * @param window the Window to render to
   * @param engine the Engine running the logic
   * @throws Exception thrown if the skybox model or texture couldn't be loaded
   */
  @Override
  public void init(Window window, Engine engine) throws Exception {
    this.engine = engine;
    camera.adjustProjection(window.getAspectRatio());
    scene = new Scene(engine.getRenderer());
  }

  /**
   * Methods used to check to user inputs
   *
   * @param window the Window where the scene is renderer to
   * @param mouseInput the MouseInput containing cursor information
   */
  @Override
  public void input(Window window, MouseInput mouseInput) {
    if (window.isKeyPressed(GLFW_KEY_UP)) {
      engine.getRenderer().setRenderingMode(RenderingMode.FILL);
    }
    if (window.isKeyPressed(GLFW_KEY_DOWN)) {
      engine.getRenderer().setRenderingMode(RenderingMode.WIREFRAME);
    }
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

    if (mouseInput.isLeftButtonPressed()) {
      Vector2f pan =
          mouseInput.getDisplacementVector().div(window.getHeight()).mul(camera.getZoom());
      pan.x = -pan.x;
      camera.move(pan);
    }

    if (mouseInput.isRightButtonPressed()) {
      float rotation = mouseInput.getDisplacementVector().y;
      camera.rotate((float) (rotation / Math.PI / 128f));
    }

    if (mouseInput.getScrollOffset() != 0) {
      camera.zoom(1 - mouseInput.getScrollOffset() / 10);
    }
  }

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
  }

  @Override
  public Scene getScene() {
    return scene;
  }

  @Override
  public Camera getCamera() {
    return camera;
  }
}
