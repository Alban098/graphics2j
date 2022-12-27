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
  TIME_MS("timeMs"),
  COLOR("color"),
  TEXTURED("textured"),
  CLICK_TINT("clickTint"),
  CLICKED("clicked"),
  HOVER_TINT("hoverTint"),
  HOVERED("hovered"),
  FOCUS_TINT("focusTint"),
  FOCUSED("focused");

  private final String name;

  Uniforms(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }
}
