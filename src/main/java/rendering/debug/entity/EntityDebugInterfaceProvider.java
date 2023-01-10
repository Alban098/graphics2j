/*
 * Copyright (c) 2022-2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering.debug.entity;

import java.util.HashMap;
import java.util.Map;
import rendering.scene.entities.Entity;

public final class EntityDebugInterfaceProvider {

  private static final Map<Class<? extends Entity>, RenderableDebugInterface<? extends Entity>>
      entityDebugInterfaces = new HashMap<>();

  private static RenderableDebugInterface<Entity> defaultDebugInterface;

  private EntityDebugInterfaceProvider() {}

  public static void setDefault(RenderableDebugInterface<Entity> defaultDebugInterface) {
    EntityDebugInterfaceProvider.defaultDebugInterface = defaultDebugInterface;
  }

  public static void register(RenderableDebugInterface<? extends Entity> debugInterface) {
    if (debugInterface != null) {
      entityDebugInterfaces.put(debugInterface.getEntityClass(), debugInterface);
    }
  }

  public static RenderableDebugInterface<? extends Entity> provide(
      Class<? extends Entity> componentableClass) {
    return entityDebugInterfaces.getOrDefault(componentableClass, defaultDebugInterface);
  }
}
