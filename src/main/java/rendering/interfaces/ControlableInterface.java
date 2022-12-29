/*
 * Copyright (c) 2022, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering.interfaces;

import org.joml.Vector4f;
import rendering.ResourceLoader;
import rendering.Texture;
import rendering.Window;
import rendering.interfaces.element.Button;
import rendering.interfaces.element.CornerProperties;
import rendering.interfaces.element.Dragger;
import rendering.interfaces.element.Section;

public class ControlableInterface extends UserInterface {

  private static final String STATUS_BAR = "statusBar";
  private static final String MAIN_SECTION = "mainSection";
  private static final String CLOSE_BUTTON = "closeButton";
  private static final int STATUS_BAR_HEIGHT = 40;
  private static final int CLOSE_BUTTON_SIZE = 30;

  public ControlableInterface(
      Window window, Texture background, String name, InterfaceManager manager) {
    super(window, new Vector4f(), name, manager);
    createBaseElements(background);
  }

  public ControlableInterface(
      Window window, Vector4f background, String name, InterfaceManager manager) {
    super(window, background, name, manager);
    createBaseElements(null);
  }

  @Override
  public void update(double elapsedTime) {}

  private void createBaseElements(Texture texture) {
    int offset = (STATUS_BAR_HEIGHT - CLOSE_BUTTON_SIZE) / 2;
    Button closeButton =
        new Button(
                ResourceLoader.loadTexture("src/main/resources/textures/interfaces/close.png"),
                "",
                new Vector4f())
            .setSize(CLOSE_BUTTON_SIZE, CLOSE_BUTTON_SIZE)
            .setPosition(getSize().x - (STATUS_BAR_HEIGHT - offset), offset);
    closeButton.onClick(() -> manager.hideInterface(this));

    Dragger statusBar =
        new Dragger(new Vector4f(0.25f, 0.35f, 0.7f, 1f))
            .setSize(getSize().x, Math.min(STATUS_BAR_HEIGHT, getSize().y))
            .setPosition(0, 0);
    statusBar.addElement(CLOSE_BUTTON, closeButton);
    super.addElement(STATUS_BAR, statusBar);
    if (texture == null) {
      super.addElement(
          MAIN_SECTION,
          new Section(getColor())
              .setSize(getSize().x, getSize().y - STATUS_BAR_HEIGHT)
              .setPosition(0, STATUS_BAR_HEIGHT));
    } else {
      super.addElement(
          MAIN_SECTION,
          new Section(texture)
              .setSize(getSize().x, getSize().y - STATUS_BAR_HEIGHT)
              .setPosition(0, STATUS_BAR_HEIGHT));
    }
  }

  @Override
  public UserInterface setCornerProperties(CornerProperties cornerProperties) {
    super.setCornerProperties(cornerProperties);
    return this;
  }

  @Override
  public UserInterface setSize(float x, float y) {
    super.setSize(x, y);
    int offset = (STATUS_BAR_HEIGHT - CLOSE_BUTTON_SIZE) / 2;
    if (super.getElement(STATUS_BAR) != null && super.getElement(MAIN_SECTION) != null) {
      super.getElement(STATUS_BAR)
          .getElement(CLOSE_BUTTON)
          .setPosition(x - (STATUS_BAR_HEIGHT - offset), offset);
      super.getElement(STATUS_BAR).setSize(x, Math.min(STATUS_BAR_HEIGHT, y));
      super.getElement(MAIN_SECTION).setSize(x, y - STATUS_BAR_HEIGHT);
    }
    return this;
  }

  @Override
  public UIElement<?> getElement(String identifier) {
    return super.getElement(MAIN_SECTION).getElement(identifier);
  }

  @Override
  protected void addElement(String identifier, UIElement<?> element) {
    super.getElement(MAIN_SECTION).addElement(identifier, element);
    element.setContainer(this);
  }
}
