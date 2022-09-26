/*
 * Copyright (c) 2022, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering.entities.component;

import java.util.HashMap;
import java.util.Map;
import rendering.Texture;
import rendering.shaders.ShaderAttribute;
import rendering.shaders.ShaderAttributes;

public class Renderable {

  private final Texture texture;
  private final Map<ShaderAttribute, Number[]> attributes;
  private final Transform transform;

  public Renderable(Transform transform) {
    this(transform, null);
  }

  public Renderable(Transform transform, Texture texture) {
    this.texture = texture;
    this.attributes = new HashMap<>();
    this.transform = transform;
  }

  public Texture getTexture() {
    return texture;
  }

  public void setAttributes(ShaderAttribute attribute, Number[] color) {
    this.attributes.put(attribute, color);
  }

  public void cleanUp() {
    if (texture != null) {
      texture.cleanup();
    }
  }

  public Number[] get(ShaderAttribute attribute) {
    if (attribute.equals(ShaderAttributes.POSITION)) {
      return new Float[] {transform.getDisplacement().x, transform.getDisplacement().y};
    } else if (attribute.equals(ShaderAttributes.SCALE)) {
      return new Float[] {transform.getScale()};
    } else if (attribute.equals(ShaderAttributes.ROTATION)) {
      return new Float[] {transform.getRotation()};
    }
    return attributes.get(attribute);
  }
}
