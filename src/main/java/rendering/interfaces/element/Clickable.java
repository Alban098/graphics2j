/*
 * Copyright (c) 2022, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering.interfaces.element;

import rendering.MouseInput;

public interface Clickable extends Interactable {

  boolean isClicked();

  void onClick(Runnable callback);

  void setClicked(boolean clicked);

  default boolean executeClickRoutine(MouseInput input, boolean inside, Runnable callback) {
    if (isClicked()) {
      // If the element is clicked and the left mouse button is released, this means the click has
      // ended, therefor executing the callback
      if (!input.isLeftButtonPressed()) {
        callback.run();
        input.release();
        setClicked(false);
        // Prevent other UIElement further down the stack to interpret the input
        return true;
      }
    } else {
      if (input.canTakeControl(this)) {
        if (inside) {
          // If the element isn't clicked, but the mouse input is free and we are inside, juste take
          // control of the input to prevent camera panning/movement
          input.halt(this);
          if (input.isLeftButtonPressed()) {
            // If we are clicked here, juste
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
