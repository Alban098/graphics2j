/*
 * Copyright (c) 2022-2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering.debug.renderable;

import java.util.HashMap;
import java.util.Map;
import rendering.renderers.Renderable;

/** A static Provider for {@link RenderableDebugInterface}s */
public final class RenderableDebugInterfaceProvider {

  /** A Map of all registered debug interfaces index by {@link Renderable} type */
  private static final Map<
          Class<? extends Renderable>, RenderableDebugInterface<? extends Renderable>>
      interfaces = new HashMap<>();

  /** A private constructor to prevent instantiation */
  private RenderableDebugInterfaceProvider() {}

  /**
   * Sets the default interface
   *
   * @param defaultDebugInterface the new default interface
   */
  public static void setDefault(
      RenderableDebugInterface<? extends Renderable> defaultDebugInterface) {
    interfaces.put(Renderable.class, defaultDebugInterface);
  }

  /**
   * Registers a new interface
   *
   * @param type the type to associate the interface with
   * @param debugInterface the interface to register
   * @param <T> the type to associate the interface with
   */
  public static <T extends Renderable> void register(
      Class<T> type, RenderableDebugInterface<T> debugInterface) {
    if (debugInterface != null) {
      interfaces.put(type, debugInterface);
    }
  }

  /**
   * Retrieves the interface for a specified type, if none is found, tries to retrieve the interface
   * associated with its superclass until getting to default
   *
   * @param renderableType the type of {@link Renderable} to retrieve the interface of
   * @return the interface for a specified type if none is found, tries to retrieve the interface
   *     associated with its superclass until getting to default
   */
  public static RenderableDebugInterface<? extends Renderable> provide(
      Class<? extends Renderable> renderableType) {
    RenderableDebugInterface<? extends Renderable> retrieved = interfaces.get(renderableType);
    Class<?> type = renderableType;
    while (retrieved == null) {
      type = type.getSuperclass();
      retrieved = interfaces.get(type);
    }
    return retrieved;
  }
}
