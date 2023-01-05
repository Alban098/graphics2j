/*
 * Copyright (c) 2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering.interfaces.element.property;

public class RenderingProperty<T> {

  private final Properties identifier;
  private T value;

  public RenderingProperty(Properties identifier, T value) {
    this.identifier = identifier;
    this.value = value;
  }

  public void setValue(T value) {
    this.value = value;
  }

  public Properties getIdentifier() {
    return identifier;
  }

  public T getValue() {
    return value;
  }
}
