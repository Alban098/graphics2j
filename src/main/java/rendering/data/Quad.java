/*
 * Copyright (c) 2022, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering.data;

import java.util.HashMap;
import java.util.Map;
import rendering.entities.Transform;
import rendering.shaders.ShaderAttribute;

public class Quad {

  public static final int VERTICES_DIM = 2;
  public static final int NB_INDICES = 6;
  public static final int NB_VERTEX = 4;
  public static final Float[] VERTICES = {-.5f, .5f, .5f, .5f, .5f, -.5f, -.5f, -.5f};
  public static final Integer[] INDICES = {0, 1, 2, 2, 3, 0};

  private final Map<ShaderAttribute, Number[]> attributes;
  private Transform transform;

  public Quad() {
    attributes = new HashMap<>();
    transform = Transform.NULL;
  }

  public void linkTransform(Transform transform) {
    this.transform = transform;
  }

  public void setAttribute(ShaderAttribute attribute, Number[] value) {
    attributes.put(attribute, value);
  }

  public Number[] get(ShaderAttribute attribute) {
    if (attribute.equals(ShaderAttribute.POSITION)) {
      return VERTICES;
    } else if (attribute.equals(ShaderAttribute.TRANSFORM)) {
      return transform.toArray();
    }
    return attributes.get(attribute);
  }
}
