/*
 * Copyright (c) 2022-2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package org.alban098.engine2j.example.debug;

import org.alban098.engine2j.example.entities.components.RotationProviderComponent;
import org.alban098.engine2j.core.debug.DebugUtils;
import org.alban098.engine2j.core.debug.component.ComponentDebugInterface;
import org.alban098.engine2j.core.objects.entities.component.Component;

public class RotationProviderComponentDebugInterface
    implements ComponentDebugInterface<RotationProviderComponent> {

  @Override
  public Class<RotationProviderComponent> getComponentClass() {
    return RotationProviderComponent.class;
  }

  @Override
  public String getDisplayName() {
    return "Rotation Provider";
  }

  @Override
  public boolean draw(Component component) {
    if (component instanceof RotationProviderComponent rotationProviderComponent) {
      DebugUtils.drawAttrib("Rotation (rad/s)", rotationProviderComponent.getValue(), 30, 150);
      return true;
    }
    return false;
  }
}
