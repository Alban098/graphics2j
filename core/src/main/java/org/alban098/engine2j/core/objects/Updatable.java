/*
 * Copyright (c) 2022-2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package org.alban098.engine2j.core.objects;

import org.alban098.engine2j.core.Engine;

/** Represents an Object that can be updated by a {@link Engine} */
public interface Updatable {

  /**
   * This method is called by the engine at each update, should call {@link
   * Updatable#update(double)} to ensure logic with {@link
   * org.alban098.engine2j.core.objects.entities.Entity} and {@link
   * org.alban098.engine2j.core.objects.entities.component.Component}
   *
   * @param elapsedTime the time elapsed since last update
   */
  void updateInternal(double elapsedTime);

  /**
   * This method should be called by {@link Updatable#updateInternal(double)} and do subsequent
   * logic
   *
   * @param elapsedTime the time elapsed since last update
   */
  void update(double elapsedTime);
}
