/*
 * Copyright (c) 2022-2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package org.alban098.graphics2j.interfaces.components;

import org.alban098.graphics2j.interfaces.components.property.Properties;

/**
 * An implementation of {@link UIElement} representing a Section, not intractable, it's only purpose
 * is to contain other elements in a specified area
 */
public final class Section extends UIElement {

  /**
   * Updates the Section, this method is called once every update, noting to do in this
   * implementation
   *
   * @param elapsedTime the elapsed time since last update in seconds
   */
  @Override
  public void update(double elapsedTime) {}

  /**
   * Called every time a {@link Properties} of the Section is changed, noting to do in this
   * implementation
   *
   * @param property the changed {@link Properties}
   * @param value the new value
   */
  @Override
  protected void onPropertyChange(Properties property, Object value) {}
}
