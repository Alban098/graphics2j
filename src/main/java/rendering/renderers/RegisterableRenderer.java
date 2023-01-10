/*
 * Copyright (c) 2022-2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering.renderers;

import rendering.Window;
import rendering.scene.Scene;

public interface RegisterableRenderer<T extends Renderable> extends Renderer {

  void render(Window window, Scene scene);

  void register(T object);

  void unregister(T object);
}
