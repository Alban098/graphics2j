/*
 * Copyright (c) 2022-2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering.debug.tab;

import rendering.renderers.Renderable;

/** An abstraction allowing a {@link DebugTab} to hold a reference to an {@link Renderable} */
public interface RenderableContainer {

  /**
   * Sets the currently selected {@link Renderable} held by the Tab
   *
   * @param renderable the new selected {@link Renderable}
   */
  void setSelectedRenderable(Renderable renderable);
}
