/*
 * Copyright (c) 2022-2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering.interfaces.element;

import org.joml.Vector2f;

public class Dragger extends UIElement implements Clickable {

  private Vector2f posInParentOnClick;

  private boolean clicked = false;

  public Dragger() {
    super();
    onClickStart(
        (input) ->
            posInParentOnClick =
                input.getCurrentPos().sub(getContainer().getProperties().getPosition()));
    onHold(
        (input) -> {
          Vector2f newPos = input.getCurrentPos().sub(posInParentOnClick);
          getContainer().getProperties().setPosition(newPos.x, newPos.y);
        });
  }

  @Override
  protected void onPropertyChange(
      Properties.Snapshot oldProperties, Properties.Snapshot newProperties) {}

  @Override
  public void update(double elapsedTime) {}

  @Override
  public boolean isClicked() {
    return clicked;
  }

  @Override
  public void setClicked(boolean clicked) {
    this.clicked = clicked;
  }
}
