/*
 * Copyright (c) 2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package org.alban098.graphics2j.example.entities;

import org.alban098.common.Entity;
import org.alban098.graphics2j.common.Renderable;
import org.alban098.physics2j.Movable;
import org.alban098.physics2j.Polygon;
import org.joml.Vector2f;

public abstract class UpdatableEntity implements Entity, Renderable, Movable {

  private final Vector2f velocity = new Vector2f();
  private final Vector2f acceleration = new Vector2f();
  private float angularVelocity = 0;
  private float angularAcceleration = 0;
  private float mass;
  private final Polygon hitbox;

  public abstract void update(double elapsedTime);

  public UpdatableEntity(float mass, Vector2f scale) {
    this.mass = mass;
    this.hitbox = new Polygon();
    hitbox.addPoint(-scale.x / 2, -scale.y / 2);
    hitbox.addPoint(scale.x / 2, -scale.y / 2);
    hitbox.addPoint(-scale.x / 2, scale.y / 2);
    hitbox.addPoint(scale.x / 2, scale.y / 2);
  }

  @Override
  public Polygon getHitbox() {
    return hitbox;
  }

  @Override
  public Vector2f getVelocity() {
    return velocity;
  }

  @Override
  public Vector2f getAcceleration() {
    return acceleration;
  }

  @Override
  public float getMass() {
    return mass;
  }

  @Override
  public float getAngularVelocity() {
    return angularVelocity;
  }

  @Override
  public float getAngularAcceleration() {
    return angularAcceleration;
  }

  @Override
  public void setAngularVelocity(float angularVelocity) {
    this.angularVelocity = angularVelocity;
  }

  @Override
  public void setVelocity(Vector2f velocity) {
    this.velocity.set(velocity);
  }

  @Override
  public void setAcceleration(float x, float y) {
    this.acceleration.set(x, y);
  }

  @Override
  public void setAngularAcceleration(float angularAcceleration) {
    this.angularAcceleration = angularAcceleration;
  }

  @Override
  public void setAcceleration(Vector2f acceleration) {
    this.acceleration.set(acceleration);
  }

  @Override
  public boolean isSubjectToStaticForces() {
    return true;
  }
}
