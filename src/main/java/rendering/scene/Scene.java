/*
 * Copyright (c) 2022-2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering.scene;

import java.util.*;
import org.joml.Vector2f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rendering.Logic;
import rendering.MouseInput;
import rendering.Window;
import rendering.interfaces.InterfaceManager;
import rendering.interfaces.UserInterface;
import rendering.renderers.RendererManager;
import rendering.scene.entities.Entity;

/** This class wraps everything that will be rendered */
public final class Scene {

  private static final Logger LOGGER = LoggerFactory.getLogger(Scene.class);

  /** The {@link Camera} used to render the Scene */
  private final Camera camera;
  /**
   * The {@link InterfaceManager} managing all {@link rendering.interfaces.UserInterface}s of the
   * {@link Scene}
   */
  private InterfaceManager interfaceManager;
  /** A Map of all {@link Entity}s in the Scene, classed by type */
  private final Map<Class<? extends Entity>, List<Entity>> objects;
  /** The Manager responsible to render everything in the Scene */
  private final RendererManager renderer;

  /** The total number of object in the Scene */
  private int nbObjects = 0;

  public Scene(RendererManager renderer, Window window, MouseInput mouseInput) {
    this.objects = new HashMap<>();
    this.renderer = renderer;
    this.camera = new Camera(new Vector2f());
    this.camera.adjustProjection(window.getAspectRatio());
    interfaceManager = new InterfaceManager(renderer, mouseInput);
  }

  public void cleanUp() {
    for (Map.Entry<Class<? extends Entity>, List<Entity>> entry : objects.entrySet()) {
      entry.getValue().forEach(Entity::cleanUpInternal);
    }
    interfaceManager.cleanUp();
  }

  public <T extends Entity> void add(T object, Class<T> type) {
    objects.computeIfAbsent(type, t -> new ArrayList<>());
    objects.get(type).add(object);
    renderer.register(object, type);
    nbObjects++;

    LOGGER.trace("Added an object of type [{}] to the scene", object.getClass().getName());
  }

  public List<? extends Entity> getObjects(Class<? extends Entity> ofType) {
    return objects.getOrDefault(ofType, Collections.emptyList());
  }

  public int getTotalObjects() {
    return nbObjects;
  }

  public Collection<Class<? extends Entity>> getTypes() {
    return objects.keySet();
  }

  public void update(Class<? extends Entity> entityClass, double elapsedTime) {
    for (Entity e : getObjects(entityClass)) {
      e.updateInternal(elapsedTime);
    }
  }

  public void update(double elapsedTime) {
    for (Class<? extends Entity> entityClass : getTypes()) {
      update(entityClass, elapsedTime);
    }
  }

  /** Update the Camera's position and scale */
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
   * Returns the {@link Camera} of the Logic
   *
   * @return the {@link Camera} of the Logic
   */
  public Camera getCamera() {
    return camera;
  }

  public void processUserInput() {
    interfaceManager.processUserInput();
  }

  /**
   * Returns the {@link InterfaceManager} managing all {@link rendering.interfaces.UserInterface}s
   * of the {@link Logic}
   *
   * @return the {@link InterfaceManager} managing all {@link rendering.interfaces.UserInterface}s
   *     of the {@link Logic}
   */
  public InterfaceManager getInterfaceManager() {
    return interfaceManager;
  }

  /**
   * Add a new {@link UserInterface} to the Scene
   *
   * @param ui the UI to add
   */
  public void add(UserInterface ui) {
    ui.setManager(interfaceManager);
    interfaceManager.add(ui);
  }

  public void end() {
    interfaceManager.end();
  }

  public void updateInterfaces(double elapsedTime) {
    interfaceManager.update(elapsedTime);
  }

  public void setVisibility(UserInterface ui, boolean visible) {
    if (visible) {
      interfaceManager.showInterface(ui);
    } else {
      interfaceManager.hideInterface(ui);
    }
  }
}
