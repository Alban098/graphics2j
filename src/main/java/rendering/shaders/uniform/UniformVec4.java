/*
 * Copyright (c) 2022, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering.shaders.uniform;

import org.joml.Vector4f;
import org.lwjgl.opengl.GL20;

public class UniformVec4 extends Uniform<Vector4f> {

  private final Vector4f defaultValue;

  /**
   * Create a new Uniform of type vec4
   *
   * @param name name of the uniform, must be the same as in the Shader program
   */
  public UniformVec4(String name, Vector4f defaultValue) {
    super(name);
    this.defaultValue = defaultValue;
    this.currentValue = new Vector4f();
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
