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

  public void applyTo(Movable movable) {
    movable.setAngularAcceleration(
        movable.getAngularAcceleration() + w / movable.getMomentOfInertia());
  }

  public void stopApplyingTo(Movable movable) {
    movable.setAngularAcceleration(
        movable.getAngularAcceleration() - w / movable.getMomentOfInertia());
  }
}
