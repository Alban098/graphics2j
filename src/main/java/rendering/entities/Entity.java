/*
 * Copyright (c) 2022, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering.entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import rendering.Texture;
import rendering.entities.component.Component;
import rendering.entities.component.Transform;

public abstract class Entity extends RenderableObject {

  protected final Map<String, Component> components;

  protected final List<Entity> children;
  protected Entity parent;

  public Entity(Transform transform, Texture texture) {
    super(transform, texture);
    this.components = new HashMap<>();
    this.children = new ArrayList<>();
  }

  public void addChild(Entity entity) {
    children.add(entity);
    entity.getTransform().setParent(getTransform());
    entity.setParent(this);
  }

  public void removeChild(Entity entity) {
    children.remove(entity);
    entity.getTransform().setParent(null);
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

  public void addComponent(String name, Component component) {
    this.components.put(name, component);
  }

  public <T extends Component> T getComponent(String name, Class<T> tClass) {
    if (hasComponent(name)) {
      Component component = components.get(name);
      if (tClass.isInstance(component)) {
        return tClass.cast(component);
      }
    }
    return null;
  }

  public boolean hasComponent(String name) {
    return components.containsKey(name);
  }

  @Override
  public void update(double elapsedTime) {
    components.values().forEach(Component::update);
  }
}
