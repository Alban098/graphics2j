/*
 * Copyright (c) 2022, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering.interfaces;

import java.util.Objects;
import rendering.ResourceLoader;
import rendering.Window;
import rendering.interfaces.element.*;

public class ControllableInterface extends UserInterface {

  private static final String STATUS_BAR = "statusBar";
  private static final String MAIN_SECTION = "mainSection";
  private static final String CLOSE_BUTTON = "closeButton";
  private static final String TITLE = "title";
  private static final int STATUS_BAR_HEIGHT = 40;
  private static final int CLOSE_BUTTON_SIZE = 30;

  public ControllableInterface(Window window, String name, InterfaceManager manager) {
    super(window, name, manager);
    createBaseElements();
  }

  @Override
  public void update(double elapsedTime) {}

  private void createBaseElements() {
    Button closeButton = new Button("");
    closeButton
        .getProperties()
        .setSize(CLOSE_BUTTON_SIZE, CLOSE_BUTTON_SIZE)
        .setCornerRadius(5)
        .setBackgroundTexture(
            ResourceLoader.loadTexture("src/main/resources/textures/interfaces/close.png"));
    closeButton.onClick(() -> manager.hideInterface(this));

    TextLabel textLabel = new TextLabel(name);
    textLabel
        .getProperties()
        .setPosition(10, 7)
        .setFontColor(1, 1, 1, 1)
        .setFontSize(30)
        .setFontFamily("Candara");

    Dragger statusBar = new Dragger();
    statusBar.getProperties().setPosition(0, 0).setBackgroundColor(0.25f, 0.35f, 0.7f, 1f);
    statusBar.addElement(CLOSE_BUTTON, closeButton);
    statusBar.addElement(TITLE, textLabel);
    super.addElement(STATUS_BAR, statusBar);

    Section section = new Section();
    super.addElement(MAIN_SECTION, section);
  }

  @Override
  protected void onPropertyChange(
      Properties.Snapshot oldProperties, Properties.Snapshot newProperties) {
    if (!oldProperties.getSize().equals(newProperties.getSize())) {
      int offset = (STATUS_BAR_HEIGHT - CLOSE_BUTTON_SIZE) / 2;
      float x = newProperties.getSize().x;
      float y = newProperties.getSize().y;
      if (super.getElement(STATUS_BAR) != null && super.getElement(MAIN_SECTION) != null) {
        super.getElement(STATUS_BAR)
            .getElement(CLOSE_BUTTON)
            .getProperties()
            .setPosition(x - (STATUS_BAR_HEIGHT - offset), offset);
        super.getElement(STATUS_BAR).getProperties().setSize(x, Math.min(STATUS_BAR_HEIGHT, y));
        super.getElement(STATUS_BAR)
            .getElement(TITLE)
            .getProperties()
            .setSize(newProperties.getSize().x - STATUS_BAR_HEIGHT, STATUS_BAR_HEIGHT);
        super.getElement(MAIN_SECTION)
            .getProperties()
            .setSize(x, y - STATUS_BAR_HEIGHT)
            .setPosition(0, STATUS_BAR_HEIGHT);
      }
    }
    if (!oldProperties.getBackgroundColor().equals(newProperties.getBackgroundColor())) {
      super.getElement(MAIN_SECTION)
          .getProperties()
          .setBackgroundColor(newProperties.getBackgroundColor());
    }
    if (!Objects.equals(
        oldProperties.getBackgroundTexture(), newProperties.getBackgroundTexture())) {
      super.getElement(MAIN_SECTION)
          .getProperties()
          .setBackgroundTexture(newProperties.getBackgroundTexture());
    }
  }

  @Override
  public UIElement getElement(String identifier) {
    return super.getElement(MAIN_SECTION).getElement(identifier);
  }

  @Override
  protected void addElement(String identifier, UIElement element) {
    super.getElement(MAIN_SECTION).addElement(identifier, element);
    element.setContainer(this);
  }
}
