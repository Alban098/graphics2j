/*
 * Copyright (c) 2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package org.alban098.physics2j;

import org.alban098.common.Transform;
import org.joml.Vector2f;

public interface Physical {

  PhysicsComponent getPhysicsComponent();

  Transform getTransform();

  void collisionCallback(Physical other, Vector2f contactPoint);
}
