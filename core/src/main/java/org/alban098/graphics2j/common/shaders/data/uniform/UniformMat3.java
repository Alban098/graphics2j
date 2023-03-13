/*
 * Copyright (c) 2022-2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package org.alban098.graphics2j.common.shaders.data.uniform;

import java.nio.FloatBuffer;
import org.joml.Matrix3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL20;

/** A concrete implementation of {@link Uniform} storing {@link Matrix3f} */
public final class UniformMat3 extends Uniform<Matrix3f> {

  /** A static buffer used to send data to the GPU */
  private static final FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(9);

  /**
   * Create a new Uniform of type int
   *
   * @param name name of the uniform, must be the same as in the Shader program
   * @param defaultValue the default value of the uniform
   */
  public UniformMat3(String name, Matrix3f defaultValue) {
    super(name, defaultValue);
    this.currentValue = new Matrix3f();
  }

  /**
   * Returns the dimension of the data stored in this Uniform
   *
   * @return the dimension of the data stored in this Uniform
   */
  @Override
  public int getDimension() {
    return 36;
  }

  /**
   * Gets the formatted type of the data stored in this Uniform
   *
   * @return "mat3"
   */
  @Override
  public String getType() {
    return "mat3";
  }

  /**
   * Loads a value into the Uniform
   *
   * @param matrix the value to load
   */
  public void load(Matrix3f matrix) {
    currentValue.set(matrix);
    matrixBuffer.put(matrix.get(new float[9]));
    matrixBuffer.flip();
    GL20.glUniformMatrix3fv(super.getLocation(), false, matrixBuffer);
    matrixBuffer.clear();
  }
}
