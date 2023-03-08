/*
 * Copyright (c) 2022-2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package org.alban098.engine2j.example.entities;

import org.alban098.engine2j.common.components.RenderElement;
import org.alban098.engine2j.common.components.Transform;
import org.alban098.engine2j.common.shaders.ShaderAttributes;
import org.alban098.engine2j.entities.Entity;
import org.joml.Vector2f;
import org.joml.Vector4f;

public class ExampleColoredEntity implements Entity {

  private final Vector4f color;
  private Transform transform;
  private RenderElement renderable;

  public ExampleColoredEntity(Vector4f color) {
    this(new Vector2f(), 1, color);
  }

  public ExampleColoredEntity(Vector2f position, float scale, Vector4f color) {
    super();
    this.color = new Vector4f(color.x, color.y, color.z, color.w);
    this.transform = new Transform(position, scale, 0);
    this.renderable = new RenderElement(color);
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

  public void update(double elapsedTime) {
    setColor(
        new Vector4f((float) Math.random(), (float) Math.random(), (float) Math.random(), 0.75f));
    transform.rotate((float) (0.1f * elapsedTime));
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
    return "example";
  }

  @Override
  public void cleanUp() {
    renderable.cleanUp();
    transform.cleanUp();
  }

  @Override
  public ExampleColoredEntity setRenderable(RenderElement renderable) {
    if (this.renderable != null) {
      this.renderable.cleanUp();
    }
    this.renderable = renderable;
    return this;
  }

  @Override
  public ExampleColoredEntity setTransform(Transform transform) {
    if (this.transform != null) {
      this.transform.cleanUp();
    }
    this.transform = transform;
    return this;
  }
}
