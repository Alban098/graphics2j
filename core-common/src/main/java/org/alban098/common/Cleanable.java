/*
 * Copyright (c) 2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package org.alban098.common;

/**
 * An interface making an Object cleanable, meaning it needs a custom behavior before destruction
 */
public interface Cleanable {

  /** The actual behavior to execute before the Object's destructor is called */
  void cleanUp();

  /** Register the element to be cleared on program exit, must be called in the constructor */
  default void initialize() {
    MemoryManager.register(this);
  }
}
