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

public class UniformMat2 extends Uniform<Matrix2f> {

  private static final FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(4);
  private final Matrix2f defaultValue;

  public UniformMat2(String name, Matrix2f defaultValue) {
    super(name);
    this.defaultValue = defaultValue;
    this.currentValue = new Matrix2f();
  }

  @Override
  public Object getDefault() {
    return defaultValue;
  }

  public void loadDefault() {
    load(defaultValue);
  }

  @Override
  public int getDimension() {
    return 16;
  }

  @Override
  public String getType() {
    return "mat2";
  }

  public void load(Matrix2f matrix) {
    currentValue.set(matrix);
    matrixBuffer.put(matrix.get(new float[4]));
    matrixBuffer.flip();
    GL20.glUniformMatrix3fv(super.getLocation(), false, matrixBuffer);
    matrixBuffer.clear();
  }
}
