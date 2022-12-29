/*
 * Copyright (c) 2022, @Author Alban098
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

public class InterfaceManager {

  private static final Logger LOGGER = LoggerFactory.getLogger(InterfaceManager.class);

  private final Collection<UserInterface> interfaces;
  private final TreeMap<Double, UserInterface> visibleInterfaces;
  private final MasterRenderer renderer;
  private boolean uiHasClosed = false;

  public InterfaceManager(MasterRenderer renderer) {
    this.interfaces = new HashSet<>();
    this.visibleInterfaces = new TreeMap<>(Collections.reverseOrder());
    this.renderer = renderer;
  }

  public void cleanUp() {
    interfaces.forEach(UserInterface::cleanUp);
    interfaces.clear();
    visibleInterfaces.clear();
  }

  public <T extends UserInterface> void add(T ui) {
    interfaces.add(ui);
    LOGGER.trace("Added an interface of type [{}]", ui.getClass().getName());
  }

  public Collection<? extends UserInterface> getInterfaces() {
    return interfaces;
  }

  public Collection<? extends UserInterface> getVisibleInterfaces() {
    return visibleInterfaces.values();
  }

  public int getTotalInterfaces() {
    return interfaces.size();
  }

  public int getTotalVisibleInterfaces() {
    return visibleInterfaces.size();
  }

  public void showInterface(UserInterface userInterface) {
    userInterface.toggleVisibility(true);
    visibleInterfaces.put(GLFW.glfwGetTime(), userInterface);
    renderer.register(userInterface);
  }

  public void hideInterface(UserInterface userInterface) {
    userInterface.toggleVisibility(false);
    uiHasClosed = true;
    renderer.unregister(userInterface);
  }

  public void prepare(Window window) {}

  public void update(double elapsedTime) {
    visibleInterfaces.forEach((time, ui) -> ui.update(elapsedTime));
  }

  public void input(MouseInput input) {
    // loop in reverse order to propagate input in reverse of rendering order
    for (UserInterface userInterface : visibleInterfaces.values()) {
      if (userInterface.input(input)) {
        break;
      }
    }
  }

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
}
