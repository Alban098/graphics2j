/*
 * Copyright (c) 2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering.interfaces;

import rendering.Window;
import rendering.interfaces.element.property.Properties;

/**
 * Represents a Modal UserInterface, it defers from a standard {@link UserInterface} in the fact
 * that it's content is rendered once, and then buffered. Its content is not supposed to be altered
 * by UserInputs as it could be displayed on hoover of a {@link
 * rendering.interfaces.element.UIElement}. It's re-rendered when its size changed
 */
public abstract class Modal extends UserInterface {

  /** Is the Modal's content already rendered */
  private boolean rendered;

  /**
   * Creates a new Modal contained in a {@link Window}, with a name and managed by an {@link
   * InterfaceManager}
   *
   * @param window the {@link Window} containing this UserInterface
   * @param name the name of this UserInterface
   * @param manager the {@link InterfaceManager} managing this UserInterface
   */
  public Modal(Window window, String name, InterfaceManager manager) {
    super(window, name, manager);
  }

  /**
   * Returns whether the Modal's content is already rendered or not
   *
   * @return is the Modal's content is already rendered or not
   */
  public final boolean isRendered() {
    return rendered;
  }

  /**
   * Change the flags indicating if the Modal's content is already rendered or not
   *
   * @param rendered new value
   */
  public final void setRendered(boolean rendered) {
    this.rendered = rendered;
  }

  /**
   * Flags the Modal's content to be re-rendered only if the changed {@link Properties} is {@link
   * Properties#SIZE}
   *
   * @implSpec call this {@link Modal#onPropertyChange(Properties, Object)} base method when
   *     overriding
   * @param property the changed {@link Properties}
   * @param object the new value
   */
  @Override
  protected void onPropertyChange(Properties property, Object object) {
    if (property == Properties.SIZE) {
      rendered = false;
    }
  }
}
