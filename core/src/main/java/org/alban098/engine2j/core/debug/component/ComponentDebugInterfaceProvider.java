/*
 * Copyright (c) 2022-2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package org.alban098.engine2j.core.debug.component;

import java.util.HashMap;
import java.util.Map;

import org.alban098.engine2j.core.objects.entities.component.Component;

/** A static Provider for {@link ComponentDebugInterface}s */
public final class ComponentDebugInterfaceProvider {

  /** A Map of all registered debug interfaces index by {@link Component} type */
  private static final Map<Class<? extends Component>, ComponentDebugInterface<? extends Component>>
      componentDebugInterfaces = new HashMap<>();
  /** The default interface if none matches */
  private static ComponentDebugInterface<Component> defaultDebugInterface;

  /** A private constructor to prevent instantiation */
  private ComponentDebugInterfaceProvider() {}

  /**
   * Sets the default interface
   *
   * @param defaultDebugInterface the new default interface
   */
  public static void setDefault(ComponentDebugInterface<Component> defaultDebugInterface) {
    ComponentDebugInterfaceProvider.defaultDebugInterface = defaultDebugInterface;
  }

  /**
   * Registers a new interface
   *
   * @param debugInterface the interface to register
   */
  public static void register(ComponentDebugInterface<? extends Component> debugInterface) {
    if (debugInterface != null) {
      componentDebugInterfaces.put(debugInterface.getComponentClass(), debugInterface);
    }
  }

  /**
   * Retrieves the interface for a specified type, providing the default one if none is found
   *
   * @param componentType the type of {@link Component} to retrieve the interface of
   * @return the interface for a specified type, providing the default one if none is found
   */
  public static ComponentDebugInterface<? extends Component> provide(
      Class<? extends Component> componentType) {
    return componentDebugInterfaces.getOrDefault(componentType, defaultDebugInterface);
  }
}
