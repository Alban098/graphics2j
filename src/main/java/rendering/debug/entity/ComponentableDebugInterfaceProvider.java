/*
 * Copyright (c) 2022, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering.debug.entity;

import java.util.HashMap;
import java.util.Map;
import rendering.entities.Entity;
import rendering.renderers.Componentable;

public class ComponentableDebugInterfaceProvider {

  private static final Map<
          Class<? extends Componentable>, ComponentableDebugInterface<? extends Componentable>>
      entityDebugInterfaces = new HashMap<>();

  private static ComponentableDebugInterface<Entity> defaultDebugInterface;

  private ComponentableDebugInterfaceProvider() {}

  public static void setDefault(ComponentableDebugInterface<Entity> defaultDebugInterface) {
    ComponentableDebugInterfaceProvider.defaultDebugInterface = defaultDebugInterface;
  }

  public static void register(ComponentableDebugInterface<? extends Componentable> debugInterface) {
    if (debugInterface != null) {
      entityDebugInterfaces.put(debugInterface.getEntityClass(), debugInterface);
    }
  }

  public static ComponentableDebugInterface<? extends Componentable> provide(
      Class<? extends Componentable> componentableClass) {
    return entityDebugInterfaces.getOrDefault(componentableClass, defaultDebugInterface);
  }
}
