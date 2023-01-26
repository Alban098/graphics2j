/*
 * Copyright (c) 2022-2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package org.alban098.engine2j.core.debug;

import org.alban098.engine2j.core.Engine;
import org.alban098.engine2j.core.Scene;

/** This class represent the common behaviour of all ImGui Layer */
public abstract class ImGuiLayer {

  /** A reference to the {@link Engine} object */
  protected final Engine engine;
  /** A reference to the {@link Scene} object */
  protected final Scene scene;
  /** A flag indicating if the Layer is visible or not */
  private boolean visible;

  /** Create a new Layer setting it as non-visible */
  public ImGuiLayer(Engine engine) {
    this.engine = engine;
    this.scene = this.engine.getLogic().getScene();
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
