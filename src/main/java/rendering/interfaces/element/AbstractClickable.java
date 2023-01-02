/*
 * Copyright (c) 2022-2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering.interfaces.element;

import java.util.function.Consumer;
import rendering.MouseInput;

public abstract class AbstractClickable extends UIElement implements Clickable {

  private Consumer<MouseInput> onClickStart = (input) -> {};
  private Consumer<MouseInput> onClickEnd = (input) -> {};
  private Consumer<MouseInput> onHold = (input) -> {};

  private boolean clicked = false;

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

  public final boolean input(MouseInput input) {
    boolean inside = isInside(input.getCurrentPos());
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
