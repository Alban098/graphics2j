/*
 * Copyright (c) 2022, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering.entities;

import java.util.*;
import rendering.entities.component.Component;

public abstract class Entity {

  protected String name;
  protected final Map<Class<? extends Component>, Component> components;
  protected final List<Entity> children;
  protected Entity parent;

  public Entity() {
    this(null);
  }

  public Entity(String name) {
    this.components = new HashMap<>();
    this.children = new ArrayList<>();
    this.name = name == null ? Integer.toHexString(hashCode()) : name;
  }

  public void addChild(Entity entity) {
    children.add(entity);
    entity.setParent(this);
  }

  public void removeChild(Entity entity) {
    children.remove(entity);
    entity.setParent(null);
  }

  public List<Entity> getChildren() {
    return children;
  }

  public Entity getParent() {
    return parent;
  }

  private void setParent(Entity entity) {
    this.parent = entity;
  }

  public Entity addComponent(Component component) {
    if (hasComponent(component.getClass())) {
      throw new IllegalArgumentException(
          "Entity already has a component of type " + component.getClass().getSimpleName());
    }
    this.components.put(component.getClass(), component);
    return this;
  }

  public <T extends Component> T getComponent(Class<T> type) {
    if (hasComponent(type)) {
      Component component = components.get(type);
      if (type.isInstance(component)) {
        return type.cast(component);
      }
    }
    return null;
  }

  public boolean hasComponent(Class<? extends Component> type) {
    return components.containsKey(type);
  }

  public void update(double elapsedTime) {
    components.values().forEach(c -> c.update(this));
  }

  public void cleanUp() {
    components.values().forEach(Component::cleanUp);
  }

  public String getName() {
    return name;
  }
}
