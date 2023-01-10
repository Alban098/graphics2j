/*
 * Copyright (c) 2022-2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering.debug.component;

import rendering.scene.entities.component.Component;

public final class DefaultComponentDebugInterface implements ComponentDebugInterface<Component> {
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
