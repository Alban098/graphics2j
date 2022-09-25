/*
 * Copyright (c) 2022, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering.data;

import java.util.Map;
import rendering.entities.component.Transform;
import rendering.shaders.ShaderAttribute;
import rendering.shaders.ShaderAttributes;

public class Quad {

  public static final int VERTICES_DIM = 2;
  public static final int NB_INDICES = 6;
  public static final int NB_VERTEX = 4;
  public static final Float[] VERTICES = {-.5f, .5f, .5f, .5f, .5f, -.5f, -.5f, -.5f};
  public static final Integer[] INDICES = {0, 1, 2, 2, 3, 0};

  private final Map<ShaderAttribute, Number[]> attributes;
  private Transform transform;

  public Quad(Map<ShaderAttribute, Number[]> attributes) {
    this.attributes = attributes;
    this.transform = Transform.NULL;
  }

  public void linkTransform(Transform transform) {
    this.transform = transform;
  }

  public Number[] get(ShaderAttribute attribute) {
    if (attribute.equals(ShaderAttributes.POSITION)) {
      return VERTICES;
    } else if (attribute.equals(ShaderAttributes.TRANSFORM)) {
      return transform.toArray();
    }
    return attributes.get(attribute);
  }
}
