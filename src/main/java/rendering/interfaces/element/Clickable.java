/*
 * Copyright (c) 2022, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering.interfaces.element;

import rendering.MouseInput;

public interface Clickable extends Interactable {

  boolean isClicked();

  void setClicked(boolean clicked);

  Runnable getCallback();

  default boolean executeClickRoutine(MouseInput input, boolean inside) {
    if (isClicked()) {
      if (!input.isLeftButtonPressed()) {
        getCallback().run();
        input.release();
        setClicked(false);
        return true;
      }
    } else {
      if (input.canTakeControl(this)) {
        if (inside) {
          input.halt(this);
          if (input.isLeftButtonPressed()) {
            setClicked(true);
            return true;
          }
        } else {
          input.release();
        }
      }
    }
    return false;
  }
}
