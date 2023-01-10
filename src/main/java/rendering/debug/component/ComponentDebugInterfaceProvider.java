/*
 * Copyright (c) 2022-2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering.debug.component;

import java.util.HashMap;
import java.util.Map;
import rendering.scene.entities.component.Component;

public final class ComponentDebugInterfaceProvider {

  private static final Map<Class<? extends Component>, ComponentDebugInterface<? extends Component>>
      componentDebugInterfaces = new HashMap<>();
  private static ComponentDebugInterface<Component> defaultDebugInterface;

  private ComponentDebugInterfaceProvider() {}

  public static void setDefault(ComponentDebugInterface<Component> defaultDebugInterface) {
    ComponentDebugInterfaceProvider.defaultDebugInterface = defaultDebugInterface;
  }

  public static void register(ComponentDebugInterface<? extends Component> debugInterface) {
    if (debugInterface != null) {
      componentDebugInterfaces.put(debugInterface.getComponentClass(), debugInterface);
    }
  }

  public static ComponentDebugInterface<? extends Component> provide(
      Class<? extends Component> componentClass) {
    return componentDebugInterfaces.getOrDefault(componentClass, defaultDebugInterface);
  }
}
