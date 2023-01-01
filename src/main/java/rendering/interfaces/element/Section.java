/*
 * Copyright (c) 2022-2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering.interfaces.element;

import rendering.MouseInput;
import rendering.interfaces.UIElement;

public class Section extends UIElement {

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
