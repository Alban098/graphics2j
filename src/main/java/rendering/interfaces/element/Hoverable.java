/*
 * Copyright (c) 2022-2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering.interfaces.element;

import java.util.function.Consumer;
import rendering.MouseInput;

public interface Hoverable {

  boolean isHovered();

  void onEnter(Consumer<MouseInput> callback);

  void onInside(Consumer<MouseInput> callback);

  void onExit(Consumer<MouseInput> callback);

  void setHovered(boolean hovered);

  boolean input(MouseInput input);
}
