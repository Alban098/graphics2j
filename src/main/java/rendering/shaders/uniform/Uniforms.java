/*
 * Copyright (c) 2022-2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering.shaders.uniform;

/**
 * An enum off all uniforms available to the Engine. Will be rewritten to allow for expandability
 */
public enum Uniforms {
  VIEW_MATRIX("viewMatrix"),
  PROJECTION_MATRIX("projectionMatrix"),
  TIME_MS("timeMs"),
  COLOR("color"),
  TEXTURED("textured"),
  CLICKED("clicked"),
  HOVERED("hovered"),
  VIEWPORT("viewport"),
  RADIUS("radius"),
  FONT_BLUR("fontBlur"),
  FONT_WIDTH("fontWidth"),
  BORDER_WIDTH("borderWidth"),
  BORDER_COLOR("borderColor"),
  LINE_WIDTH("lineWidth");

  /** The name of the uniform */
  private final String name;

  /**
   * Create a new Uniform
   *
   * @param name the name of the Uniform
   */
  Uniforms(String name) {
    this.name = name;
  }

  /**
   * Returns the name of the Uniform
   *
   * @return the name of the Uniform
   */
  public String getName() {
    return name;
  }
}
