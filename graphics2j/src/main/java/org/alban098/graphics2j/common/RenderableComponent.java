/*
 * Copyright (c) 2022-2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package org.alban098.graphics2j.common;

import org.alban098.common.Component;
import org.alban098.graphics2j.common.components.RenderElement;

/** An abstract representation of an Object that can be rendered */
public class RenderableComponent extends Component {

  private final RenderElement renderable;
  private final String name;

  public RenderableComponent(RenderElement renderable, String name) {
    this.renderable = renderable;
    this.name = name;
  }

  /**
   * Returns the {@link RenderElement} enabling the object to be renderer, should not be null
   *
   * @return the {@link RenderElement} enabling the object to be renderer, should not be null
   */
  public RenderElement getRenderable() {
    return renderable;
  }

  /**
   * Returns a display name for the Renderable
   *
   * @return a display name for the Renderable
   */
  public String getName() {
    return name;
  }
}
