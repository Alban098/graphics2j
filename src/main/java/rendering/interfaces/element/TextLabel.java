/*
 * Copyright (c) 2022, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering.interfaces.element;

import java.util.Collection;
import java.util.Collections;
import org.joml.Vector4f;
import rendering.MouseInput;
import rendering.data.FrameBufferObject;
import rendering.interfaces.UIElement;

public class TextLabel extends UIElement<TextLabel> {

  private String text;

  public TextLabel(Vector4f color, String text) {
    super(color);
    this.text = text;
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  @Override
  public void update(double elapsedTime) {}

  @Override
  public boolean input(MouseInput input) {
    return false;
  }

  @Override
  public boolean isTextured() {
    return false;
  }

  @Override
  public Collection<UIElement<?>> getElements() {
    return Collections.emptyList();
  }

  @Override
  public UIElement<?> getElement(String identifier) {
    return null;
  }

  @Override
  public void addElement(String identifier, UIElement<?> element) {}

  @Override
  public FrameBufferObject getFbo() {
    return null;
  }
}
