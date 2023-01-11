/*
 * Copyright (c) 2022-2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering.shaders.data.uniform;

import java.util.Objects;
import org.lwjgl.opengl.GL20;

/** A concrete implementation of {@link Uniform} storing {@link Float} */
public final class UniformFloat extends Uniform<Float> {

  /**
   * Create a new Uniform of type float
   *
   * @param name name of the uniform, must be the same as in the Shader program
   * @param defaultValue the default value of the Uniform
   */
  public UniformFloat(String name, float defaultValue) {
    super(name, defaultValue);
    this.defaultValue = defaultValue;
    this.currentValue = 0f;
  }

  /**
   * Returns the dimension of the data stored in this Uniform
   *
   * @return the dimension of the data stored in this Uniform
   */
  @Override
  public int getDimension() {
    return 4;
  }

  /**
   * Gets the formatted type of the data stored in this Uniform
   *
   * @return "float"
   */
  @Override
  public String getType() {
    return "float";
  }

  /**
   * Loads a value into the Uniform
   *
   * @param value the value to load
   */
  public void load(Float value) {
    if (!Objects.equals(currentValue, value)) {
      GL20.glUniform1f(super.getLocation(), value);
      currentValue = value;
    }
  }
}
