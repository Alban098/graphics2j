/*
 * Copyright (c) 2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package org.alban098.graphics2j.common.shaders.data.model;

import static org.lwjgl.opengl.GL11C.*;

public class Primitive {

  public static final Primitive TRIANGLES = new Primitive(GL_TRIANGLES, 6);
  public static final Primitive LINES = new Primitive(GL_LINES, 6);
  public static final Primitive POINT = new Primitive(GL_POINTS, 1);

  public final int type;
  public final int verticesCount;

  public Primitive(
      int type, int verticesCount) {
    this.type = type;
    this.verticesCount = verticesCount;
  }
}
