/*
 * Copyright (c) 2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package org.alban098.physics2j;

import org.joml.Vector2f;

public class Force {

  private final Vector2f components = new Vector2f();

  public Force(float x, float y) {
    components.set(x, y);
  }

  public void combine(Force force) {
    components.add(force.components);
  }

  public float applyTo(PhysicsComponent physical, Vector2f offset) {
    physical
        .getAcceleration()
        .add(components.x / physical.getMass(), components.y / physical.getMass());

    /* Torque is defined as |r|*|F|*sinΘ, with
    - r the offset vector between, the pivot and application point
    - F the force vector
    - Θ the angle between the 2 vectors

    sinΘ = |r x F| / (|r|*|F|) as defined by the cross product formula

    therefore
     Torque = |r|*|F| * |r x F| / (|r|*|F|)
     Torque = |r x F|
    */

    return (offset.x * components.y - offset.y * components.x);
  }
}
