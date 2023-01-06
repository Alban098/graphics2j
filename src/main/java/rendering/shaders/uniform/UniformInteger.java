/*
 * Copyright (c) 2022-2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering.shaders.uniform;

import java.util.Objects;
import org.lwjgl.opengl.GL20;

public class UniformInteger extends Uniform<Integer> {

  private final int defaultValue;

  /**
   * Create a new Uniform of type int
   *
   * @param name name of the uniform, must be the same as in the Shader program
   */
  public UniformInteger(String name, int defaultValue) {
    super(name);
    this.defaultValue = defaultValue;
    this.currentValue = 0;
  }

  @Override
  public Object getDefault() {
    return defaultValue;
  }

  public void loadDefault() {
    load(defaultValue);
  }

  @Override
  public int getDimension() {
    return 1;
  }

  @Override
  public String getType() {
    return "int";
  }

  public void load(Integer value) {
    if (!Objects.equals(currentValue, value)) {
      GL20.glUniform1i(super.getLocation(), value);
      currentValue = value;
    }
  }
}
