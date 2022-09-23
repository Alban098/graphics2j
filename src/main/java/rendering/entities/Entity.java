/*
 * Copyright (c) 2022, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering.entities;

import java.util.HashMap;
import java.util.Map;
import rendering.Texture;

public abstract class Entity {

  protected final Transform transform;
  private final Renderable renderable;
  protected final Map<String, Component> components;

  public Entity(Transform transform, Texture texture) {
    this.components = new HashMap<>();
    this.transform = transform;
    this.renderable = new Renderable(texture);
    renderable.link(this.transform);
  }

  protected abstract void update(double elapsedTime);

  Renderable getRenderable() {
    return renderable;
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

  public void process(double elapsedTime) {
    components.values().forEach(Component::update);
    this.update(elapsedTime);
  }

  public void cleanUp() {
    renderable.cleanUp();
  }
}
