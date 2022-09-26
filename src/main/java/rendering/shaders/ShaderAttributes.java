/*
 * Copyright (c) 2022, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering.shaders;

import static org.lwjgl.opengl.GL11.GL_FLOAT;

public class ShaderAttributes {
  public static final ShaderAttribute POSITION = new ShaderAttribute(0, "position", GL_FLOAT, 2);
  public static final ShaderAttribute SCALE = new ShaderAttribute(1, "scale", GL_FLOAT, 1);
  public static final ShaderAttribute ROTATION = new ShaderAttribute(2, "rotation", GL_FLOAT, 1);
}
