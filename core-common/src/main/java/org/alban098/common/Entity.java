/*
 * Copyright (c) 2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package org.alban098.common;

import java.util.HashSet;
import java.util.Set;
import org.joml.Vector2f;

/** Main Entity interface for all Engine2J modules */
public abstract class Entity {

  protected final Transform transform;
  protected final Set<Component> components;

  public Entity(Vector2f position, Vector2f scale, float rotation) {
    this.transform = new Transform(position, scale, rotation);
    this.components = new HashSet<>();
  }
  /**
   * Returns the {@link Transform} enabling the object to be placed, can be null ({@link
   * TransformUtils#getNullTransformBuffer()} will be used instead)
   *
   * @return the {@link Transform} enabling the object to be placed, can be null
   */
  public Transform getTransform() {
    return transform;
  }

  public void addComponent(Component component) {
    if (components.contains(component)) {
      throw new IllegalArgumentException(
          "Entity already have a '" + component.getClass() + "' component");
    }
    this.components.add(component);
  }

  public Set<Component> getComponents() {
    return components;
  }
}
