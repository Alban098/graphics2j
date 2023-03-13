/*
 * Copyright (c) 2022-2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package org.alban098.graphics2j.interfaces.components;

import java.util.function.Consumer;
import org.alban098.graphics2j.input.MouseState;

/**
 * Interface making an Object Hoverable, enable logic to happen when the mouse passes hover the
 * Object
 */
public interface Hoverable {

  /**
   * Returns whether the Object is hovered or not, only relevant if the concrete implementation
   * implements {@link Hoverable}
   *
   * @return whether the Object is hovered or not
   */
  boolean isHovered();

  /**
   * Sets the hovered state of this Object, only relevant if the concrete implementation implements
   * {@link Hoverable}
   *
   * @param hovered the new state of the Object
   */
  void setHovered(boolean hovered);

  /**
   * Sets the callback to call when the Object start being hovered, only relevant if the concrete
   * implementation implements {@link Hoverable}
   *
   * @param callback the callback to set
   */
  void onEnter(Consumer<MouseState> callback);

  /**
   * Sets the callback to call when the Object stop being hovered, only relevant if the concrete
   * implementation implements {@link Hoverable}
   *
   * @param callback the callback to set
   */
  void onExit(Consumer<MouseState> callback);

  /**
   * Sets the callback to call when the Object is being hovered, only relevant if the concrete
   * implementation implements {@link Hoverable}
   *
   * @param callback the callback to set
   */
  void onInside(Consumer<MouseState> callback);

  /**
   * Applies the onEnter callback if defined
   *
   * @param input the input to process
   */
  void onEnter(MouseState input);

  /**
   * Applies the onExit callback if defined
   *
   * @param input the input to process
   */
  void onExit(MouseState input);

  /**
   * Applies the onInside callback if defined
   *
   * @param input the input to process
   */
  void onInside(MouseState input);

  /**
   * The default hovering test routine, calls the right callbacks and update state when necessary
   *
   * @param input the current state of user inputs
   * @param inside is the cursor inside the element
   */
  default void hoverRoutine(MouseState input, boolean inside) {
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
