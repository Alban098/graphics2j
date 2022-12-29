/*
 * Copyright (c) 2022, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering.interfaces;

import java.util.HashMap;
import java.util.Map;
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
  protected Map<String, UIElement<?>> createFixedElements(Window window) {
    Dragger statusBar =
        new Dragger(new Vector4f(0.25f, 0.25f, 0.25f, 0.25f))
            .setSize(getSize().x, Math.min(40, getSize().y))
            .setPosition(0, 0);
    Button closeButton =
        new Button(
                ResourceLoader.loadTexture("src/main/resources/textures/interfaces/close.png"), "")
            .setSize(30, 30)
            .setPosition(getSize().x - 35, 5);
    closeButton.onClick(() -> manager.hideInterface(this));
    statusBar.setContainer(this);
    statusBar.addElement(closeButton);
    closeButton.setContainer(this);

    Map<String, UIElement<?>> elements = new HashMap<>();
    elements.put("statusBar", statusBar);
    return elements;
  }

  @Override
  public UserInterface setCornerProperties(CornerProperties cornerProperties) {
    super.setCornerProperties(cornerProperties);
    return this;
  }

  @Override
  public UserInterface setSize(float x, float y) {
    super.setSize(x, y);
    getOverlayElement("statusBar").getElements().get(0).setPosition(x - 35, 5);
    getOverlayElement("statusBar").setSize(getSize().x, Math.min(40, getSize().y));
    return this;
  }
}
