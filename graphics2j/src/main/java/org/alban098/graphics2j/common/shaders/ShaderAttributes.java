/*
 * Copyright (c) 2022-2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package org.alban098.graphics2j.common.shaders;

/** A class off all attributes used in the Engine */
public final class ShaderAttributes {

  /** Empty private constructor to prevent instantiation */
  private ShaderAttributes() {}

  public static final ShaderAttribute VERTEX = new ShaderAttribute(0, "vertex", 2, Float.class);
  /** Contains the vertex index, used to retrieve Transforms and other data from SSBOs */
  public static final ShaderAttribute UV = new ShaderAttribute(1, "uv", 2, Float.class);
  /** Contains the vertex index, used to retrieve Transforms and other data from SSBOs */
  public static final ShaderAttribute TRANSFORM_INDEX =
      new ShaderAttribute(2, "transformIndex", 1, Integer.class);

  public static final ShaderAttribute INDEX = new ShaderAttribute(0, "vertexId", 1, Integer.class);

  /** Contains the offsets of a character in the texture atlas, used for font rendering */
  public static final ShaderAttribute TEXT_TEXTURE_POS =
      new ShaderAttribute(1, "uvPos", 2, Float.class);
  /** Contains the size of a character in the texture atlas, used for font rendering */
  public static final ShaderAttribute TEXT_TEXTURE_SIZE =
      new ShaderAttribute(2, "uvSize", 2, Float.class);
  /** Contains the color of a quad with alpha */
  public static final ShaderAttribute COLOR_ATTRIBUTE =
      new ShaderAttribute(3, "color", 4, Float.class);
  /** Contains the starting point of a line, in pixels */
  public static final ShaderAttribute LINE_START =
      new ShaderAttribute(1, "lineStart", 2, Float.class);
  /** Contains the ending point of a line, in pixels */
  public static final ShaderAttribute LINE_END = new ShaderAttribute(2, "lineEnd", 2, Float.class);
}
