/*
 * Copyright (c) 2022-2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package org.alban098.graphics2j.example.entities;

import org.alban098.common.Transform;
import org.alban098.graphics2j.common.components.RenderElement;
import org.alban098.graphics2j.common.shaders.data.Texture;
import org.joml.Vector2f;

public class TexturedEntity extends UpdatableEntity {

  private final Transform transform;
  private final RenderElement renderable;

  public TexturedEntity(Vector2f position, Vector2f scale, float rotation, Texture texture) {
    super(10, scale);
    this.transform = new Transform(position, scale, rotation);
    this.renderable = new RenderElement(texture);
  }

  @Override
  public void update(double elapsedTime) {
    transform.commit();
  }

  @Override
  public RenderElement getRenderable() {
    return renderable;
  }

  @Override
  public Transform getTransform() {
    return transform;
  }

  @Override
  public String getName() {
    return "Textured";
  }
}
