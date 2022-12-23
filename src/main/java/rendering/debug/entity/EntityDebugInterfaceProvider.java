/*
 * Copyright (c) 2022, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering.debug.entity;

import java.util.HashMap;
import java.util.Map;
import rendering.entities.Entity;

public class EntityDebugInterfaceProvider {

  private static final Map<Class<? extends Entity>, EntityDebugInterface<? extends Entity>>
      entityDebugInterfaces = new HashMap<>();

  private static EntityDebugInterface<Entity> defaultDebugInterface;

  private EntityDebugInterfaceProvider() {}

  public static void setDefault(EntityDebugInterface<Entity> defaultDebugInterface) {
    EntityDebugInterfaceProvider.defaultDebugInterface = defaultDebugInterface;
  }

  public static void register(EntityDebugInterface<? extends Entity> debugInterface) {
    if (debugInterface != null) {
      entityDebugInterfaces.put(debugInterface.getEntityClass(), debugInterface);
    }
  }

  public static EntityDebugInterface<? extends Entity> provide(
      Class<? extends Entity> entityClass) {
    return entityDebugInterfaces.getOrDefault(entityClass, defaultDebugInterface);
  }
}
