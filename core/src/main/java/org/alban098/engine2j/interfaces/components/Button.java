/*
 * Copyright (c) 2022-2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package org.alban098.engine2j.interfaces.components;

import org.alban098.engine2j.interfaces.components.property.Properties;
import org.alban098.engine2j.interfaces.components.text.TextLabel;
import org.joml.Vector2f;

/** An implementation of {@link UIElement} representing a simple Button, Hoverable and Clickable */
public final class Button extends UIElement implements Hoverable, Clickable {

  /** The identifier of the text to be displayed in the Button */
  private static final String TEXT = "textLabel";

  /**
   * Creates a new Button with a specified text
   *
   * @param text the text inside the Button
   */
  public Button(String text) {
    super();
    super.addElement(TEXT, new TextLabel(text));
  }

  /**
   * Called every time a {@link Properties} of the Button is changed, updates the TextLabel
   * contained inside when one of those properties is changed :
   *
   * <ul>
   *   <li>{@link Properties#FONT_FAMILY}
   *   <li>{@link Properties#FONT_SIZE}
   *   <li>{@link Properties#FONT_COLOR}
   *   <li>{@link Properties#FONT_BLUR}
   *   <li>{@link Properties#FONT_WIDTH}
   *   <li>{@link Properties#SIZE}
   * </ul>
   *
   * @param property the changed {@link Properties}
   * @param value the new value
   */
  @Override
  protected void onPropertyChange(Properties property, Object value) {
    if (property == Properties.FONT_FAMILY
        || property == Properties.FONT_SIZE
        || property == Properties.FONT_COLOR
        || property == Properties.FONT_BLUR
        || property == Properties.FONT_WIDTH) {
      getElement(TEXT).getProperties().set(property, value);
    }
    if (property == Properties.SIZE) {
      Vector2f size = getProperties().get(Properties.SIZE, Vector2f.class);
      float fontSize = getProperties().get(Properties.FONT_SIZE, Float.class);
      getElement(TEXT)
          .getProperties()
          .set(Properties.SIZE, value)
          .set(Properties.POSITION, new Vector2f(fontSize / 4f, (size.y - fontSize) / 1.1f));
    }
    if (property == Properties.FONT_SIZE) {
      Vector2f size = getProperties().get(Properties.SIZE, Vector2f.class);
      float fontSize = getProperties().get(Properties.FONT_SIZE, Float.class);
      getElement(TEXT)
          .getProperties()
          .set(Properties.POSITION, new Vector2f(fontSize / 4f, (size.y - fontSize) / 1.1f));
    }
  }

  /**
   * Updates the Section, this method is called once every update, noting to do in this
   * implementation
   *
   * @param elapsedTime the elapsed time since last update in seconds
   */
  @Override
  public void update(double elapsedTime) {}

  /**
   * Retrieves the current text of this Button
   *
   * @return the current text of this Button
   */
  public String getText() {
    return ((TextLabel) getElement(TEXT)).getText();
  }
}
