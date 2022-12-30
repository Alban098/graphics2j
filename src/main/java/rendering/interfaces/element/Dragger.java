/*
 * Copyright (c) 2022, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering.interfaces.element;

import org.joml.Vector2f;
import rendering.MouseInput;
import rendering.interfaces.UIElement;

public class Dragger extends UIElement {

  private boolean clicked = false;
  private Vector2f posInParentOnClick;

  public Dragger() {
    super();
  }

  @Override
  protected void onPropertyChange(
      Properties.Snapshot oldProperties, Properties.Snapshot newProperties) {}

  @Override
  public void update(double elapsedTime) {}

  public boolean input(MouseInput input) {
    boolean inside = isInside(input.getCurrentPos());

    if (clicked) {
      if (!input.isLeftButtonPressed()) {
        input.release();
        clicked = false;
        return true;
      } else {
        Vector2f newPos = input.getCurrentPos().sub(posInParentOnClick);
        getContainer().getProperties().setPosition(newPos.x, newPos.y);
      }
    } else {
      if (input.canTakeControl(this)) {
        if (inside) {
          if (input.isLeftButtonPressed()) {
            input.halt(this);
            clicked = true;
            posInParentOnClick =
                input.getCurrentPos().sub(getContainer().getProperties().getPosition());
          }
          return true;
        } else {
          input.release();
        }
      }
    }
    return false;
  }
}
