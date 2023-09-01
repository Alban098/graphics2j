package org.alban098.graphics2j.common.shaders.data;

import static org.lwjgl.opengl.GL11C.GL_TRIANGLES;

public class Primitive {

  public static final Primitive QUAD = new Primitive(
          GL_TRIANGLES,
          6,
          6,
          new float[] {
                  -0.5f, 0.5f,
                  -0.5f, -0.5f,
                  0.5f, 0.5f,
                  0.5f, 0.5f,
                  -0.5f, -0.5f,
                  0.5f, -0.5f
          },
          new float[] {
                  0, 0,
                  0, 1,
                  1, 0,
                  1, 0,
                  0, 1,
                  1, 1
          },
          new int[] {0, 1, 2, 2, 3, 1});

  public static final Primitive BIG_QUAD = new Primitive(
          GL_TRIANGLES,
          4,
          6,
          new float[] {
                  -1f, -1f,
                  -1f,  1f,
                   1f,  1f,
                   1f, -1f,
          },
          new float[] {
                  0, 1,
                  0, 0,
                  1, 0,
                  1, 1
          },
          new int[] {0, 1, 2, 0, 2, 3});

  public final int type;
  public final int verticesCount;
  public final int indicesCount;
  public final float[] vertices;
  public final float[] uv;
  public final int[] indices;

  public Primitive(int type, int verticesCount, int indicesCount, float[] vertices, float[] uv, int[] indices) {
    this.type = type;
    this.verticesCount = verticesCount;
    this.indicesCount = indicesCount;
    this.vertices = vertices;
    this.uv = uv;
    this.indices = indices;
  }
}
