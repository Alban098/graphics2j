/*
 * Copyright (c) 2022-2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package example.debug;

import example.entities.components.RotationProviderComponent;
import org.alban098.engine2j.debug.DebugUtils;
import org.alban098.engine2j.debug.component.ComponentDebugInterface;
import org.alban098.engine2j.objects.entities.component.Component;

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
    if (component instanceof RotationProviderComponent) {
      RotationProviderComponent rotationProviderComponent = (RotationProviderComponent) component;
      DebugUtils.drawAttrib("Rotation (rad/s)", rotationProviderComponent.getValue(), 30, 150);
      return true;
    }
    return false;
  }
}
