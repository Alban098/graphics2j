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

public class Button extends UIElement<Button> implements Hoverable, Clickable {

  private final String text;
  private Runnable callback;
  private boolean hovered = false;
  private boolean clicked = false;

  public Button(Texture texture, String text) {
    super(texture);
    this.text = text;
  }

  public Button(Vector4f color, String text) {
    super(color);
    this.text = text;
  }

  public String getText() {
    return text;
  }

  public void onClick(Runnable callback) {
    this.callback = callback;
  }

  @Override
  public boolean isHovered() {
    return hovered;
  }

  @Override
  public void setHovered(boolean hovered) {
    this.hovered = hovered;
  }

  @Override
  public boolean isClicked() {
    return clicked;
  }

  @Override
  public void setClicked(boolean clicked) {
    this.clicked = clicked;
  }

  @Override
  public void update(double elapsedTime) {}

  @Override
  public boolean input(MouseInput input) {
    Vector2f pos = input.getCurrentPos();
    Vector2f topLeft = getPositionInWindow();
    boolean inside =
        pos.x >= topLeft.x
            && pos.x <= topLeft.x + size.x
            && pos.y >= topLeft.y
            && pos.y <= topLeft.y + size.y;
    executeHoverRoutine(input, inside);
    return executeClickRoutine(input, inside, callback);
  }
}
