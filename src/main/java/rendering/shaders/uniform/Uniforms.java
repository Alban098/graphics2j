/*
 * Copyright (c) 2022, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering.shaders.uniform;

public enum Uniforms {
  VIEW_MATRIX("viewMatrix"),
  PROJECTION_MATRIX("projectionMatrix"),
  TIME_MS("timeMs"),
  COLOR("color"),
  TEXTURED("textured"),
  CLICKED("clicked"),
  HOVERED("hovered"),
  FOCUSED("focused"),
  DIMENSION("dimension"),
  RADIUS("radius"),
  FONT_BLUR("fontBlur"),
  FONT_WIDTH("fontWidth"),
  BORDER_WIDTH("borderWidth"),
  BORDER_COLOR("borderColor");

  private final String name;

  Uniforms(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }
}
