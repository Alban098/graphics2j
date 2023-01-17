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
  protected final Map<Class<? extends Component>, Component> components;
  /** A List of all direct children of this Entity */
  protected final List<Entity> children;
  /** A link to the Parent of this Entity, can be null */
  protected Entity parent;

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
    this.components = new HashMap<>();
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
    if (hasComponent(component.getClass())) {
      throw new IllegalArgumentException(
          "Entity already has a component of type " + component.getClass().getSimpleName());
    }
    this.components.put(component.getClass(), component);
    return this;
  }

  /**
   * Retrieves the {@link Component} of a certain type if present
   *
   * @param type the class type of {@link Component} to retrieve
   * @return the {@link Component} of a certain type if present, null otherwise
   * @param <T> the type of {@link Component} to retrieve
   */
  public final <T extends Component> T getComponent(Class<T> type) {
    if (hasComponent(type)) {
      Component component = components.get(type);
      if (type.isInstance(component)) {
        return type.cast(component);
      }
    }
    return null;
  }

  /**
   * Returns whether the Entity has a {@link Component} of a certain type or not
   *
   * @param type the type to test for
   * @return whether the Entity has a {@link Component} of a certain type or not
   */
  public final boolean hasComponent(Class<? extends Component> type) {
    return components.containsKey(type);
  }

  /**
   * Updates all {@link Component}s of the Entity and call the standard {@link
   * Updatable#update(double)} routine
   *
   * @param elapsedTime the time elapsed since last update
   */
  public final void updateInternal(double elapsedTime) {
    components.values().forEach(c -> c.update(this));
    update(elapsedTime);
  }

  /** Clears the Entity by clearing all its {@link Component}s and children */
  public final void cleanUpInternal() {
    components.values().forEach(Component::cleanUp);
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
    return components.values();
  }

  /**
   * Returns the {@link RenderableComponent} of the Entity
   *
   * @return the {@link RenderableComponent} of the Entity
   */
  @Override
  public RenderableComponent getRenderable() {
    return getComponent(RenderableComponent.class);
  }

  /**
   * Returns the {@link TransformComponent} of the Entity
   *
   * @return the {@link TransformComponent} of the Entity
   */
  @Override
  public TransformComponent getTransform() {
    return getComponent(TransformComponent.class);
  }

  /** Standard cleanup routine */
  protected abstract void cleanUp();
}
