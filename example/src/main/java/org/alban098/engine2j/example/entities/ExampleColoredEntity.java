/*
 * Copyright (c) 2022-2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package org.alban098.engine2j.example.entities;

import org.alban098.engine2j.core.objects.entities.Entity;
import org.alban098.engine2j.core.objects.entities.component.RenderableComponent;
import org.alban098.engine2j.core.objects.entities.component.TransformComponent;
import org.alban098.engine2j.core.shaders.ShaderAttributes;
import org.joml.Vector2f;
import org.joml.Vector4f;

public class ExampleColoredEntity extends Entity {

  private final Vector4f color;

  public ExampleColoredEntity() {
    this(new Vector2f(), 1, new Vector4f());
  }

  public ExampleColoredEntity(Vector2f position, float scale, Vector4f color) {
    super();
    this.color = new Vector4f(color.x, color.y, color.z, color.w);
    addComponent(new TransformComponent(position, scale, 0));
    addComponent(new RenderableComponent(color));
  }

  public Vector4f getColor() {
    return color;
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
  protected void cleanUp() {}
}
