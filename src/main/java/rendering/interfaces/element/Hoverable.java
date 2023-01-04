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

  Consumer<MouseInput> onEnter();

  Consumer<MouseInput> onExit();

  Consumer<MouseInput> onInside();

  default void hoverRoutine(MouseInput input, boolean inside) {
    if (isHovered() && !inside) {
      onExit().accept(input);
      setHovered(false);
    } else if (isHovered() && inside) {
      onInside().accept(input);
    } else if (!isHovered() && inside) {
      onEnter().accept(input);
      setHovered(true);
    }
  }
}
