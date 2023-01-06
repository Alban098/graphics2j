/*
 * Copyright (c) 2022-2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering.shaders;

public class ShaderAttributes {

  public static final ShaderAttribute INDEX = new ShaderAttribute(0, "vertexId", 1, Integer.class);
  public static final ShaderAttribute TEXT_TEXTURE_POS =
      new ShaderAttribute(1, "uvPos", 2, Float.class);
  public static final ShaderAttribute TEXT_TEXTURE_SIZE =
      new ShaderAttribute(2, "uvSize", 2, Float.class);
  public static final ShaderAttribute COLOR_ATTRIBUTE =
      new ShaderAttribute(1, "color", 3, Float.class);
  public static final ShaderAttribute LINE_START =
      new ShaderAttribute(1, "lineStart", 2, Float.class);
  public static final ShaderAttribute LINE_END = new ShaderAttribute(2, "lineEnd", 2, Float.class);
}
