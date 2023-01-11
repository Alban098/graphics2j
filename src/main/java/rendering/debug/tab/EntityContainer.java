/*
 * Copyright (c) 2022-2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering.debug.tab;

import rendering.scene.entities.Entity;

/** An abstraction allowing a {@link DebugTab} to hold a reference to an {@link Entity} */
public interface EntityContainer {

  /**
   * Sets the currently selected {@link Entity} held by the Tab
   *
   * @param entity the new selected {@link Entity}
   */
  void setSelectedEntity(Entity entity);
}
