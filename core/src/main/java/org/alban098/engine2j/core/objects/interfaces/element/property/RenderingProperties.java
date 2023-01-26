/*
 * Copyright (c) 2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package org.alban098.engine2j.core.objects.interfaces.element.property;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * Represents a set of {@link Properties} that can be linked to an {@link
 * org.alban098.engine2j.core.objects.interfaces.element.UIElement}
 */
public final class RenderingProperties {

  /** The callback for when a Property is updated */
  private final BiConsumer<Properties, Object> onChange;

  /** a Map of all the Properties of this set */
  private final Map<Properties, RenderingProperty<?>> properties = new HashMap<>();

  /**
   * Creates a new RenderingProperties and associate a callback to it
   *
   * @param onChange the callback for when a Property is changed
   */
  public RenderingProperties(BiConsumer<Properties, Object> onChange) {
    this.onChange = onChange;
    for (Properties property : Properties.values()) {
      properties.put(property, new RenderingProperty<>(property, property.getDefaultValue()));
    }
  }

  /**
   * Updates a Property
   *
   * @param property the {@link Properties} to update
   * @param value the new value to set
   * @return this object to chain calls
   * @param <T> the type of the value to set
   */
  public <T> RenderingProperties set(Properties property, T value) {
    RenderingProperty<T> renderingProperty = (RenderingProperty<T>) properties.get(property);
    if (renderingProperty != null) {
      renderingProperty.setValue(value);
    } else {
      properties.put(property, new RenderingProperty<>(property, value));
    }
    onChange.accept(property, value);
    return this;
  }

  /**
   * Retrieves the value of a property cast to a specified type
   *
   * @param property the {@link Properties} to retrieve
   * @param type The class of the type to cast to
   * @return the cast property
   * @param <T> the type to cast to
   */
  public <T> T get(Properties property, Class<T> type) {
    return (T) properties.get(property).getValue();
  }
}
