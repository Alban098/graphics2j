/*
 * Copyright (c) 2022-2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering.renderers;

import rendering.ILogic;
import rendering.Window;

public interface RegisterableRenderer<T extends Renderable> extends Renderer {

  void render(Window window, ILogic logic);

  void register(T object);

  void unregister(T object);
}
