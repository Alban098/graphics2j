/*
 * Copyright (c) 2022-2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package org.alban098.engine2j.core.shaders.data.uniform;

import java.util.Objects;
import org.lwjgl.opengl.GL20;

/** A concrete implementation of {@link Uniform} storing {@link Boolean} */
public final class UniformBoolean extends Uniform<Boolean> {

  /**
   * Create a new Uniform
   *
   * @param name the name of the Uniform
   * @param defaultValue the default value of the Uniform
   */
  public UniformBoolean(String name, boolean defaultValue) {
    super(name, defaultValue);
    this.currentValue = false;
  }

  /**
   * Returns the dimension of the data stored in this Uniform
   *
   * @return the dimension of the data stored in this Uniform
   */
  @Override
  public int getDimension() {
    return 1;
  }

  /**
   * Gets the formatted type of the data stored in this Uniform
   *
   * @return "bool"
   */
  @Override
  public String getType() {
    return "bool";
  }

  /**
   * Loads a value into the Uniform
   *
   * @param value the value to load
   */
  public void load(Boolean value) {
    if (!Objects.equals(currentValue, value)) {
      GL20.glUniform1i(super.getLocation(), value ? 1 : 0);
      currentValue = value;
    }
  }
}
