/*
 * Copyright (c) 2022, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering.shaders.uniform;

import org.lwjgl.opengl.GL20;

public class UniformFloat extends Uniform<Float> {

  private final float defaultValue;

  /**
   * Create a new Uniform of type float
   *
   * @param name name of the uniform, must be the same as in the Shader program
   */
  public UniformFloat(String name, float defaultValue) {
    super(name);
    this.defaultValue = defaultValue;
    this.currentValue = 0f;
  }

  @Override
  public Float getValue() {
    return currentValue;
  }

  @Override
  public Object getDefault() {
    return defaultValue;
  }

  public void loadDefault() {
    loadFloat(defaultValue);
  }

  @Override
  public int getDimension() {
    return 4;
  }

  @Override
  public String getType() {
    return "float";
  }

  public void loadFloat(float value) {
    if (currentValue != value) {
      GL20.glUniform1f(super.getLocation(), value);
      currentValue = value;
    }
  }
}
