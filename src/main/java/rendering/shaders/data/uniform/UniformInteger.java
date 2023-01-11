/*
 * Copyright (c) 2022-2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering.shaders.data.uniform;

import java.util.Objects;
import org.lwjgl.opengl.GL20;

/** A concrete implementation of {@link Uniform} storing {@link Integer} */
public final class UniformInteger extends Uniform<Integer> {

  /**
   * Create a new Uniform of type int
   *
   * @param name name of the uniform, must be the same as in the Shader program
   */
  public UniformInteger(String name, int defaultValue) {
    super(name, defaultValue);
    this.currentValue = 0;
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
   * @return "int"
   */
  @Override
  public String getType() {
    return "int";
  }

  /**
   * Loads a value into the Uniform
   *
   * @param value the value to load
   */
  public void load(Integer value) {
    if (!Objects.equals(currentValue, value)) {
      GL20.glUniform1i(super.getLocation(), value);
      currentValue = value;
    }
  }
}
