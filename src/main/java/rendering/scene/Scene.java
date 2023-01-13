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
import rendering.MouseInputManager;
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
  private final InterfaceManager interfaceManager;
  /** A Map of all {@link Entity}s in the Scene, classed by type */
  private final Map<Class<? extends Entity>, Set<Entity>> objects;
  /** The Manager responsible for rendering everything in the Scene */
  private final RendererManager renderer;

  /** The total number of object in the Scene */
  private int nbObjects = 0;

  /**
   * Creates a new Scene and link it to a Renderer and Window, also links a MouseInputManager
   *
   * @param renderer the Manager responsible for rendering everything in the Scene
   * @param window the Window in which the scene will be rendered
   * @param mouseInputManager the Manager responsible for mouse input handling
   */
  public Scene(RendererManager renderer, Window window, MouseInputManager mouseInputManager) {
    this.objects = new HashMap<>();
    this.renderer = renderer;
    this.camera = new Camera(new Vector2f());
    this.camera.adjustProjection(window.getAspectRatio());
    interfaceManager = new InterfaceManager(renderer, mouseInputManager);
  }

  /** Clears the Scene and all it's Objects */
  public void cleanUp() {
    for (Map.Entry<Class<? extends Entity>, Set<Entity>> entry : objects.entrySet()) {
      entry.getValue().forEach(Entity::cleanUpInternal);
    }
    objects.clear();
    interfaceManager.cleanUp();
  }

  /**
   * Adds a new {@link Entity} to the Scene
   *
   * @param object the {@link Entity} to add
   * @param type the class type of Entity to add
   */
  public void add(Entity object, Class<? extends Entity> type) {
    objects.computeIfAbsent(type, t -> new HashSet<>());
    if (objects.get(type).add(object)) {
      renderer.register(object, type);
      nbObjects++;
      for (Entity e : object.getChildren()) {
        add(e, e.getClass());
      }

      LOGGER.trace("Added an object of type [{}] to the scene", object.getClass().getName());
    }
  }

  /**
   * Returns a List of all Entities of a certain type
   *
   * @param ofType the class type of Entity to retrieve
   * @return a List of all Entities of a certain type
   */
  public Collection<? extends Entity> getEntitiesOfType(Class<? extends Entity> ofType) {
    return objects.getOrDefault(ofType, new HashSet<>());
  }

  /**
   * Returns a List of all User Interfaces of a certain type
   *
   * @param ofType the class type of UserInterface to retrieve
   * @return a List of all User Interfaces of a certain type
   */
  public Collection<? extends UserInterface> getInterfacesOfType(
      Class<? extends UserInterface> ofType) {
    return interfaceManager.getInterfaces(ofType);
  }

  /**
   * Returns the total number of objects in the scene
   *
   * @return the total number of objects in the scene
   */
  public int getTotalObjects() {
    return nbObjects;
  }

  /**
   * Returns a Collection of all types of {@link Entity} present in the Scene
   *
   * @return a Collection of all types of {@link Entity} present in the Scene
   */
  public Collection<Class<? extends Entity>> getEntityTypes() {
    return objects.keySet();
  }

  /**
   * Returns a Collection of all types of {@link UserInterface} present in the Scene
   *
   * @return a Collection of all types of {@link UserInterface} present in the Scene
   */
  public Collection<Class<? extends UserInterface>> getInterfaceTypes() {
    return interfaceManager.getInterfaceTypes();
  }

  /**
   * Updates the Scene by updating every Entity of a certain type
   *
   * @param entityClass the class type of Entity to update
   * @param elapsedTime the elapsed time since last update in seconds
   */
  public void update(Class<? extends Entity> entityClass, double elapsedTime) {
    for (Entity e : getEntitiesOfType(entityClass)) {
      e.updateInternal(elapsedTime);
    }
  }

  /**
   * Updates the Scene by updating every Entity of every types
   *
   * @param elapsedTime the elapsed time since last update in seconds
   */
  public void update(double elapsedTime) {
    for (Class<? extends Entity> entityClass : getEntityTypes()) {
      update(entityClass, elapsedTime);
    }
  }

  /** Update the Camera's position and scale */
  public void updateCamera(Window window, MouseInputManager mouseInput) {
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

  /** Propagate the Mouse inputs to every User Interface in the scene */
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
   * Adds a new {@link UserInterface} to the Scene
   *
   * @param ui the UI to add
   */
  public void add(UserInterface ui) {
    ui.setManager(interfaceManager);
    interfaceManager.add(ui);
  }

  /** Finalizes the frame of the Scene */
  public void end() {
    interfaceManager.end();
  }

  /**
   * Updates all User Interfaces present in the Scene
   *
   * @param elapsedTime the elapsed time since last update in seconds
   */
  public void updateInterfaces(double elapsedTime) {
    interfaceManager.update(elapsedTime);
  }

  /**
   * Sets the visibility of a {@link UserInterface}
   *
   * @param ui the {@link UserInterface} to change the visibility of
   * @param visible should the {@link UserInterface} be visible or not
   */
  public void setVisibility(UserInterface ui, boolean visible) {
    if (visible) {
      interfaceManager.showInterface(ui);
    } else {
      interfaceManager.hideInterface(ui);
    }
  }
}
