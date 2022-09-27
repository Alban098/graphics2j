/*
 * Copyright (c) 2022, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering.shaders.uniform;

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
    loadInteger(defaultValue);
  }

  public void loadInteger(int value) {
    if (currentValue != value) {
      GL20.glUniform1i(super.getLocation(), value);
      currentValue = value;
    }
  }
}
