/*
 * Copyright (c) 2022, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering.shaders;

import static org.lwjgl.opengl.GL11.GL_FLOAT;

import rendering.data.Quad;

public class ShaderAttributes {
  public static final ShaderAttribute POSITION =
      new ShaderAttribute(0, "position", GL_FLOAT, Quad.VERTICES_DIM, false);
  public static final ShaderAttribute TRANSFORM =
      new ShaderAttribute(1, "transform", GL_FLOAT, 4, true);
}
