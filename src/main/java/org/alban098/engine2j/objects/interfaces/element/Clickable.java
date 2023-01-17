/*
 * Copyright (c) 2022-2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package org.alban098.engine2j.objects.interfaces.element;

import java.util.function.Consumer;
import org.alban098.engine2j.core.MouseInputManager;

/** Interface making an Object Clickable, enable logic to happen when clicking on the Object */
public interface Clickable {

  /**
   * Returns whether the Object is clicked or not, only relevant if the concrete implementation
   * implements {@link Clickable}
   *
   * @return whether the Object is clicked or not
   */
  boolean isClicked();

  /**
   * Sets the clicked state of this Object, only relevant if the concrete implementation implements
   * {@link Clickable}
   *
   * @param clicked the new state of the Object
   */
  void setClicked(boolean clicked);

  /**
   * Sets the callback to call when the Object stop being clicked, only relevant if the concrete
   * implementation implements {@link Clickable}
   *
   * @param callback the callback to set
   */
  void onClickEnd(Consumer<MouseInputManager> callback);

  /**
   * Sets the callback to call when the Object start being clicked, only relevant if the concrete
   * implementation implements {@link Clickable}
   *
   * @param callback the callback to set
   */
  void onClickStart(Consumer<MouseInputManager> callback);

  /**
   * Sets the callback to call when the Object is being clicked, only relevant if the concrete
   * implementation implements {@link Clickable}
   *
   * @param callback the callback to set
   */
  void onHold(Consumer<MouseInputManager> callback);

  /**
   * Applies the onClickEnd callback if defined
   *
   * @param input the input to process
   */
  void onClickEnd(MouseInputManager input);

  /**
   * Applies the onClickStart callback if defined
   *
   * @param input the input to process
   */
  void onClickStart(MouseInputManager input);

  /**
   * Applies the onHold callback if defined
   *
   * @param input the input to process
   */
  void onHold(MouseInputManager input);

  /**
   * The default clicking test routine, calls the right callbacks and update state when necessary
   *
   * @param input the current state of user inputs
   * @param inside is the cursor inside the element
   */
  default void clickRoutine(MouseInputManager input, boolean inside) {
    if (isClicked() && !input.isLeftButtonPressed()) {
      onClickEnd(input);
      setClicked(false);
    }
    if (isClicked() && input.isLeftButtonPressed()) {
      onHold(input);
    }
    if (!isClicked() && inside && input.isLeftButtonPressed()) {
      onClickStart(input);
      setClicked(true);
    }
  }
}
