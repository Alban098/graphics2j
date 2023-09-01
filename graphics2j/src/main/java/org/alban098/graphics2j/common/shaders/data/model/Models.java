package org.alban098.graphics2j.common.shaders.data.model;

public class Models {

  public static final Model QUAD = new Model(
          "Quad",
          Primitive.TRIANGLES,
          2,
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
          }
  );

  public static final Model POINT = new Model(
          "Point",
          Primitive.POINT,
          1,
          new float[] {0, 0},
          new float[] {0, 0}
  );
}
