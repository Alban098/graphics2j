/*
 * Copyright (c) 2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package org.alban098.common;

public abstract class Component {

  @Override
  public final int hashCode() {
    return this.getClass().hashCode();
  }

  @Override
  public final boolean equals(Object obj) {
    return this.getClass().equals(obj.getClass());
  }
}
