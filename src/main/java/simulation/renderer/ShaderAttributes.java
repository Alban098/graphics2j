/*
 * Copyright (c) 2022, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package simulation.renderer;

import static org.lwjgl.opengl.GL11.GL_FLOAT;

import rendering.shaders.ShaderAttribute;

public class ShaderAttributes {

  public static final ShaderAttribute COLOR_ATTRIBUTE =
      new ShaderAttribute(3, "color", GL_FLOAT, 3);
}
