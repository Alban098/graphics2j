/*
 * Copyright (c) 2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package org.alban098.graphics2j.example.entities;

import org.alban098.common.Entity;
import org.alban098.graphics2j.common.Renderable;

public interface UpdatableEntity extends Entity, Renderable {

  void update(double elapsedTime);
}
