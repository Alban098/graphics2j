/*
 * Copyright (c) 2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering.interfaces;

import rendering.Window;
import rendering.interfaces.element.property.Properties;

public class Modal extends UserInterface {

  private boolean rendered;

  public Modal(Window window, String name, InterfaceManager manager) {
    super(window, name, manager);
  }

  @Override
  public void update(double elapsedTime) {}

  public boolean isRendered() {
    return rendered;
  }

  @Override
  protected void onPropertyChange(Properties property, Object object) {
    if (property == Properties.SIZE) {
      rendered = false;
    }
  }

  public void setRendered(boolean rendered) {
    this.rendered = rendered;
  }
}
