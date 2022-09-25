/*
 * Copyright (c) 2022, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering.shaders.uniform;

public enum Uniforms {
  VIEW_MATRIX,
  PROJECTION_MATRIX;

  public String getName() {
    return toString();
  }
}
