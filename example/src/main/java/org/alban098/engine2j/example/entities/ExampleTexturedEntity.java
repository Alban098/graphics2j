/*
 * Copyright (c) 2022-2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package org.alban098.engine2j.example.entities;

import org.alban098.engine2j.common.components.RenderElement;
import org.alban098.engine2j.common.components.Transform;
import org.alban098.engine2j.common.shaders.data.Texture;
import org.alban098.engine2j.entities.Entity;
import org.joml.Vector2f;

public class ExampleTexturedEntity implements Entity {

  private Transform transform;
  private RenderElement renderable;

  public ExampleTexturedEntity(Texture texture) {
    this(new Vector2f(), 1, texture);
  }

  public ExampleTexturedEntity(Vector2f position, float scale, Texture texture) {
    super();
    this.transform = new Transform(position, scale, 0);
    this.renderable = new RenderElement(texture);
  }

  public void update(double elapsedTime) {
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
  public ExampleTexturedEntity setRenderable(RenderElement renderable) {
    if (this.renderable != null) {
      this.renderable.cleanUp();
    }
    this.renderable = renderable;
    return this;
  }

  @Override
  public ExampleTexturedEntity setTransform(Transform transform) {
    if (this.transform != null) {
      this.transform.cleanUp();
    }
    this.transform = transform;
    return this;
  }
}
