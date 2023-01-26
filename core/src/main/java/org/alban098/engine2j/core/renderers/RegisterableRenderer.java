/*
 * Copyright (c) 2022-2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package org.alban098.engine2j.core.renderers;

import org.alban098.engine2j.core.Scene;
import org.alban098.engine2j.core.Window;
import org.alban098.engine2j.core.objects.Camera;
import org.alban098.engine2j.core.objects.Renderable;

/**
 * An abstract representation of a Renderer that can register object for Rendering
 *
 * @param <T> the type of Objects to render
 */
public interface RegisterableRenderer<T extends Renderable> extends DebuggableRenderer {

  /**
   * Renders all registered element to the screen
   *
   * @param window the {@link Window} to render into
   * @param scene the {@link Scene} to render (only used for {@link Camera} placement as all Object
   *     have been previously registered)
   */
  void render(Window window, Scene scene);

  /**
   * Registers an Object to be rendered each frame until unregistered
   *
   * @param object the Object to register
   */
  void register(T object);

  /**
   * Unregisters an Object to no longer be rendered each frame
   *
   * @param object the Object to unregister
   */
  void unregister(T object);
}
