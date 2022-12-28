/*
 * Copyright (c) 2022, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering.interfaces;

import java.util.*;
import org.joml.Vector4f;
import rendering.MouseInput;
import rendering.ResourceLoader;
import rendering.Texture;
import rendering.Window;
import rendering.interfaces.element.Button;
import rendering.interfaces.element.CornerProperties;
import rendering.interfaces.element.DragBar;
import rendering.interfaces.element.Interactable;

public class UserInterface extends UIElement<UserInterface> {

  protected final InterfaceManager manager;
  protected final Map<String, UIElement<?>> uiElements = new HashMap<>();

  protected final LinkedList<UIElement<?>> ordedredUiElements = new LinkedList<>();
  protected final String name;
  private boolean visible = false;

  public UserInterface(
      Window window, Texture background, String name, boolean statusBar, InterfaceManager manager) {
    super(window, background, null);
    this.name = name;
    this.manager = manager;
    if (statusBar) {
      createStatusBar(window);
    }
  }

  public UserInterface(
      Window window,
      Vector4f background,
      String name,
      boolean statusBar,
      InterfaceManager manager) {
    super(window, background, null);
    this.name = name;
    this.manager = manager;
    if (statusBar) {
      createStatusBar(window);
    }
  }

  private void createStatusBar(Window window) {
    DragBar statusBar =
        new DragBar(window, new Vector4f(.25f, .25f, .25f, .5f), this)
            .setSize(getSize().x, Math.min(40, getSize().y))
            .setPosition(0, 0);
    Button closeButton =
        new Button(
                window,
                ResourceLoader.loadTexture("src/main/resources/textures/interfaces/close.png"),
                "",
                this)
            .setSize(30, 30)
            .setPosition(getSize().x - 35, 5);
    closeButton.onClick(() -> manager.hideInterface(this));
    addElement("statusBarBg", statusBar);
    addElement("closeButton", closeButton);
  }

  public String getName() {
    return name;
  }

  public void update(double elapsedTime, UIElement<?> parent) {
    uiElements.forEach((k, v) -> v.update(elapsedTime, this));
  }

  public boolean isTextured() {
    return renderable.getTexture() != null;
  }

  public Vector4f getColor() {
    return color;
  }

  public Collection<UIElement<?>> getElements() {
    return ordedredUiElements;
  }

  protected void addElement(String name, UIElement<?> element) {
    element.setParent(this);
    uiElements.put(name, element);
    ordedredUiElements.add(element);
  }

  @Override
  public void cleanUp() {
    super.cleanUp();
    uiElements.forEach((k, v) -> v.cleanUp());
    uiElements.clear();
    ordedredUiElements.clear();
  }

  public boolean isVisible() {
    return visible;
  }

  public void toggleVisibility(boolean visible) {
    this.visible = visible;
  }

  @Override
  public UserInterface setCornerProperties(CornerProperties cornerProperties) {
    super.setCornerProperties(cornerProperties);
    if (uiElements.containsKey("statusBarBg")) {
      uiElements
          .get("statusBarBg")
          .setCornerProperties(
              new CornerProperties(
                  cornerProperties.getTopLeftRadius(), cornerProperties.getTopRightRadius(), 0, 0));
    }
    return this;
  }

  @Override
  public UserInterface setSize(float x, float y) {
    super.setSize(x, y);
    if (uiElements.containsKey("closeButton")) {
      uiElements.get("closeButton").setPosition(x - 35, 5);
    }
    if (uiElements.containsKey("statusBarBg")) {
      uiElements.get("statusBarBg").setSize(getSize().x, Math.min(40, getSize().y));
    }
    return this;
  }

  public boolean input(MouseInput input) {
    Iterator<UIElement<?>> iterator = ordedredUiElements.descendingIterator();
    while (iterator.hasNext()) {
      UIElement<?> element = iterator.next();
      if (element instanceof Interactable) {
        if (((Interactable) element).input(input)) {
          return true;
        }
      }
    }
    return false;
  }
}
