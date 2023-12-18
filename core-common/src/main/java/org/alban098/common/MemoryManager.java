/*
 * Copyright (c) 2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package org.alban098.common;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** This static class is tasked with keeping track and cleaning up every Cleanable object */
public class MemoryManager {

  /** Just a Logger to log events */
  private static final Logger LOGGER = LoggerFactory.getLogger(MemoryManager.class);
  /** A Map of all Cleanable objects of the application, sorted by type */
  private static final Map<Class<?>, Set<Cleanable>> OBJECTS = new HashMap<>();

  /**
   * Adds a new Cleanable to the Manager, only called by {@link Cleanable#initialize()}
   *
   * @param cleanable the cleanable to add
   */
  static void register(Cleanable cleanable) {
    if (!OBJECTS.containsKey(cleanable.getClass())) {
      OBJECTS.put(cleanable.getClass(), new HashSet<>());
    }
    OBJECTS.get(cleanable.getClass()).add(cleanable);
  }

  /** Cleans up all Objects */
  public static void finish() {
    OBJECTS.forEach(
        (type, set) -> {
          LOGGER.info("Clearing objects of type '{}'", type.getSimpleName());
          set.forEach(Cleanable::cleanUp);
          set.clear();
        });
    OBJECTS.clear();
  }

  /**
   * Cleans up an Objects and stop keeping track of it
   *
   * @param cleanable the object to free
   */
  public static void free(Cleanable cleanable) {
    cleanable.cleanUp();
    if (OBJECTS.containsKey(cleanable.getClass())) {
      OBJECTS.get(cleanable.getClass()).remove(cleanable);
    }
  }
}
