/*
 * Copyright (c) 2022, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering.debug.component;

import rendering.entities.component.Component;

public class DefaultComponentDebugInterface implements ComponentDebugInterface<Component> {
  @Override
  public Class<Component> getComponentClass() {
    return Component.class;
  }

  @Override
  public String getDisplayName() {
    return "Default Component";
  }

  @Override
  public boolean draw(Component component) {
    return false;
  }
}
