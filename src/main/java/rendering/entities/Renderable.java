/*
 * Copyright (c) 2022, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering.entities;

import rendering.Texture;
import rendering.data.Quad;

public class Renderable {

  private final Quad quad;
  private final Texture texture;
  private int color;

  public Renderable(Texture texture) {
    this.texture = texture;
    this.quad = new Quad();
  }

  public Texture getTexture() {
    return texture;
  }

  public int getColor() {
    return color;
  }

  public void link(Transform transform) {
    quad.linkTransform(transform);
  }

  public void cleanUp() {
    texture.cleanup();
  }

  public Quad getQuad() {
    return quad;
  }
}
