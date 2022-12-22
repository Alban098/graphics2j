/*
 * Copyright (c) 2022, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering.shaders.uniform;

import java.nio.FloatBuffer;
import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL20;

public class UniformMat4 extends Uniform<Matrix4f> {

  private static final FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(16);
  private final Matrix4f defaultValue;

  public UniformMat4(String name, Matrix4f defaultValue) {
    super(name);
    this.defaultValue = defaultValue;
    this.currentValue = new Matrix4f();
  }

  @Override
  public Object getDefault() {
    return defaultValue;
  }

  public void loadDefault() {
    loadMatrix(defaultValue);
  }

  public void loadMatrix(Matrix4f matrix) {
    currentValue.set(matrix);
    matrixBuffer.put(matrix.get(new float[16]));
    matrixBuffer.flip();
    GL20.glUniformMatrix4fv(super.getLocation(), false, matrixBuffer);
    matrixBuffer.clear();
  }
}
