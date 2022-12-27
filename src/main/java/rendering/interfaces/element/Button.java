/*
 * Copyright (c) 2022, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering.interfaces.element;

import java.awt.*;
import org.joml.Vector2f;
import org.joml.Vector4f;
import rendering.MouseInput;
import rendering.Texture;
import rendering.Window;
import rendering.interfaces.UIElement;

public class Button extends UIElement<Button> implements Hoverable, Clickable {

  private final String text;
  private Runnable callback;
  private boolean hovered = true;
  private boolean clicked = false;

  public Button(Window window, Texture texture, String text) {
    super(window, texture, null);
    this.text = text;
  }

  public Button(Window window, Vector4f color, String text) {
    super(window, color, null);
    this.text = text;
  }

  public String getText() {
    return text;
  }

  public void onClick(Runnable func) {
    this.callback = func;
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
  public Runnable getCallback() {
    return callback;
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
    executeHoverRoutine(input, inside);
    return executeClickRoutine(input, inside);
  }
}
