/*
 * Copyright (c) 2022-2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package org.alban098.engine2j.core.shaders.data.uniform;

/** A class off all uniforms name available to the Engine */
public final class Uniforms {

  /** Empty private constructor to prevent instantiation */
  private Uniforms() {}

  /**
   * Contains the view matrix, used to transform from world space to view space (relative to the
   * camera)
   *
   * <p>type : mat4
   */
  public static final String VIEW_MATRIX = "viewMatrix";
  /**
   * Contains the projection matrix, used to transform from 3D view or world space to screen space
   *
   * <p>type : mat4
   */
  public static final String PROJECTION_MATRIX = "projectionMatrix";
  /**
   * Contains the current time en seconds, can be used to animate element in the shader
   *
   * <p>type : float
   */
  public static final String TIME = "time";
  /**
   * Contains a color with alpha component, components go from 0 to 1
   *
   * <p>type : vec4
   */
  public static final String COLOR = "color";
  /**
   * Contains a flag indicating if the quad should be textured or not
   *
   * <p>type : bool
   */
  public static final String TEXTURED = "textured";
  /**
   * Contains a flag indicating if the element is clicked or not (used for interfaces)
   *
   * <p>type : bool
   */
  public static final String CLICKED = "clicked";
  /**
   * Contains a flag indicating if the element is hovered or not (used for interfaces)
   *
   * <p>type : bool
   */
  public static final String HOVERED = "hovered";
  /**
   * Contains the dimension of the viewport in pixels
   *
   * <p>type : vec2
   */
  public static final String VIEWPORT = "viewport";
  /**
   * Contains the corner radius of an interface or interface element
   *
   * <p>type : float
   */
  public static final String RADIUS = "radius";
  /**
   * Contains the width of the blurring interval at the edge of a font character must be between 0
   * and 1
   *
   * <p>type : float
   */
  public static final String FONT_BLUR = "fontBlur";
  /**
   * Contains the width of a character, it specifies what is considered inside and should be opaque
   *
   * <p>type : float
   */
  public static final String FONT_WIDTH = "fontWidth";
  /**
   * Contains the width of the border of an interface or interface element, in pixels
   *
   * <p>type : float
   */
  public static final String BORDER_WIDTH = "borderWidth";
  /**
   * Contains the color of the border of an interface or interface element, without alpha channel
   *
   * <p>type : vec3
   */
  public static final String BORDER_COLOR = "borderColor";
  /**
   * Contains the width of a line inside an interface or interface element, in pixels
   *
   * <p>type : float
   */
  public static final String LINE_WIDTH = "lineWidth";
}
