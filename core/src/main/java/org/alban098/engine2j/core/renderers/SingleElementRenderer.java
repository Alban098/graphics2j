/*
 * Copyright (c) 2022-2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package org.alban098.engine2j.core.renderers;

import org.alban098.engine2j.core.objects.Renderable;

/**
 * An abstract representation of a Renderer that can render element one at a time
 *
 * @param <T> the type of Objects to render
 */
public interface SingleElementRenderer<T extends Renderable> extends DebuggableRenderer {

  /**
   * Renders all registered element to the screen
   *
   * @param object the Object to render
   */
  void render(T object);
}