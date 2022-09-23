/*
 * Copyright (c) 2022, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering;

import static org.joml.Math.cos;
import static org.joml.Math.sin;

import org.joml.Matrix2f;
import org.joml.Vector2f;
import rendering.entities.Transform;

public class Quad {

  public static final int VERTICES_DIM = 2;
  public static final int NB_INDICES = 6;
  public static final int NB_VERTEX = 4;

  private static final float[] BASE_POS = {-.5f, .5f, .5f, .5f, .5f, -.5f, -.5f, -.5f};
  private static final float[] TEX_COORDS = {0f, 0f, 1f, 0f, 1f, 1f, 0f, 1f};
  private static final int[] INDICES = {0, 1, 2, 2, 3, 0};

  private final float[] position = new float[VERTICES_DIM * NB_VERTEX];
  private final int[] indices = new int[NB_INDICES];

  private final Matrix2f scaleTransform = new Matrix2f();
  private final Matrix2f rotationTransform = new Matrix2f();
  private final Vector2f vertexBuffer = new Vector2f();

  private float scale;
  private float rotation;

  public Quad() {}

  public void setTransform(Transform transform) {
    if (scale != transform.getScale() || rotation != transform.getRotation()) {
      scale = transform.getScale();
      rotation = transform.getRotation();
      float cos = cos(rotation);
      float sin = sin(rotation);

      scaleTransform.set(scale, 0, 0, scale);
      rotationTransform.set(cos, -sin, sin, cos);

      for (int i = 0; i < BASE_POS.length; i += 2) {
        vertexBuffer
            .set(BASE_POS[i], BASE_POS[i + 1])
            .mul(scaleTransform)
            .mul(rotationTransform)
            .add(transform.getDisplacement());
        position[i] = vertexBuffer.x;
        position[i + 1] = vertexBuffer.y;
      }
    }
  }

  public void setIndices(int vertexOffset) {
    for (int i = 0; i < INDICES.length; i++) {
      indices[i] = INDICES[i] + vertexOffset * NB_VERTEX;
    }
  }

  public float[] getPosition() {
    return position;
  }

  public int[] getIndices() {
    return indices;
  }

  public float[] getTexCoords() {
    return TEX_COORDS;
  }
}
