/*
 * Copyright (c) 2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package org.alban098.graphics2j.interfaces.components.property;

import org.alban098.graphics2j.interfaces.components.UIElement;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

/** The set of all possible properties an {@link UIElement} can have */
public enum Properties {

  /** The corner radius in pixels can be a non integer, but must be positive */
  CORNER_RADIUS(0f),
  /** The width of a border around the element in pixels */
  BORDER_WIDTH(0f),
  /** The background color of an element, as RGBA with components ranging from 0 to 1 */
  BACKGROUND_COLOR(new Vector4f()),
  /**
   * The background color of the border of an element, as RGB with components ranging from 0 to 1
   */
  BORDER_COLOR(new Vector3f()),
  /** The position of the top left corner of an element or user interface in pixels */
  POSITION(new Vector2f()),
  /** The size of an element or user interface in pixels */
  SIZE(new Vector2f()),
  /** The background texture of an element */
  BACKGROUND_TEXTURE(null),
  /** The size of a font in pixels */
  FONT_SIZE(16f),
  /** The family of a font */
  FONT_FAMILY("Candara"),
  /** The color of a font, as RGBA with components ranging from 0 to 1 */
  FONT_COLOR(new Vector4f(1)),
  /**
   * The width of the font, used as a threshold when decoding SDF font ranging from 0 to 1, default
   * is 0.5
   */
  FONT_WIDTH(0.5f),
  /** The amount of blurring at the edge of a character ranging from 0 to 1, default is 0.2 */
  FONT_BLUR(0.2f),
  /** The width of a line in pixels */
  LINE_WIDTH(1f);

  /** The default value of the property */
  private final Object defaultValue;
  /** The type of the property */
  private final Class<?> type;

  /**
   * Create a new Property with its associated default value
   *
   * @param defaultValue the property's default value
   */
  Properties(Object defaultValue) {
    this.defaultValue = defaultValue;
    if (defaultValue == null) {
      this.type = Object.class;
    } else {
      this.type = defaultValue.getClass();
    }
  }

  /**
   * Returns the Property's default value
   *
   * @return the Property's default value
   */
  public Object getDefaultValue() {
    return defaultValue;
  }

  /**
   * Returns the Property's type
   *
   * @return the Property's type
   */
  public Class<?> getType() {
    return type;
  }
}
