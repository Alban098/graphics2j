/*
 * Copyright (c) 2022, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering.entities;

import org.joml.Vector3f;
import rendering.Texture;
import rendering.entities.component.Renderable;
import rendering.shaders.ShaderAttribute;

public abstract class RenderableObject {

  protected final Renderable renderable;

  public RenderableObject(Texture texture) {
    this.renderable = new Renderable(texture);
  }

  public RenderableObject(Vector3f color, ShaderAttribute shaderAttribute) {
    this.renderable = new Renderable();
    this.renderable.setAttributes(shaderAttribute, new Float[] {color.x, color.y, color.z});
  }

  protected abstract void update(double elapsedTime);

  public Renderable getRenderable() {
    return renderable;
  }

  public void cleanUp() {
    renderable.cleanUp();
  }
}
