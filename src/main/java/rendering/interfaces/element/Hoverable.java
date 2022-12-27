/*
 * Copyright (c) 2022, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering.interfaces.element;

import rendering.MouseInput;

public interface Hoverable extends Interactable {

  boolean isHovered();

  void setHovered(boolean hovered);

  default void executeHoverRoutine(MouseInput input, boolean inside) {
    setHovered(inside && input.canTakeControl(this));
  }
}
