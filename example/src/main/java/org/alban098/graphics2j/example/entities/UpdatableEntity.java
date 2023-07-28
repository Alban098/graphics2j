/*
 * Copyright (c) 2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package org.alban098.graphics2j.example.entities;

import org.alban098.common.Entity;
import org.alban098.graphics2j.common.Renderable;
import org.alban098.graphics2j.common.RenderableComponent;
import org.alban098.graphics2j.common.components.RenderElement;
import org.alban098.graphics2j.common.shaders.data.Texture;
import org.alban098.physics2j.Physical;
import org.alban098.physics2j.PhysicsComponent;
import org.joml.Vector2f;
import org.joml.Vector4f;

public abstract class UpdatableEntity extends Entity implements Renderable, Physical {

  // Shortcut to components
  private final RenderableComponent renderableComponent;
  private final PhysicsComponent physicsComponent;

  public abstract void update(double elapsedTime);

  public UpdatableEntity(
      Vector2f position, Vector2f scale, float rotation, float mass, String name, Texture texture) {
    this(position, scale, rotation, mass, name, new RenderElement(texture));
  }

  public UpdatableEntity(
      Vector2f position, Vector2f scale, float rotation, float mass, String name, Vector4f color) {
    this(position, scale, rotation, mass, name, new RenderElement(color));
  }

  private UpdatableEntity(
      Vector2f position,
      Vector2f scale,
      float rotation,
      float mass,
      String name,
      RenderElement renderElement) {
    super(position, scale, rotation);
    this.renderableComponent = new RenderableComponent(renderElement, name);
    this.physicsComponent = new PhysicsComponent(mass, scale);
    this.addComponent(renderableComponent);
    this.addComponent(physicsComponent);
  }

  @Override
  public RenderableComponent getRenderableComponent() {
    return renderableComponent;
  }

  @Override
  public PhysicsComponent getPhysicsComponent() {
    return physicsComponent;
  }

  @Override
  public void collisionCallback(Physical other, Vector2f contactPoint) {
    // Do Nothing
  }
}
