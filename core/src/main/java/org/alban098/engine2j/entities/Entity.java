/*
 * Copyright (c) 2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package org.alban098.engine2j.entities;

import org.alban098.engine2j.common.Renderable;
import org.alban098.engine2j.common.components.RenderElement;
import org.alban098.engine2j.common.components.Transform;

public interface Entity extends Renderable {

  Entity setRenderable(RenderElement renderable);

  Entity setTransform(Transform transform);
}