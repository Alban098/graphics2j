/*
 * Copyright (c) 2022-2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package org.alban098.graphics2j.example.entities;

import org.alban098.common.Transform;
import org.alban098.graphics2j.common.components.RenderElement;
import org.alban098.graphics2j.common.shaders.ShaderAttributes;
import org.joml.Vector2f;
import org.joml.Vector4f;

public class ColoredEntity extends UpdatableEntity {

  private final Vector4f color;
  private final Transform transform;
  private final RenderElement renderable;

  public ColoredEntity(Vector2f position, Vector2f scale, float rotation, Vector4f color) {
    super(10, scale);
    this.color = new Vector4f(color.x, color.y, color.z, color.w);
    this.transform = new Transform(position, scale, rotation);
    this.renderable = new RenderElement(color);
  }

  public void setColor(Vector4f color) {
    this.color.set(color);
    if (getRenderable() != null) {
      getRenderable().setAttributeValue(ShaderAttributes.COLOR_ATTRIBUTE, color);
    }
  }

  @Override
  public void update(double elapsedTime) {
    setColor(
        new Vector4f((float) Math.random(), (float) Math.random(), (float) Math.random(), 0.75f));
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
    return "Colored";
  }
}
