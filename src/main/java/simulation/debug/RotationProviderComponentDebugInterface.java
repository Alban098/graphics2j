/*
 * Copyright (c) 2022-2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package simulation.debug;

import rendering.debug.DebugUtils;
import rendering.debug.component.ComponentDebugInterface;
import rendering.scene.entities.component.Component;
import simulation.entities.components.RotationProviderComponent;

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
