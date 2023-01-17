/*
 * Copyright (c) 2022-2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package org.alban098.engine2j.shaders.data.uniform;

import org.joml.Vector2f;
import org.lwjgl.opengl.GL20;

/** A concrete implementation of {@link Uniform} storing {@link Vector2f} */
public final class UniformVec2 extends Uniform<Vector2f> {

  /**
   * Create a new Uniform of type vec2
   *
   * @param name name of the uniform, must be the same as in the Shader program
   */
  public UniformVec2(String name, Vector2f defaultValue) {
    super(name, defaultValue);
    this.currentValue = new Vector2f();
  }

  /**
   * Returns the dimension of the data stored in this Uniform
   *
   * @return the dimension of the data stored in this Uniform
   */
  @Override
  public int getDimension() {
    return 8;
  }

  /**
   * Gets the formatted type of the data stored in this Uniform
   *
   * @return "vec2"
   */
  @Override
  public String getType() {
    return "vec2";
  }

  /**
   * Load a vector in GPU RAM
   *
   * @param vector vector to load
   */
  public void load(Vector2f vector) {
    load(vector.x, vector.y);
  }

  /**
   * Load a vector in GPU RAM
   *
   * @param x x component of the vector
   * @param y y component of the vector
   */
  public void load(float x, float y) {
    if (!currentValue.equals(x, y)) {
      currentValue.set(x, y);
      GL20.glUniform2f(super.getLocation(), x, y);
    }
  }
}
