/*
 * Copyright (c) 2022, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering.debug;

import rendering.Engine;

/** This class represent the common behaviour of all ImGui Layer */
public abstract class ImGuiLayer {

  protected final Engine engine;
  private boolean visible;

  /** Create a new Layer setting it as non-visible */
  public ImGuiLayer(Engine engine) {
    this.engine = engine;
    visible = false;
  }

  /** Placeholder render method called to render the layer to the screen */
  public abstract void render();

  /**
   * Return whether the layer is visible or not
   *
   * @return is the layer visible
   */
  public boolean isVisible() {
    return visible;
  }

  /**
   * Set the layer as visible or non-visible
   *
   * @param visible should the layer be visible or not
   */
  public void setVisible(boolean visible) {
    this.visible = visible;
  }
}
