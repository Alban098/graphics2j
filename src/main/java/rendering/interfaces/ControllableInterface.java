/*
 * Copyright (c) 2022-2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering.interfaces;

import org.joml.Vector2f;
import org.joml.Vector4f;
import rendering.ResourceLoader;
import rendering.Texture;
import rendering.Window;
import rendering.interfaces.element.Button;
import rendering.interfaces.element.Dragger;
import rendering.interfaces.element.Section;
import rendering.interfaces.element.UIElement;
import rendering.interfaces.element.property.Properties;
import rendering.interfaces.element.text.TextLabel;

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
        .set(Properties.SIZE, new Vector2f(CLOSE_BUTTON_SIZE, CLOSE_BUTTON_SIZE))
        .set(Properties.CORNER_RADIUS, 5f)
        .set(
            Properties.BACKGROUND_TEXTURE,
            ResourceLoader.loadTexture("src/main/resources/textures/interfaces/close.png"));
    closeButton.onClickEnd((input) -> manager.hideInterface(this));

    TextLabel textLabel = new TextLabel(name);
    textLabel
        .getProperties()
        .set(Properties.POSITION, new Vector2f(10, 7))
        .set(Properties.FONT_COLOR, new Vector4f(1, 1, 1, 1))
        .set(Properties.FONT_SIZE, 30f)
        .set(Properties.FONT_FAMILY, "Candara");

    Dragger statusBar = new Dragger();
    statusBar
        .getProperties()
        .set(Properties.POSITION, new Vector2f(0, 0))
        .set(Properties.BACKGROUND_COLOR, new Vector4f(0.25f, 0.35f, 0.7f, 1f));
    statusBar.addElement(CLOSE_BUTTON, closeButton);
    statusBar.addElement(TITLE, textLabel);
    super.addElement(STATUS_BAR, statusBar);

    Section section = new Section();
    super.addElement(MAIN_SECTION, section);
  }

  @Override
  protected void onPropertyChange(Properties property, Object value) {
    if (property == Properties.SIZE) {
      int offset = (STATUS_BAR_HEIGHT - CLOSE_BUTTON_SIZE) / 2;
      Vector2f size = (Vector2f) value;
      if (super.getElement(STATUS_BAR) != null && super.getElement(MAIN_SECTION) != null) {
        super.getElement(STATUS_BAR)
            .getElement(CLOSE_BUTTON)
            .getProperties()
            .set(Properties.POSITION, new Vector2f(size.x - (STATUS_BAR_HEIGHT - offset), offset));
        super.getElement(STATUS_BAR)
            .getProperties()
            .set(Properties.SIZE, new Vector2f(size.x, Math.min(STATUS_BAR_HEIGHT, size.y)));
        super.getElement(STATUS_BAR)
            .getElement(TITLE)
            .getProperties()
            .set(Properties.SIZE, new Vector2f(size.x - STATUS_BAR_HEIGHT, STATUS_BAR_HEIGHT));
        super.getElement(MAIN_SECTION)
            .getProperties()
            .set(Properties.SIZE, new Vector2f(size.x, size.y - STATUS_BAR_HEIGHT))
            .set(Properties.POSITION, new Vector2f(0, STATUS_BAR_HEIGHT));
      }
    }
    if (property == Properties.BACKGROUND_COLOR) {
      super.getElement(MAIN_SECTION)
          .getProperties()
          .set(Properties.BACKGROUND_COLOR, (Vector4f) value);
    }
    if (property == Properties.BACKGROUND_TEXTURE) {
      super.getElement(MAIN_SECTION)
          .getProperties()
          .set(Properties.BACKGROUND_TEXTURE, (Texture) value);
    }
  }

  @Override
  public UIElement getElement(String identifier) {
    return super.getElement(MAIN_SECTION).getElement(identifier);
  }

  @Override
  public void addElement(String identifier, UIElement element) {
    super.getElement(MAIN_SECTION).addElement(identifier, element);
    element.setContainer(this);
  }
}
