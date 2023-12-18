/*
 * Copyright (c) 2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package org.alban098.graphics2j.common;

import org.alban098.common.Transform;

public interface Renderable {

  RenderableComponent getRenderableComponent();

  Transform getTransform();
}
