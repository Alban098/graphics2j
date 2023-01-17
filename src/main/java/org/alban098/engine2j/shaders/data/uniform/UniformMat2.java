/*
 * Copyright (c) 2022-2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package org.alban098.engine2j.shaders.data.uniform;

import java.nio.FloatBuffer;
import org.joml.Matrix2f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL20;

/** A concrete implementation of {@link Uniform} storing {@link Matrix2f} */
public final class UniformMat2 extends Uniform<Matrix2f> {

  /** A static buffer used to send data to the GPU */
  private static final FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(4);

  public UniformMat2(String name, Matrix2f defaultValue) {
    super(name, defaultValue);
    this.currentValue = new Matrix2f();
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
   * @return "mat2"
   */
  @Override
  public String getType() {
    return "mat2";
  }

  /**
   * Loads a value into the Uniform
   *
   * @param matrix the value to load
   */
  public void load(Matrix2f matrix) {
    currentValue.set(matrix);
    matrixBuffer.put(matrix.get(new float[4]));
    matrixBuffer.flip();
    GL20.glUniformMatrix3fv(super.getLocation(), false, matrixBuffer);
    matrixBuffer.clear();
  }
}
