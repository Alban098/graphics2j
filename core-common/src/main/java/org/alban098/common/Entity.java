/*
 * Copyright (c) 2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package org.alban098.common;

/** Main Entity interface for all Engine2J modules */
public interface Entity {

  /**
   * Returns the {@link Transform} enabling the object to be placed, can be null ({@link
   * TransformUtils#getNullTransformBuffer()} will be used instead)
   *
   * @return the {@link Transform} enabling the object to be placed, can be null
   */
  Transform getTransform();
}
