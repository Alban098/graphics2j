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
import org.joml.Vector3f;
import rendering.Texture;
import rendering.entities.component.Component;
import rendering.entities.component.Renderable;
import rendering.entities.component.Transform;
import rendering.shaders.ShaderAttribute;

public abstract class Entity {

  protected final Transform transform;
  protected final Renderable renderable;

  protected final Map<String, Component> components;

  protected final List<Entity> children;
  protected Entity parent;

  public Entity(Transform transform, Texture texture) {
    this.transform = transform;
    this.renderable = new Renderable(transform, texture);
    this.components = new HashMap<>();
    this.children = new ArrayList<>();
  }

  public Entity(Transform transform, Vector3f color, ShaderAttribute colorAttribite) {
    this.transform = transform;
    this.renderable = new Renderable(transform);
    this.renderable.setAttributes(colorAttribite, color);
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

  public void update(double elapsedTime) {
    components.values().forEach(Component::update);
  }

  public Renderable getRenderable() {
    return renderable;
  }

  public Transform getTransform() {
    return transform;
  }

  public void cleanUp() {
    renderable.cleanUp();
  }
}
