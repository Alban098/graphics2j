/*
 * Copyright (c) 2022, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering.interfaces;

import java.util.HashMap;
import java.util.Map;
import org.joml.Vector2f;
import org.joml.Vector4f;
import rendering.MouseInput;
import rendering.Texture;
import rendering.Window;
import rendering.interfaces.element.Interactable;

public class UserInterface extends UIElement<UserInterface> {

  private final Map<String, UIElement<?>> uiElements = new HashMap<>();
  private final String name;
  private boolean visible = false;

  public UserInterface(Window window, Texture background, String name) {
    super(window, background, null);
    this.name = name;
  }

  public UserInterface(Window window, Vector4f background, String name) {
    super(window, background, null);
    this.name = name;
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

  public Map<String, UIElement<?>> getElements() {
    return uiElements;
  }

  public void addElement(String name, UIElement<?> element) {
    element.setParent(this);
    uiElements.put(name, element);
  }

  public Vector2f getPosition() {
    return position;
  }

  public Vector2f getSize() {
    return size;
  }

  @Override
  public void cleanUp() {
    super.cleanUp();
    uiElements.forEach((k, v) -> v.cleanUp());
  }

  public boolean isVisible() {
    return visible;
  }

  public void toggleVisibility(boolean visible) {
    this.visible = visible;
  }

  public boolean input(MouseInput input) {
    for (UIElement<?> element : uiElements.values()) {
      if (element instanceof Interactable) {
        if (((Interactable) element).input(input)) {
          return true;
        }
      }
    }
    return false;
  }
}
