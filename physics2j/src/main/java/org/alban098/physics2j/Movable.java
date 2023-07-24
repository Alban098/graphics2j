/*
 * Copyright (c) 2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package org.alban098.physics2j;

import org.alban098.common.Entity;
import org.joml.Vector2f;

public interface Movable extends Entity {

  Polygon getHitbox();

  default Vector2f getCenterOfMass() {
    return getHitbox().getCenter();
  }

  Vector2f getVelocity();

  void setVelocity(Vector2f velocity);

  Vector2f getAcceleration();

  void setAcceleration(Vector2f acceleration);

  void setAcceleration(float x, float y);

  float getAngularVelocity();

  void setAngularVelocity(float angularVelocity);

  float getAngularAcceleration();

  void setAngularAcceleration(float angularAcceleration);

  float getMass();

  default float getMomentOfInertia() {
    return getHitbox().getMomentOfInertia(getMass());
  }

  boolean isSubjectToStaticForces();
}
