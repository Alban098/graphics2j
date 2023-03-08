/*
 * Copyright (c) 2022-2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package org.alban098.engine2j.interfaces;

import java.util.*;
import java.util.stream.Collectors;

import org.alban098.engine2j.common.Cleanable;
import org.alban098.engine2j.common.Window;
import org.alban098.engine2j.engine.Engine;
import org.alban098.engine2j.entities.EntityRenderingManager;
import org.alban098.engine2j.input.MouseInputManager;
import org.alban098.engine2j.interfaces.renderers.FontRenderer;
import org.alban098.engine2j.interfaces.renderers.InterfaceRenderer;
import org.alban098.engine2j.interfaces.renderers.LineRenderer;
import org.alban098.engine2j.interfaces.windows.UserInterface;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is responsible for managing all the {@link UserInterface}s present in {@link Engine}
 * instance
 */
public final class InterfaceRenderingManager implements Cleanable {

  /** Just a Logger to log events */
  private static final Logger LOGGER = LoggerFactory.getLogger(InterfaceRenderingManager.class);

  /** A Collection of all {@link UserInterface}s registered and managed by this Manager */
  private final Collection<UserInterface> interfaces;
  /** A Map of all {@link UserInterface} classified by type */
  private final Map<Class<? extends UserInterface>, Collection<UserInterface>> classifiedInterfaces;
  /**
   * A Map of all {@link UserInterface} currently visible on the screen, index by time of opening
   */
  private final TreeMap<Double, UserInterface> visibleInterfaces;
  /** The Manager responsible for mouse input capture */
  private final MouseInputManager mouseInputManager;
  /**
   * Just a flag to indicate that a {@link UserInterface} has been closed and that {@link
   * InterfaceRenderingManager#visibleInterfaces} needs to be updated
   */
  private boolean uiHasClosed = false;

  /** The {@link InterfaceRenderer} in charge of rendering {@link UserInterface}s */
  private final InterfaceRenderer renderer;

  /**
   * Creates a new InterfaceManager registered with a {@link EntityRenderingManager}
   *
   * @param mouseInputManager the {@link MouseInputManager} to link
   */
  public InterfaceRenderingManager(Window window, MouseInputManager mouseInputManager) {
    this.interfaces = new HashSet<>();
    this.classifiedInterfaces = new HashMap<>();
    this.visibleInterfaces = new TreeMap<>(Collections.reverseOrder());
    this.mouseInputManager = mouseInputManager;
    this.renderer = new InterfaceRenderer(window, new FontRenderer(), new LineRenderer());
    LOGGER.info("Successfully initialized InterfaceManager");
  }

  /**
   * Clears the Manager and all its registered {@link UserInterface}s also unregister all {@link
   * UserInterface}s
   */
  @Override
  public void cleanUp() {
    interfaces.forEach(UserInterface::cleanUp);
    interfaces.clear();
    visibleInterfaces.clear();
  }

  /**
   * Add a new {@link UserInterface} to the Manager, hidden by default
   *
   * @param ui the UI to add
   */
  public void add(UserInterface ui) {
    interfaces.add(ui);
    ui.setManager(this);
    classifiedInterfaces.computeIfAbsent(ui.getClass(), key -> new ArrayList<>());
    classifiedInterfaces.get(ui.getClass()).add(ui);
    LOGGER.debug("Added an interface of type [{}]", ui.getClass().getName());
  }

  /**
   * Returns a Collection of all registered {@link UserInterface}
   *
   * @return a Collection of all registered {@link UserInterface}
   */
  public Collection<? extends UserInterface> getInterfaces() {
    return interfaces;
  }

  /**
   * Returns a Collection of all registered {@link UserInterface}
   *
   * @param ofType the class type of UserInterface to retrieve
   * @return a Collection of all registered {@link UserInterface}
   */
  public Collection<? extends UserInterface> getInterfaces(Class<? extends UserInterface> ofType) {
    return classifiedInterfaces.getOrDefault(ofType, Collections.emptyList());
  }

  /**
   * Returns a Collection of all types of {@link UserInterface} present in the Scene
   *
   * @return a Collection of all types of {@link UserInterface} present in the Scene
   */
  public Collection<Class<? extends UserInterface>> getInterfaceTypes() {
    return classifiedInterfaces.keySet();
  }

  /**
   * Returns a Collection of all visible {@link UserInterface} managed by this Manager
   *
   * @return a Collection of all visible {@link UserInterface}
   */
  public Collection<? extends UserInterface> getVisibleInterfaces() {
    return visibleInterfaces.values();
  }

  /**
   * Makes a {@link UserInterface} visible on screen and update it accordingly
   *
   * <p>/!\ Please only use this method to make a UserInterface visible /!\
   *
   * @param userInterface the {@link UserInterface} to make visible
   */
  public void showInterface(UserInterface userInterface) {
    userInterface.setVisible(true);
    visibleInterfaces.put(GLFW.glfwGetTime(), userInterface);
    renderer.register(userInterface);
  }

  /**
   * Hides a {@link UserInterface} on screen and update it accordingly
   *
   * <p>/!\ Please only use this method to hide a UserInterface /!\
   *
   * @param userInterface the {@link UserInterface} to hide
   */
  public void hideInterface(UserInterface userInterface) {
    userInterface.setVisible(false);
    uiHasClosed = true;
    renderer.unregister(userInterface);
  }

  public void setVisibility(UserInterface ui, boolean visible) {
    if (visible) {
      showInterface(ui);
    } else {
      hideInterface(ui);
    }
  }

  /**
   * Updates the Manager by updating all visible {@link UserInterface}
   *
   * @param elapsedTime time elapsed since last update in seconds
   */
  public void update(double elapsedTime) {
    visibleInterfaces.forEach((time, ui) -> ui.updateInternal(elapsedTime));
  }

  /** Final operation of the Manager, called once every update after everything has been updated */
  public void end() {
    if (uiHasClosed) {
      Set<Double> toRemove =
          visibleInterfaces.entrySet().stream()
              .filter(e -> !e.getValue().isVisible())
              .map(Map.Entry::getKey)
              .collect(Collectors.toSet());

      toRemove.forEach(
          (ui) -> {
            if (mouseInputManager.hasControl(visibleInterfaces.remove(ui))) {
              mouseInputManager.release();
            }
          });
      uiHasClosed = false;
    }
  }

  /**
   * Processes user inputs by propagating them to visible {@link UserInterface}, in reverse order
   * from rendering order
   */
  public void processUserInput() {
    // loop in reverse order to propagate input in reverse of rendering order
    for (UserInterface userInterface : visibleInterfaces.values()) {
      if (mouseInputManager.canTakeControl(userInterface)
          && userInterface.propagateInput(mouseInputManager)) {
        break;
      }
    }
  }

  public void render() {
    renderer.render();
  }
}
