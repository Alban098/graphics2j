/*
 * Copyright (c) 2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package org.alban098.graphics2j.interfaces;

import org.alban098.common.Transform;
import org.alban098.common.TransformUtils;
import org.alban098.graphics2j.common.components.RenderElement;

/** Represent the highest level of an Element that can be rendered by the UI rendering pipeline */
public interface UIRenderable {

  /**
   * Returns the {@link RenderElement} enabling the object to be renderer, should not be null
   *
   * @return the {@link RenderElement} enabling the object to be renderer, should not be null
   */
  RenderElement getRenderable();

  /**
   * Returns the {@link Transform} enabling the object to be placed, can be null ({@link
   * TransformUtils#getNullTransformBuffer()} will be used instead)
   *
   * @return the {@link Transform} enabling the object to be placed, can be null
   */
  Transform getTransform();

  /**
   * Returns a display name for the UIRenderable
   *
   * @return a display name for the UIRenderable
   */
  String getName();
}
