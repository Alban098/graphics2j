/*
 * Copyright (c) 2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package org.alban098.physics2j;

public class Torque {

  private float w;

  public Torque(float w) {
    this.w = w;
  }

  public void combine(Torque torque) {
    w += torque.w;
  }

  public void applyTo(PhysicsComponent movable) {
    movable.setAngularAcceleration(
        movable.getAngularAcceleration() + w / movable.getMomentOfInertia());
  }

  public void add(float torque) {
    this.w += torque;
  }

  public void set(int torque) {
    this.w = torque;
  }
}
