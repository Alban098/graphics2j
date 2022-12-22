/*
 * Copyright (c) 2022, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering.shaders.uniform;

import org.joml.Vector2f;
import org.lwjgl.opengl.GL20;

public class UniformVec2 extends Uniform<Vector2f> {

  private final Vector2f defaultValue;

  /**
   * Create a new Uniform of type vec2
   *
   * @param name name of the uniform, must be the same as in the Shader program
   */
  public UniformVec2(String name, Vector2f defaultValue) {
    super(name);
    this.defaultValue = defaultValue;
    this.currentValue = new Vector2f();
  }

  @Override
  public Object getDefault() {
    return defaultValue;
  }

  public void loadDefault() {
    loadVec2(defaultValue);
  }

  /**
   * Load a vector in GPU RAM
   *
   * @param vector vector to load
   */
  public void loadVec2(Vector2f vector) {
    loadVec2(vector.x, vector.y);
  }

  /**
   * Load a vector in GPU RAM
   *
   * @param x x component of the vector
   * @param y y component of the vector
   */
  public void loadVec2(float x, float y) {
    if (!currentValue.equals(x, y)) {
      currentValue.set(x, y);
      GL20.glUniform2f(super.getLocation(), x, y);
    }
  }
}
