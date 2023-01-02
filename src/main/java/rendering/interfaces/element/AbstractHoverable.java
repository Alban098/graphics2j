/*
 * Copyright (c) 2022-2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering.interfaces.element;

import java.util.function.Consumer;
import rendering.MouseInput;

public abstract class AbstractHoverable extends UIElement implements Hoverable {

  private boolean hovered = false;

  private Consumer<MouseInput> onEnter = (input) -> {};
  private Consumer<MouseInput> onInside = (input) -> {};
  private Consumer<MouseInput> onExit = (input) -> {};

  public boolean isHovered() {
    return hovered;
  }

  public void onEnter(Consumer<MouseInput> callback) {
    this.onEnter = callback;
  }

  public void onInside(Consumer<MouseInput> callback) {
    this.onInside = callback;
  }

  public void onExit(Consumer<MouseInput> callback) {
    this.onExit = callback;
  }

  public void setHovered(boolean hovered) {
    this.hovered = hovered;
  }

  public final boolean input(MouseInput input) {
    boolean inside = isInside(input.getCurrentPos());
    if (isHovered()) {
      if (!inside) {
        if (input.hasControl(this)) {
          onExit.accept(input);
          input.release();
          setHovered(false);
        }
      } else {
        if (input.hasControl(this)) {
          onInside.accept(input);
        }
      }
    } else {
      if (inside) {
        if (input.canTakeControl(this)) {
          input.halt(this);
          setHovered(true);
          onEnter.accept(input);
        }
      } else {
        if (input.hasControl(this)) {
          input.release();
        }
      }
    }
    return false;
  }
}
