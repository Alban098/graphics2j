/*
 * Copyright (c) 2022, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering.interfaces.element;

import java.util.function.Function;
import org.joml.Vector2f;
import org.joml.Vector4f;
import rendering.ILogic;
import rendering.Texture;
import rendering.Window;
import rendering.interfaces.UIElement;

public class Button extends UIElement implements Interactable, Hoverable, Clickable {

  private final String text;
  private Function<ILogic, Boolean> callback;
  private boolean hovered = true;
  private boolean clicked = false;

  public Button(Window window, Vector2f position, Vector2f size, String text, Texture texture) {
    super(window, texture, null);
    this.text = text;
    this.position.set(position);
    this.size.set(size);
  }

  public Button(Window window, Vector2f position, Vector2f size, String text, Vector4f color) {
    super(window, color, null);
    this.text = text;
    this.position.set(position);
    this.size.set(size);
  }

  @Override
  public boolean interact(ILogic logic) {
    return callback.apply(logic);
  }

  public String getText() {
    return text;
  }

  public void onClick(Function<ILogic, Boolean> func) {
    this.callback = func;
  }

  @Override
  public boolean isHovered() {
    return hovered;
  }

  @Override
  public boolean isClicked() {
    return clicked;
  }

  @Override
  public void update(double elapsedTime, UIElement parent) {}
}
