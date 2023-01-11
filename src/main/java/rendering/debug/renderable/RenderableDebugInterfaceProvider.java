/*
 * Copyright (c) 2022-2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering.debug.renderable;

import java.util.HashMap;
import java.util.Map;
import rendering.renderers.Renderable;
import rendering.scene.entities.Entity;

/** A static Provider for {@link RenderableDebugInterface}s */
public final class RenderableDebugInterfaceProvider {

  /** A Map of all registered debug interfaces index by {@link Renderable} type */
  private static final Map<
          Class<? extends Renderable>, RenderableDebugInterface<? extends Renderable>>
      entityDebugInterfaces = new HashMap<>();

  /** The default interface if none matches */
  private static RenderableDebugInterface<? extends Renderable> defaultDebugInterface;

  /** A private constructor to prevent instantiation */
  private RenderableDebugInterfaceProvider() {}

  /**
   * Sets the default interface
   *
   * @param defaultDebugInterface the new default interface
   */
  public static void setDefault(
      RenderableDebugInterface<? extends Renderable> defaultDebugInterface) {
    RenderableDebugInterfaceProvider.defaultDebugInterface = defaultDebugInterface;
  }

  /**
   * Registers a new interface
   *
   * @param debugInterface the interface to register
   */
  public static void register(RenderableDebugInterface<? extends Entity> debugInterface) {
    if (debugInterface != null) {
      entityDebugInterfaces.put(debugInterface.getRenderableType(), debugInterface);
    }
  }

  /**
   * Retrieves the interface for a specified type, providing the default one if none is found
   *
   * @param renderableType the type of {@link Renderable} to retrieve the interface of
   * @return the interface for a specified type, providing the default one if none is found
   */
  public static RenderableDebugInterface<? extends Renderable> provide(
      Class<? extends Renderable> renderableType) {
    return entityDebugInterfaces.getOrDefault(renderableType, defaultDebugInterface);
  }
}
