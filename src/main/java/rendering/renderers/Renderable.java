/*
 * Copyright (c) 2022-2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering.renderers;

import rendering.entities.component.RenderableComponent;
import rendering.entities.component.TransformComponent;

public interface Renderable {

  RenderableComponent getRenderable();

  TransformComponent getTransform();
}
