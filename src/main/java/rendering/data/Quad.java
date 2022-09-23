/*
 * Copyright (c) 2022, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering.data;

import static org.joml.Math.cos;
import static org.joml.Math.sin;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.joml.Matrix2f;
import org.joml.Vector2f;
import rendering.entities.Transform;
import rendering.shaders.VertexAttribute;

public class Quad {

  public static final int VERTICES_DIM = 2;
  public static final int NB_INDICES = 6;
  public static final int NB_VERTEX = 4;

  private static final Float[] VERTICES = {-.5f, .5f, .5f, .5f, .5f, -.5f, -.5f, -.5f};
  private static final Float[] TEX_COORDS = {0f, 0f, 1f, 0f, 1f, 1f, 0f, 1f};
  private static final Integer[] INDICES = {0, 1, 2, 2, 3, 0};

  private final Float[] position = Arrays.copyOf(VERTICES, VERTICES_DIM * NB_VERTEX);
  private final Integer[] indices = Arrays.copyOf(INDICES, NB_INDICES);

  private final Matrix2f scaleTransform = new Matrix2f();
  private final Matrix2f rotationTransform = new Matrix2f();
  private final Vector2f vertexBuffer = new Vector2f();

  private final Map<VertexAttribute, Number[]> attributes;
  private float scale;
  private float rotation;

  public Quad() {
    attributes = new HashMap<>();
  }

  public void setTransform(Transform transform) {
    if (scale != transform.getScale()) {
      scale = transform.getScale();
      scaleTransform.set(scale, 0, 0, scale);
    }
    if (rotation != transform.getRotation()) {
      rotation = transform.getRotation();
      float cos = cos(rotation);
      float sin = sin(rotation);
      rotationTransform.set(cos, -sin, sin, cos);
    }

    for (int i = 0; i < VERTICES.length; i += 2) {
      vertexBuffer
          .set(VERTICES[i], VERTICES[i + 1])
          .mul(scaleTransform)
          .mul(rotationTransform)
          .add(transform.getDisplacement());
      position[i] = vertexBuffer.x;
      position[i + 1] = vertexBuffer.y;
    }
  }

  public void setIndices(int vertexOffset) {
    if (indices[0] != INDICES[0] + vertexOffset * NB_VERTEX) {
      for (int i = 0; i < INDICES.length; i++) {
        indices[i] = INDICES[i] + vertexOffset * NB_VERTEX;
      }
    }
  }

  public Float[] getPosition() {
    return position;
  }

  public Integer[] getIndices() {
    return indices;
  }

  public Float[] getTexCoords() {
    return TEX_COORDS;
  }

  public void setAttribute(VertexAttribute attribute, Number[] value) {
    attributes.put(attribute, value);
  }

  public Number[] get(VertexAttribute attribute) {
    if (attribute.equals(VertexAttribute.POSITION)) {
      return getPosition();
    } else if (attribute.equals(VertexAttribute.TEXTURE_COORDINATES)) return getTexCoords();
    return attributes.get(attribute);
  }
}
