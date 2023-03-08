/*
 * Copyright (c) 2022-2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package org.alban098.engine2j.interfaces.windows;

import org.alban098.engine2j.common.Window;
import org.alban098.engine2j.common.resources.InternalResources;
import org.alban098.engine2j.common.shaders.data.Texture;
import org.alban098.engine2j.common.utils.ResourceLoader;
import org.alban098.engine2j.interfaces.InterfaceRenderingManager;
import org.alban098.engine2j.interfaces.components.Button;
import org.alban098.engine2j.interfaces.components.Dragger;
import org.alban098.engine2j.interfaces.components.Section;
import org.alban098.engine2j.interfaces.components.UIElement;
import org.alban098.engine2j.interfaces.components.property.Properties;
import org.alban098.engine2j.interfaces.components.text.TextLabel;
import org.joml.Vector2f;
import org.joml.Vector4f;

/**
 * Just a convenience default implementation of {@link UserInterface} containing a taskbar
 * displaying its title, a close button and a subsection for children elements
 */
public class DecoratedUI extends UserInterface {

  /** A UUID identifying the status bar element */
  private static final String STATUS_BAR = "5585936e-c75c-4344-a561-1286afb7affa";
  /** A UUID identifying the main section of the user interface */
  private static final String MAIN_SECTION = "8cb03958-de6f-4e75-90d6-466538f3ee13";
  /** A UUID identifying the close button element */
  private static final String CLOSE_BUTTON = "ee3d36b6-abda-4c9a-9a18-16fa357936e7";
  /** A UUID identifying the title element */
  private static final String TITLE = "97eb6ce1-c262-49a6-be7c-08898755fcc7";
  /** The height of the status bar in pixels */
  private static final int STATUS_BAR_HEIGHT = 40;
  /** the size of the close button (width and height) in pixels */
  private static final int CLOSE_BUTTON_SIZE = 30;

  /**
   * Creates a new UserInterface contained in a {@link Window}, with a name and managed by an {@link
   * InterfaceRenderingManager}
   *
   * @param window the {@link Window} containing this UserInterface
   * @param name the name of this UserInterface
   */
  public DecoratedUI(Window window, String name) {
    super(window, name);
    createBaseElements();
  }

  /**
   * Updates the UserInterface, this method is called once every update. /!\ This method is called
   * once every update, thus can be called multiple time per frame /!\
   *
   * @param elapsedTime the elapsed time since last update in seconds
   */
  @Override
  public void update(double elapsedTime) {}

  /**
   * Called every time a {@link Properties} of the ControllableInterface is changed. Updates Status
   * Bar and Main SubSection when size or background are changed
   *
   * <p>/!\ This base method must be called if overridden to ensure elements coherence /!\
   *
   * @param property the changed {@link Properties}
   * @param value the new value
   */
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

  /**
   * See {@link UserInterface#getElement(String)}, just forward the call to the main SubSection
   *
   * @param identifier the identifier of the child to retrieve
   * @return the retrieves {@link UIElement}, null if not found
   */
  @Override
  public UIElement getElement(String identifier) {
    return super.getElement(MAIN_SECTION).getElement(identifier);
  }

  /**
   * See {@link UserInterface#addElement(String, UIElement)}, just forward the call to the main
   * SubSection
   *
   * @param identifier the identifier of the child to retrieve
   * @param element the {@link UIElement} to add
   */
  @Override
  public void addElement(String identifier, UIElement element) {
    super.getElement(MAIN_SECTION).addElement(identifier, element);
    element.setContainer(this);
  }

  /** Create all the element for this ControllableInterface */
  private void createBaseElements() {
    // Close button
    Button closeButton = new Button("");
    closeButton
        .getProperties()
        .set(Properties.SIZE, new Vector2f(CLOSE_BUTTON_SIZE, CLOSE_BUTTON_SIZE))
        .set(Properties.CORNER_RADIUS, 5f)
        .set(
            Properties.BACKGROUND_TEXTURE,
            ResourceLoader.decodeTexture(InternalResources.CLOSE_TEXTURE_BASE64));
    closeButton.onClickEnd((input) -> manager.hideInterface(this));

    // Title of the window
    TextLabel textLabel = new TextLabel(name);
    textLabel
        .getProperties()
        .set(Properties.POSITION, new Vector2f(10, 7))
        .set(Properties.FONT_COLOR, new Vector4f(1, 1, 1, 1))
        .set(Properties.FONT_SIZE, 30f)
        .set(Properties.FONT_FAMILY, "Candara");

    // Status bar containing title and close button
    Dragger statusBar = new Dragger();
    statusBar
        .getProperties()
        .set(Properties.POSITION, new Vector2f(0, 0))
        .set(Properties.BACKGROUND_COLOR, new Vector4f(0.25f, 0.35f, 0.7f, 1f));
    statusBar.addElement(CLOSE_BUTTON, closeButton);
    statusBar.addElement(TITLE, textLabel);
    super.addElement(STATUS_BAR, statusBar);

    // Main section containing all element subsequent of the UserInterface
    Section section = new Section();
    super.addElement(MAIN_SECTION, section);
  }
}
