/*
 * Copyright (c) 2022, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering.shaders;

public class ShaderAttributes {

  public static final ShaderAttribute INDEX = new ShaderAttribute(0, "vertexId", 1);
  public static final ShaderAttribute TRANSFORMS = new ShaderAttribute(0, "transforms", 16);
  public static final ShaderAttribute COLOR_ATTRIBUTE = new ShaderAttribute(1, "color", 3);
}
