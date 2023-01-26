/*
 * Copyright (c) 2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package org.alban098.engine2j.core.objects.interfaces.element.property;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

/**
 * The set of all possible properties an {@link
 * org.alban098.engine2j.core.objects.interfaces.element.UIElement} can have
 */
public enum Properties {
  CORNER_RADIUS(0f),
  BORDER_WIDTH(0f),
  BACKGROUND_COLOR(new Vector4f()),
  BORDER_COLOR(new Vector3f()),
  POSITION(new Vector2f()),
  SIZE(new Vector2f()),
  BACKGROUND_TEXTURE(null),
  FONT_SIZE(16f),
  FONT_FAMILY("Candara"),
  FONT_COLOR(new Vector4f(1)),
  FONT_WIDTH(0.5f),
  FONT_BLUR(0.2f),
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
