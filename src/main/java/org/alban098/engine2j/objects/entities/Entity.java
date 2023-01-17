/*
 * Copyright (c) 2022-2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package org.alban098.engine2j.objects.entities;

import java.util.*;
import org.alban098.engine2j.objects.Renderable;
import org.alban098.engine2j.objects.Updatable;
import org.alban098.engine2j.objects.entities.component.Component;
import org.alban098.engine2j.objects.entities.component.RenderableComponent;
import org.alban098.engine2j.objects.entities.component.TransformComponent;

/**
 * Represents a Base Entity, every Object to be rendered on the Scene that isn't a User Interface
 * need to be derived from this class An Entity can have components and children, Transform
 * component of Children will take as origin the current transform of its parent recursively,
 * therefor any transform applied to an Entity while be applied recursively to its children
 */
public abstract class Entity implements Renderable, Updatable {

  /** The name of the Entity */
  protected final String name;
  /** A Map of all {@link Component} of the Entity indexed by type */
  protected final List<Component> components;
  /** A List of all direct children of this Entity */
  protected final List<Entity> children;
  /** A link to the Parent of this Entity, can be null */
  protected Entity parent;

  private TransformComponent transformComponent;
  private RenderableComponent renderableComponent;

  /** Creates a new Empty Entity */
  public Entity() {
    this(null);
  }

  /**
   * Creates a new Empty Entity with a name
   *
   * @param name the name of the Entity
   */
  public Entity(String name) {
    this.components = new LinkedList<>();
    this.children = new ArrayList<>();
    this.name = name == null ? Integer.toHexString(hashCode()) : name;
  }

  /**
   * Adds a new child to the Entity and update its parent link
   *
   * @param entity the entity to add
   */
  public final void addChild(Entity entity) {
    children.add(entity);
    entity.setParent(this);
  }

  /**
   * Removes a child from the Entity and update its parent link
   *
   * @param entity the entity to remove
   */
  public final void removeChild(Entity entity) {
    children.remove(entity);
    entity.setParent(null);
  }

  /**
   * Returns a List of all children of this Entity
   *
   * @return a List of all children of this Entity
   */
  public final List<Entity> getChildren() {
    return children;
  }

  /**
   * Returns the parent of this Entity
   *
   * @return the parent of this Entity, null if none
   */
  public final Entity getParent() {
    return parent;
  }

  /**
   * Sets the parent of this Entity
   *
   * @param entity the new parent of the Entity
   */
  private void setParent(Entity entity) {
    this.parent = entity;
  }

  /**
   * Adds a new {@link Component} to the Entity if not already present
   *
   * @param component the {@link Component} to add
   * @return a reference to the current Entity to chain calls
   */
  public final Entity addComponent(Component component) {
    if (component instanceof TransformComponent) {
      transformComponent = (TransformComponent) component;
    } else if (component instanceof RenderableComponent) {
      renderableComponent = (RenderableComponent) component;
    } else {
      this.components.add(component);
    }
    return this;
  }

  /**
   * Updates all {@link Component}s of the Entity and call the standard {@link
   * Updatable#update(double)} routine
   *
   * @param elapsedTime the time elapsed since last update
   */
  public final void updateInternal(double elapsedTime) {
    components.forEach(c -> c.update(this, elapsedTime));
    if (renderableComponent != null) {
      renderableComponent.update(this, elapsedTime);
    }
    if (transformComponent != null) {
      transformComponent.update(this, elapsedTime);
    }
    update(elapsedTime);
  }

  /** Clears the Entity by clearing all its {@link Component}s and children */
  public final void cleanUpInternal() {
    components.forEach(Component::cleanUp);
    renderableComponent.cleanUp();
    transformComponent.cleanUp();
    children.forEach(e -> e.setParent(null));
    children.clear();
    cleanUp();
  }

  /**
   * Returns the name of the Entity
   *
   * @return the name of the Entity
   */
  public String getName() {
    return name;
  }

  /**
   * Returns a Collection of all the {@link Component}s of the Entity
   *
   * @return a Collection of all the {@link Component}s of the Entity
   */
  public Collection<Component> getComponents() {
    return components;
  }

  /**
   * Returns the {@link RenderableComponent} of the Entity
   *
   * @return the {@link RenderableComponent} of the Entity
   */
  @Override
  public RenderableComponent getRenderable() {
    return renderableComponent;
  }

  /**
   * Returns the {@link TransformComponent} of the Entity
   *
   * @return the {@link TransformComponent} of the Entity
   */
  @Override
  public TransformComponent getTransform() {
    return transformComponent;
  }

  /** Standard cleanup routine */
  protected abstract void cleanUp();
}
