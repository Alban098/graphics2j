/*
 * Copyright (c) 2022-2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package org.alban098.engine2j.core.shaders.data.uniform;

import org.joml.Vector4f;
import org.lwjgl.opengl.GL20;

/** A concrete implementation of {@link Uniform} storing {@link Vector4f} */
public final class UniformVec4 extends Uniform<Vector4f> {

  /**
   * Create a new Uniform of type vec4
   *
   * @param name name of the uniform, must be the same as in the Shader program
   * @param defaultValue the default value of the uniform
   */
  public UniformVec4(String name, Vector4f defaultValue) {
    super(name, defaultValue);
    this.currentValue = new Vector4f();
  }

  /**
   * Returns the dimension of the data stored in this Uniform
   *
   * @return the dimension of the data stored in this Uniform
   */
  @Override
  public int getDimension() {
    return 16;
  }

  /**
   * Gets the formatted type of the data stored in this Uniform
   *
   * @return "vec4"
   */
  @Override
  public String getType() {
    return "vec4";
  }

  /**
   * Load a vector in GPU RAM
   *
   * @param vector vector to load
   */
  public void load(Vector4f vector) {
    load(vector.x, vector.y, vector.z, vector.w);
  }

  /**
   * Load a vector in GPU RAM
   *
   * @param x x component of the vector
   * @param y y component of the vector
   * @param z z component of the vector
   * @param w x component of the vector
   */
  public void load(float x, float y, float z, float w) {
    if (!currentValue.equals(x, y, z, w)) {
      currentValue.set(x, y, z, w);
      GL20.glUniform4f(super.getLocation(), x, y, z, w);
    }
  }
}
