/*
 * Copyright (c) 2022, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering.entities;

import java.util.HashMap;
import java.util.Map;

public class Entity {

  private final Transform transform;
  private final Renderable renderable;
  private final Map<Class<?>, Component> components;

  public Entity() {
    this(new Transform(), new Renderable(null));
  }

  public Entity(Transform transform, Renderable renderable) {
    this.components = new HashMap<>();
    this.transform = transform;
    this.renderable = renderable;
    renderable.updateQuad(this.transform);
  }

  public Transform getTransform() {
    return transform;
  }

  public Renderable getRenderable() {
    return renderable;
  }

  public void addComponent(Component component) {
    this.components.put(component.getClass(), component);
  }

  public <T extends Component> T getComponent(Class<T> tClass) {
    if (hasComponent(tClass)) {
      return tClass.cast(components.get(tClass));
    }
    return null;
  }

  public void cleanUp() {}

  public <T> boolean hasComponent(Class<T> tClass) {
    return components.containsKey(tClass);
  }

  public void update() {
    components.values().forEach(Component::update);
    renderable.updateQuad(transform);
  }
}
