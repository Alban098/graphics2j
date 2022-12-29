/*
 * Copyright (c) 2022, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering.interfaces;

import java.util.*;
import org.joml.Vector4f;
import rendering.MouseInput;
import rendering.Texture;
import rendering.Window;
import rendering.interfaces.element.Interactable;

public abstract class UserInterface extends UIElement<UserInterface> {
  protected final InterfaceManager manager;
  private final Map<String, UIElement<?>> uiElements = new HashMap<>();
  private final LinkedList<UIElement<?>> orderedUiElements = new LinkedList<>();
  private final LinkedList<UIElement<?>> fixedElements;

  protected final String name;
  private boolean visible = false;

  public UserInterface(Window window, Texture background, String name, InterfaceManager manager) {
    super(window, background, null);
    this.name = name;
    this.manager = manager;
    this.fixedElements = createFixedElements(window);
  }

  public UserInterface(Window window, Vector4f background, String name, InterfaceManager manager) {
    super(window, background, null);
    this.name = name;
    this.manager = manager;
    this.fixedElements = createFixedElements(window);
  }

  protected abstract LinkedList<UIElement<?>> createFixedElements(Window window);

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
    return orderedUiElements;
  }

  protected void addElement(String name, UIElement<?> element) {
    element.setParent(this);
    uiElements.put(name, element);
    orderedUiElements.add(element);
  }

  @Override
  public void cleanUp() {
    super.cleanUp();
    uiElements.forEach((k, v) -> v.cleanUp());
    uiElements.clear();
    orderedUiElements.clear();
    fixedElements.forEach(UIElement::cleanUp);
  }

  public boolean isVisible() {
    return visible;
  }

  public void toggleVisibility(boolean visible) {
    this.visible = visible;
  }

  public boolean input(MouseInput input) {
    Iterator<UIElement<?>> iterator = fixedElements.descendingIterator();
    while (iterator.hasNext()) {
      UIElement<?> element = iterator.next();
      if (element instanceof Interactable) {
        if (((Interactable) element).input(input)) {
          return true;
        }
      }
    }

    iterator = orderedUiElements.descendingIterator();
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

  public List<UIElement<?>> getFixedElements() {
    return Collections.unmodifiableList(fixedElements);
  }
}
