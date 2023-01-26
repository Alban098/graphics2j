/*
 * Copyright (c) 2022-2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package org.alban098.engine2j.core.objects;

import java.util.Collection;
import org.alban098.engine2j.core.objects.entities.component.Component;
import org.alban098.engine2j.core.objects.entities.component.RenderableComponent;
import org.alban098.engine2j.core.objects.entities.component.TransformComponent;
import org.alban098.engine2j.core.objects.entities.component.TransformUtils;

/** An abstract representation of an Object that can be rendered */
public interface Renderable {

  /**
   * Returns the {@link RenderableComponent} enabling the object to be renderer, should not be null
   *
   * @return the {@link RenderableComponent} enabling the object to be renderer, should not be null
   */
  RenderableComponent getRenderable();

  /**
   * Returns the {@link RenderableComponent} enabling the object to be placed, can be null ({@link
   * TransformUtils#getNullTransformBuffer()} will be used instead)
   *
   * @return the {@link RenderableComponent} enabling the object to be placed, can be null
   */
  TransformComponent getTransform();

  /**
   * Returns a Collection of {@link Component}, must at least return a {@link RenderableComponent}
   * and a {@link TransformComponent}
   */
  Collection<Component> getComponents();

  /**
   * Returns a display name for the Renderable
   *
   * @return a display name for the Renderable
   */
  String getName();
}
