/*
 * Copyright (c) 2022, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering.interfaces.element;

import org.joml.Vector2f;
import org.joml.Vector4f;
import rendering.MouseInput;
import rendering.Texture;
import rendering.interfaces.UIElement;

public class Dragger extends UIElement<Dragger> implements Interactable {

  private boolean clicked = false;
  private Vector2f posInParentOnClick;

  public Dragger(Texture texture) {
    super(texture);
  }

  public Dragger(Vector4f color) {
    super(color);
  }

  @Override
  public void update(double elapsedTime) {}

  @Override
  public boolean input(MouseInput input) {
    for (UIElement<?> element : uiElements) {
      if (element instanceof Interactable) {
        if (((Interactable) element).input(input)) {
          return true;
        }
      }
    }

    Vector2f pos = input.getCurrentPos();
    Vector2f topLeft = getPositionInWindow();
    boolean inside =
        pos.x >= topLeft.x
            && pos.x <= topLeft.x + size.x
            && pos.y >= topLeft.y
            && pos.y <= topLeft.y + size.y;

    if (clicked) {
      if (!input.isLeftButtonPressed()) {
        input.release();
        clicked = false;
        return true;
      } else {
        Vector2f newPos = input.getCurrentPos().sub(posInParentOnClick);
        if (parent != null) {
          container.setPosition(newPos.x, newPos.y);
        } else {
          parent.setPosition(newPos.x, newPos.y);
        }
      }
    } else {
      if (input.canTakeControl(this)) {
        if (inside) {
          if (input.isLeftButtonPressed()) {
            input.halt(this);
            clicked = true;
            if (parent != null) {
              posInParentOnClick = input.getCurrentPos().sub(container.getPosition());
            } else {
              posInParentOnClick = input.getCurrentPos().sub(parent.getPosition());
            }
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
