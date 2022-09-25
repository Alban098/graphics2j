/*
 * Copyright (c) 2022, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering.entities.component;

import java.util.HashMap;
import java.util.Map;
import rendering.Texture;
import rendering.data.Quad;
import rendering.shaders.ShaderAttribute;

public class Renderable {

  private final Quad quad;
  private final Texture texture;
  private final Map<ShaderAttribute, Number[]> attributes;

  public Renderable() {
    this(null);
  }

  public Renderable(Texture texture) {
    this.texture = texture;
    this.attributes = new HashMap<>();
    this.quad = new Quad(this.attributes);
  }

  public Texture getTexture() {
    return texture;
  }

  public void setAttributes(ShaderAttribute attribute, Number[] color) {
    this.attributes.put(attribute, color);
  }

  public void link(Transform transform) {
    quad.linkTransform(transform);
  }

  public void cleanUp() {
    if (texture != null) {
      texture.cleanup();
    }
  }

  public Quad getQuad() {
    return quad;
  }
}
