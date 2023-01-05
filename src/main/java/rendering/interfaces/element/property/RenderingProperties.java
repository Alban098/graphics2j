/*
 * Copyright (c) 2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering.interfaces.element.property;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public class RenderingProperties {

  private final BiConsumer<Properties, Object> onChange;

  Map<Properties, RenderingProperty<?>> properties = new HashMap<>();

  public RenderingProperties(BiConsumer<Properties, Object> onChange) {
    this.onChange = onChange;
    for (Properties property : Properties.values()) {
      properties.put(property, new RenderingProperty<>(property, property.getDefaultValue()));
    }
  }

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

  public <T> T get(Properties property, Class<T> type) {
    return (T) properties.get(property).getValue();
  }
}
