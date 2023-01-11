/*
 * Copyright (c) 2022-2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering.debug.component;

import rendering.scene.entities.component.Component;

/**
 * An abstraction of a Debug Interface that will display information about a {@link Component}
 *
 * @param <T> the type of {@link Component} to be displayed
 */
public interface ComponentDebugInterface<T extends Component> {

  /**
   * Returns the class type of the {@link Component}
   *
   * @return the class type of the {@link Component}
   */
  Class<T> getComponentClass();

  /**
   * Returns the name of the {@link Component}
   *
   * @return the name of the {@link Component}
   */
  String getDisplayName();

  /**
   * Draws the interface into its container
   *
   * @param component the {@link Component} to display in the interface
   * @return true if successfully drawn, false otherwise
   */
  boolean draw(Component component);
}
