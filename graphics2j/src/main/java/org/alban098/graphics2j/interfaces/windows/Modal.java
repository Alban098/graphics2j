/*
 * Copyright (c) 2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package org.alban098.graphics2j.interfaces.windows;

import org.alban098.graphics2j.common.Window;
import org.alban098.graphics2j.interfaces.InterfaceRenderingManager;
import org.alban098.graphics2j.interfaces.components.UIElement;
import org.alban098.graphics2j.interfaces.components.property.Properties;

/**
 * Represents a Modal UserInterface, it defers from a standard {@link UserInterface} in the fact
 * that it's content is rendered once, and then buffered. Its content is not supposed to be altered
 * by UserInputs as it could be displayed on hoover of a {@link UIElement}. It's re-rendered when
 * its size changed
 */
public abstract class Modal extends UserInterface {

  /** Is the Modal's content already rendered */
  private boolean rendered;

  /**
   * Creates a new Modal contained in a {@link Window}, with a name and managed by an {@link
   * InterfaceRenderingManager}
   *
   * @param window the {@link Window} containing this UserInterface
   * @param name the name of this UserInterface
   */
  public Modal(Window window, String name) {
    super(window, name);
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
   * <p>/!\ call this {@link Modal#onPropertyChange(Properties, Object)} base method when overriding
   *
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
