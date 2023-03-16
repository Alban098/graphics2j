/*
 * Copyright (c) 2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package org.alban098.graphics2j.debug;

import imgui.ImGui;

public abstract class DebugInterface {

  /** A flag indicating if the Layer is visible or not */
  private boolean visible = true;

  private String title;

  public DebugInterface(String title) {
    this.title = title;
  }

  public final void renderInternal() {
    ImGui.begin(title);
    render();
    ImGui.end();
  }

  public abstract void render();

  /**
   * Return whether the layer is visible or not
   *
   * @return is the layer visible
   */
  public final boolean isVisible() {
    return visible;
  }

  /**
   * Set the layer as visible or non-visible
   *
   * @param visible should the layer be visible or not
   */
  public final void setVisible(boolean visible) {
    this.visible = visible;
  }

  public String getTitle() {
    return title;
  }
}
