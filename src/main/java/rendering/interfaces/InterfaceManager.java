/*
 * Copyright (c) 2022-2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering.interfaces;

import java.util.*;
import java.util.stream.Collectors;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rendering.MouseInput;
import rendering.Window;
import rendering.renderers.MasterRenderer;

/**
 * This class is responsible for managing all the {@link UserInterface}s present in {@link
 * rendering.Engine} instance
 */
public class InterfaceManager {

  private static final Logger LOGGER = LoggerFactory.getLogger(InterfaceManager.class);

  /** A Collection of all {@link UserInterface}s registered and managed by this Manager */
  private final Collection<UserInterface> interfaces;
  /**
   * A Map of all {@link UserInterface} currently visible on the screen, index by time of opening
   */
  private final TreeMap<Double, UserInterface> visibleInterfaces;
  /** The renderer responsible for rendering all visible {@link UserInterface}s */
  private final MasterRenderer renderer;
  /**
   * Just a flag to indicate that a {@link UserInterface} has been closed and that {@link
   * InterfaceManager#visibleInterfaces} needs to be updated
   */
  private boolean uiHasClosed = false;

  /**
   * Creates a new InterfaceManager registered with a {@link MasterRenderer}
   *
   * @param renderer the {@link MasterRenderer} to link
   */
  public InterfaceManager(MasterRenderer renderer) {
    this.interfaces = new HashSet<>();
    this.visibleInterfaces = new TreeMap<>(Collections.reverseOrder());
    this.renderer = renderer;
  }

  /**
   * Clears the Manager and all its registered {@link UserInterface}s also unregister all {@link
   * UserInterface}s
   */
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
    LOGGER.trace("Added an interface of type [{}]", ui.getClass().getName());
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
   * <p>/!\ Please only use this methods to make a UserInterface visible /!\
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
   * <p>/!\ Please only use this methods to hide a UserInterface /!\
   *
   * @param userInterface the {@link UserInterface} to hide
   */
  public void hideInterface(UserInterface userInterface) {
    userInterface.setVisible(false);
    uiHasClosed = true;
    renderer.unregister(userInterface);
  }

  /**
   * Prepares the Manager, called once every update before anything has been updated
   *
   * @param window the main {@link Window} of the application
   */
  public void prepare(Window window) {}

  /**
   * Updates the Manager by updating all visible {@link UserInterface}
   *
   * @param elapsedTime time elapsed since last update in seconds
   */
  public void update(double elapsedTime) {
    visibleInterfaces.forEach((time, ui) -> ui.updateInternal(elapsedTime));
  }

  /**
   * Final operation of the Manager, called once every update after everything has been updated
   *
   * @param window the main {@link Window} of the application
   */
  public void finalize(Window window) {
    if (uiHasClosed) {
      Set<Double> toRemove =
          visibleInterfaces.entrySet().stream()
              .filter(e -> !e.getValue().isVisible())
              .map(Map.Entry::getKey)
              .collect(Collectors.toSet());
      toRemove.forEach(visibleInterfaces::remove);
      uiHasClosed = false;
    }
  }

  /**
   * Processes user inputs by propagating them to visible {@link UserInterface}, in reverse order
   * from rendering order
   *
   * @param input the input to process
   */
  public void processUserInput(MouseInput input) {
    // loop in reverse order to propagate input in reverse of rendering order
    for (UserInterface userInterface : visibleInterfaces.values()) {
      if (userInterface.propagateInput(input)) {
        break;
      }
    }
  }
}
