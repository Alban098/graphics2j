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
import rendering.Window;
import rendering.interfaces.UIElement;

public class DragBar extends UIElement<DragBar> implements Interactable {

  private boolean clicked = false;
  private Vector2f posInParentOnClick;

  public DragBar(Window window, Texture texture, UIElement<?> parent) {
    super(window, texture, parent);
  }

  public DragBar(Window window, Vector4f color, UIElement<?> parent) {
    super(window, color, parent);
  }

  @Override
  public void update(double elapsedTime, UIElement<?> parent) {}

  @Override
  public boolean input(MouseInput input) {
    Vector2f pos = input.getCurrentPos();
    Vector2f topLeft = getAbsolutePosition();
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
        parent.setPosition(newPos.x, newPos.y);
      }
    } else {
      if (input.canTakeControl(this)) {
        if (inside) {
          if (input.isLeftButtonPressed()) {
            input.halt(this);
            clicked = true;
            posInParentOnClick = input.getCurrentPos().sub(parent.getPosition());
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
