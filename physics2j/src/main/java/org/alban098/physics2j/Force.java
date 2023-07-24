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

  public void applyTo(Movable movable) {
    movable
        .getAcceleration()
        .add(components.x / movable.getMass(), components.y / movable.getMass());
  }

  public void stopApplyingTo(Movable movable) {
    movable
        .getAcceleration()
        .sub(components.x / movable.getMass(), components.y / movable.getMass());
  }
}
