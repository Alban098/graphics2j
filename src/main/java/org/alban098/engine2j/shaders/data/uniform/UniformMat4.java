/*
 * Copyright (c) 2022-2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package org.alban098.engine2j.shaders.data.uniform;

import java.nio.FloatBuffer;
import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL20;

/** A concrete implementation of {@link Uniform} storing {@link Matrix4f} */
public final class UniformMat4 extends Uniform<Matrix4f> {

  /** A static buffer used to send data to the GPU */
  private static final FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(16);

  public UniformMat4(String name, Matrix4f defaultValue) {
    super(name, defaultValue);
    this.defaultValue = defaultValue;
    this.currentValue = new Matrix4f();
  }

  /**
   * Returns the dimension of the data stored in this Uniform
   *
   * @return the dimension of the data stored in this Uniform
   */
  @Override
  public int getDimension() {
    return 64;
  }

  /**
   * Gets the formatted type of the data stored in this Uniform
   *
   * @return "mat4"
   */
  @Override
  public String getType() {
    return "mat4";
  }

  /**
   * Loads a value into the Uniform
   *
   * @param matrix the value to load
   */
  public void load(Matrix4f matrix) {
    currentValue.set(matrix);
    matrixBuffer.put(matrix.get(new float[16]));
    matrixBuffer.flip();
    GL20.glUniformMatrix4fv(super.getLocation(), false, matrixBuffer);
    matrixBuffer.clear();
  }
}
