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
import rendering.Texture;
import rendering.Window;

public class UserInterface extends UIElement {

  private final Map<String, UIElement> uiElements = new HashMap<>();
  private final String name;

  public UserInterface(Texture background, String name, Window window) {
    super(window, background, null);
    this.name = name;
  }

  public UserInterface(Vector4f background, String name, Window window) {
    super(window, background, null);
    this.name = name;
  }

  public void setSize(int x, int y) {
    this.size.set(x, y);
  }

  public void setPosition(int x, int y) {
    this.position.set(x, y);
  }

  public String getName() {
    return name;
  }

  public void update(double elapsedTime, UIElement parent) {
    uiElements.forEach((k, v) -> v.update(elapsedTime, this));
  }

  public boolean isTextured() {
    return renderable.getTexture() != null;
  }

  public Vector4f getColor() {
    return color;
  }

  public Map<String, UIElement> getElements() {
    return uiElements;
  }

  public void addElement(String name, UIElement element) {
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
}
