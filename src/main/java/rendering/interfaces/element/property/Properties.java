/*
 * Copyright (c) 2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering.interfaces.element.property;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

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

  private final Object defaultValue;
  private final Class<?> type;

  Properties(Object defaultValue) {
    this.defaultValue = defaultValue;
    if (defaultValue == null) {
      this.type = Object.class;
    } else {
      this.type = defaultValue.getClass();
    }
  }

  public Object getDefaultValue() {
    return defaultValue;
  }

  public Class<?> getType() {
    return type;
  }
}
