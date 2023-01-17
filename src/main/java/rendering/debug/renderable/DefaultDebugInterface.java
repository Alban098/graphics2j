/*
 * Copyright (c) 2022-2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering.debug.renderable;

import rendering.debug.Debugger;
import rendering.renderers.Renderable;
import rendering.scene.entities.Entity;

/** A concrete implementation of {@link RenderableDebugInterface} for {@link Renderable} */
public class DefaultDebugInterface extends RenderableDebugInterface<Renderable> {

  /**
   * Returns the type of {@link Renderable} to display
   *
   * @return Renderable.class
   */
  @Override
  public Class<Renderable> getRenderableType() {
    return Renderable.class;
  }

  /**
   * Renders all additional tabs of the Interface (Hierarchy Tab)
   *
   * @param caller the main Debugger
   * @param entity the {@link Entity} to display
   */
  @Override
  protected void renderTabs(Debugger caller, Renderable entity) {}

  /**
   * Should the component tab be displayed
   *
   * @return true if not override
   */
  @Override
  protected boolean showComponentTab() {
    return true;
  }

  /**
   * Should the rendering tab be displayed
   *
   * @return true
   */
  @Override
  protected boolean showRenderingTab() {
    return true;
  }
}
