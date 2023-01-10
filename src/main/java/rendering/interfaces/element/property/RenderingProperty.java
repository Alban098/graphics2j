/*
 * Copyright (c) 2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering.interfaces.element.property;

/**
 * Represents an instance of a {@link Properties} used in a {@link RenderingProperties}
 *
 * @param <T> the type of the Property
 */
public final class RenderingProperty<T> {

  /** The {@link Properties} this is linked to */
  private final Properties identifier;
  /** The value of the Property */
  private T value;

  /**
   * Creates a new Instance of a Property
   *
   * @param identifier the {@link Properties} to link to
   * @param value the value of the Property
   */
  public RenderingProperty(Properties identifier, T value) {
    this.identifier = identifier;
    this.value = value;
  }

  /**
   * Sets the value of the Property
   *
   * @param value the new value of the Property
   */
  public void setValue(T value) {
    this.value = value;
  }

  /**
   * Gets the identifier of the Property
   *
   * @return the identifier of the Property
   */
  public Properties getIdentifier() {
    return identifier;
  }

  /**
   * Gets the current value of the Property
   *
   * @return the current value of the Property
   */
  public T getValue() {
    return value;
  }
}
