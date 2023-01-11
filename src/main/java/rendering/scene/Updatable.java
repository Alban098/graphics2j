/*
 * Copyright (c) 2022-2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering.scene;

/** Represents an Object that can be updated by a {@link rendering.Engine} */
public interface Updatable {

  /**
   * This method is called by the engine at each update, should call {@link
   * Updatable#update(double)} to ensure logic with {@link rendering.scene.entities.Entity} and
   * {@link rendering.scene.entities.component.Component}
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
