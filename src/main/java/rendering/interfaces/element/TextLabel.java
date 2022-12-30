/*
 * Copyright (c) 2022, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering.interfaces.element;

import rendering.MouseInput;
import rendering.interfaces.UIElement;

public class TextLabel extends UIElement {

  private String text;

  public TextLabel(String text) {
    this.text = text;
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  @Override
  protected void onPropertyChange(
      Properties.Snapshot oldProperties, Properties.Snapshot newProperties) {}

  @Override
  public void update(double elapsedTime) {}

  @Override
  public boolean input(MouseInput input) {
    return false;
  }
}
