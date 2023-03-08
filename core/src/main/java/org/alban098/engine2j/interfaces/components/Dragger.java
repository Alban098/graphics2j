/*
 * Copyright (c) 2022-2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package org.alban098.engine2j.interfaces.components;

import org.alban098.engine2j.interfaces.components.property.Properties;
import org.alban098.engine2j.interfaces.windows.UserInterface;
import org.joml.Vector2f;

/**
 * An implementation of {@link UIElement} allowing the user to drag the parent {@link UserInterface}
 */
public final class Dragger extends UIElement implements Clickable {

  /** The position the cursor was in when started to drag, relative to the container, in pixel */
  private Vector2f posInParentOnClick;

  /** Creates a new Dragger and sets the correct {@link Clickable} callback */
  public Dragger() {
    super();
    onClickStart(
        (input) ->
            posInParentOnClick =
                input
                    .getCurrentPos()
                    .sub(getContainer().getProperties().get(Properties.POSITION, Vector2f.class)));
    onHold(
        (input) -> {
          Vector2f newPos = input.getCurrentPos().sub(posInParentOnClick);
          getContainer().getProperties().set(Properties.POSITION, newPos);
        });
  }

  /**
   * Updates the Dragger, this method is called once every update, noting to do in this
   * implementation
   *
   * @param elapsedTime the elapsed time since last update in seconds
   */
  @Override
  public void update(double elapsedTime) {}

  /**
   * Called every time a {@link Properties} of the Dragger is changed, noting to do in this
   * implementation
   *
   * @param property the changed {@link Properties}
   * @param value the new value
   */
  @Override
  protected void onPropertyChange(Properties property, Object value) {}
}
