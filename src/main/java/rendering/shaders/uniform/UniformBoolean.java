/*
 * Copyright (c) 2022, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering.shaders.uniform;

import org.lwjgl.opengl.GL20;

public class UniformBoolean extends Uniform {

  private boolean currentBool;
  private final boolean defaultValue;

  public UniformBoolean(String name, boolean defaultValue) {
    super(name);
    this.defaultValue = defaultValue;
  }

  @Override
  public Object getDefault() {
    return defaultValue;
  }

  public void loadDefault() {
    loadBoolean(defaultValue);
  }

  public void loadBoolean(boolean bool) {
    if (currentBool != bool) {
      GL20.glUniform1i(super.getLocation(), bool ? 1 : 0);
      currentBool = bool;
    }
  }
}
