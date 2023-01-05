/*
 * Copyright (c) 2022-2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering.interfaces.element;

import org.joml.Vector2f;
import rendering.interfaces.element.property.Properties;

public class Dragger extends UIElement implements Clickable {

  private Vector2f posInParentOnClick;

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

  @Override
  protected void onPropertyChange(Properties property, Object value) {}

  @Override
  public void update(double elapsedTime) {}
}
