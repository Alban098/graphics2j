/*
 * Copyright (c) 2022, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering.shaders.uniform;

import org.lwjgl.opengl.GL20;

public class UniformBoolean extends Uniform<Boolean> {

  private final boolean defaultValue;

  public UniformBoolean(String name, boolean defaultValue) {
    super(name);
    this.defaultValue = defaultValue;
    this.currentValue = false;
  }

  @Override
  public Boolean getValue() {
    return currentValue;
  }

  @Override
  public Object getDefault() {
    return defaultValue;
  }

  public void loadDefault() {
    loadBoolean(defaultValue);
  }

  @Override
  public int getDimension() {
    return 1;
  }

  @Override
  public String getType() {
    return "bool";
  }

  public void loadBoolean(boolean bool) {
    if (currentValue != bool) {
      GL20.glUniform1i(super.getLocation(), bool ? 1 : 0);
      currentValue = bool;
    }
  }
}
