/*
 * Copyright (c) 2022, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering.interfaces;

import java.util.LinkedList;
import org.joml.Vector4f;
import rendering.ResourceLoader;
import rendering.Texture;
import rendering.Window;
import rendering.interfaces.element.Button;
import rendering.interfaces.element.CornerProperties;
import rendering.interfaces.element.Dragger;

public class DraggableInterface extends UserInterface {

  public DraggableInterface(
      Window window, Texture background, String name, InterfaceManager manager) {
    super(window, background, name, manager);
  }

  public DraggableInterface(
      Window window, Vector4f background, String name, InterfaceManager manager) {
    super(window, background, name, manager);
  }

  @Override
  protected LinkedList<UIElement<?>> createFixedElements(Window window) {
    Dragger statusBar =
        new Dragger(window, new Vector4f(.25f, .25f, .25f, .5f), this)
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
    LinkedList<UIElement<?>> elements = new LinkedList<>();
    elements.add(statusBar);
    elements.add(closeButton);
    return elements;
  }

  @Override
  public UserInterface setCornerProperties(CornerProperties cornerProperties) {
    super.setCornerProperties(cornerProperties);
    getFixedElements()
        .get(0)
        .setCornerProperties(
            new CornerProperties(
                cornerProperties.getTopLeftRadius(), cornerProperties.getTopRightRadius(), 0, 0));
    return this;
  }

  @Override
  public UserInterface setSize(float x, float y) {
    super.setSize(x, y);
    getFixedElements().get(1).setPosition(x - 35, 5);
    getFixedElements().get(0).setSize(getSize().x, Math.min(40, getSize().y));
    return this;
  }
}
