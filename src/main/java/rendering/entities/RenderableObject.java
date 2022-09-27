/*
 * Copyright (c) 2022, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering.entities;

import org.joml.Vector3f;
import rendering.Texture;
import rendering.entities.component.Renderable;
import rendering.entities.component.Transform;
import rendering.shaders.ShaderAttribute;

public abstract class RenderableObject {

  protected final Transform transform;
  protected final Renderable renderable;

  public RenderableObject(Texture texture) {
    this(new Transform(), texture);
  }

  public RenderableObject(Vector3f color, ShaderAttribute shaderAttribute) {
    this(new Transform(), color, shaderAttribute);
  }

  public RenderableObject(Transform transform, Texture texture) {
    this.transform = transform;
    this.renderable = new Renderable(transform, texture);
  }

  public RenderableObject(Transform transform, Vector3f color, ShaderAttribute shaderAttribute) {
    this.transform = transform;
    this.renderable = new Renderable(transform);
    this.renderable.setAttributes(shaderAttribute, color);
  }

  protected abstract void update(double elapsedTime);

  public Renderable getRenderable() {
    return renderable;
  }

  public Transform getTransform() {
    return transform;
  }

  public void cleanUp() {
    renderable.cleanUp();
  }
}
