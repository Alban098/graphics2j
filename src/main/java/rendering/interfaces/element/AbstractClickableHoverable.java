/*
 * Copyright (c) 2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering.interfaces.element;

import java.util.function.Consumer;
import rendering.MouseInput;

public abstract class AbstractClickableHoverable extends UIElement implements Clickable, Hoverable {

  private boolean hovered = false;
  private boolean clicked = false;

  private Consumer<MouseInput> onEnter = (input) -> {};
  private Consumer<MouseInput> onInside = (input) -> {};
  private Consumer<MouseInput> onExit = (input) -> {};

  private Consumer<MouseInput> onClickStart = (input) -> {};
  private Consumer<MouseInput> onClickEnd = (input) -> {};
  private Consumer<MouseInput> onHold = (input) -> {};

  public boolean isClicked() {
    return clicked;
  }

  public void onClickEnd(Consumer<MouseInput> callback) {
    this.onClickEnd = callback;
  }

  public void onHold(Consumer<MouseInput> callback) {
    this.onHold = callback;
  }

  public void onClickStart(Consumer<MouseInput> callback) {
    this.onClickStart = callback;
  }

  public void setClicked(boolean clicked) {
    this.clicked = clicked;
  }

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

  @Override
  public boolean input(MouseInput input) {
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
    if (isClicked()) {
      // If the element is clicked and the left mouse button is released, this means the click has
      // ended, therefor executing the callback
      if (!input.isLeftButtonPressed()) {
        onClickEnd.accept(input);
        input.release();
        setClicked(false);
        // Prevent other UIElement further down the stack to interpret the input
        return true;
      } else {
        onHold.accept(input);
      }
    } else {
      if (input.canTakeControl(this)) {
        if (inside) {
          // If the element isn't clicked, but the mouse input is free and we are inside, juste take
          // control of the input to prevent camera panning/movement
          if (input.isLeftButtonPressed()) {
            input.halt(this);
            onClickStart.accept(input);
            setClicked(true);
            // Prevent other UIElement further down the stack to interpret the input
            return true;
          }
        } else {
          // Otherwise juste release the input to allow camera panning/movement
          input.release();
        }
      }
    }

    return false;
  }
}
