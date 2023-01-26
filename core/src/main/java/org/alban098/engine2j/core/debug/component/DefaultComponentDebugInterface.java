/*
 * Copyright (c) 2022-2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package org.alban098.engine2j.core.debug.component;

import org.alban098.engine2j.core.objects.entities.component.Component;

/**
 * A concrete implementation of {@link ComponentDebugInterface} in charge of displaying {@link
 * Component}s, it's the default one when none are defined
 */
public final class DefaultComponentDebugInterface implements ComponentDebugInterface<Component> {

  /**
   * Returns the class type of the {@link Component}
   *
   * @return Component.class
   */
  @Override
  public Class<Component> getComponentClass() {
    return Component.class;
  }

  /**
   * Returns the name of the {@link Component}
   *
   * @return "Default Component"
   */
  @Override
  public String getDisplayName() {
    return "Default Component";
  }

  /**
   * Draws the interface into its container, do nothing here
   *
   * @param component the {@link Component} to display in the interface
   * @return false
   */
  @Override
  public boolean draw(Component component) {
    return false;
  }
}
