/*
 * Copyright (c) 2022-2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package org.alban098.engine2j.objects.entities.component;

import org.alban098.engine2j.objects.entities.Entity;

/** Represents an abstraction of a Component that can be attached to an {@link Entity} */
public abstract class Component {

  /** Standard cleanup routine, must clear the component from memory */
  public abstract void cleanUp();

  /**
   * Updates the Component
   *
   * @param entity the parent {@link Entity} of the Component
   * @param elapsedTime the elapsed time since last update in seconds
   */
  public abstract void update(Entity entity, double elapsedTime);
}
