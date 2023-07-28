/*
 * Copyright (c) 2022-2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package org.alban098.graphics2j.example.entities;

import org.alban098.graphics2j.common.shaders.ShaderAttributes;
import org.joml.Vector2f;
import org.joml.Vector4f;

public class ColoredEntity extends UpdatableEntity {

  private final Vector4f color;

  public ColoredEntity(Vector2f position, Vector2f scale, float rotation, Vector4f color) {
    super(position, scale, rotation, 10, "Colored", color);
    this.color = new Vector4f(color.x, color.y, color.z, color.w);
  }

  public void setColor(Vector4f color) {
    this.color.set(color);
    if (getRenderableComponent() != null) {
      getRenderableComponent()
          .getRenderable()
          .setAttributeValue(ShaderAttributes.COLOR_ATTRIBUTE, color);
    }
  }

  @Override
  public void update(double elapsedTime) {
    setColor(
        new Vector4f((float) Math.random(), (float) Math.random(), (float) Math.random(), 0.75f));
  }
}
