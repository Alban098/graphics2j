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

  void setHovered(boolean hovered);

  void onEnter(Consumer<MouseInput> callback);

  void onExit(Consumer<MouseInput> callback);

  void onInside(Consumer<MouseInput> callback);

  void onEnter(MouseInput input);

  void onExit(MouseInput input);

  void onInside(MouseInput input);

  default void hoverRoutine(MouseInput input, boolean inside) {
    if (isHovered() && !inside) {
      onExit(input);
      setHovered(false);
    } else if (isHovered() && inside) {
      onInside(input);
    } else if (!isHovered() && inside) {
      onEnter(input);
      setHovered(true);
    }
  }
}
