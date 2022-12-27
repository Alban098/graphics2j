/*
 * Copyright (c) 2022, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering.shaders.uniform;

public enum Uniforms {
  VIEW_MATRIX("viewMatrix"),
  PROJECTION_MATRIX("projectionMatrix"),
  WIREFRAME("wireframe"),
  WIREFRAME_COLOR("wireframeColor"),
  TIME_MS("timeMs");

  private final String name;

  Uniforms(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }
}
