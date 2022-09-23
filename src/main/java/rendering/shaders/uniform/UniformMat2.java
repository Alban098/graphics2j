/*
 * Copyright (c) 2022, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering.shaders.uniform;

import java.nio.FloatBuffer;
import org.joml.Matrix2f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL20;

public class UniformMat2 extends Uniform {

  private static final FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(4);
  private final Matrix2f defaultValue;

  public UniformMat2(String name, Matrix2f defaultValue) {
    super(name);
    this.defaultValue = defaultValue;
  }

  @Override
  public Object getDefault() {
    return defaultValue;
  }

  public void loadDefault() {
    loadMatrix(defaultValue);
  }

  public void loadMatrix(Matrix2f matrix) {
    matrix.get(matrixBuffer);
    matrixBuffer.flip();
    GL20.glUniformMatrix3fv(super.getLocation(), false, matrixBuffer);
  }
}
