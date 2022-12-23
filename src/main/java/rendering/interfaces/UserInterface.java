/*
 * Copyright (c) 2022, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering.interfaces;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.joml.Vector2f;
import org.joml.Vector3f;
import rendering.Texture;
import rendering.Window;
import rendering.entities.component.Component;
import rendering.entities.component.RenderableComponent;
import rendering.entities.component.TransformComponent;
import rendering.interfaces.element.UIElementInstance;
import rendering.renderers.Componentable;

public class UserInterface implements Componentable {

  private final Map<String, UIElementInstance> uiElements = new HashMap<>();
  private final RenderableComponent renderable;
  private final TransformComponent transform;

  private final Collection<Component> components;
  private final String name;
  private final Window window;

  private final Vector2f size;
  private final Vector2f position;

  public UserInterface(Texture background, String name, Window window) {
    this.renderable = new RenderableComponent(background);
    this.window = window;
    this.transform = new TransformComponent();
    this.name = name;
    this.components = List.of(renderable, transform);
    this.position = new Vector2f();
    this.size = new Vector2f();
  }

  public UserInterface(Vector3f background, String name, Window window) {
    this.renderable = new RenderableComponent(background);
    this.window = window;
    this.transform = new TransformComponent();
    this.name = name;
    this.components = List.of(renderable, transform);
    this.position = new Vector2f();
    this.size = new Vector2f();
  }

  public void setSize(int x, int y) {
    this.size.set(x, y);
  }

  public void setPosition(int x, int y) {
    this.position.set(x, y);
  }

  @Override
  public Componentable addComponent(Component component) {
    throw new UnsupportedOperationException();
  }

  @Override
  public <T extends Component> T getComponent(Class<T> type) {
    if (type != null && type.equals(RenderableComponent.class)) {
      return (T) renderable;
    } else if (type != null && type.equals(TransformComponent.class)) {
      return (T) transform;
    }
    return null;
  }

  @Override
  public boolean hasComponent(Class<? extends Component> type) {
    return type != null
        && (type.equals(RenderableComponent.class) || type.equals(TransformComponent.class));
  }

  @Override
  public void cleanUpInternal() {}

  @Override
  public String getName() {
    return name;
  }

  @Override
  public Collection<Component> getComponents() {
    return components;
  }

  public final void updateInternal(double elapsedTime) {
    components.forEach(c -> c.update(this));
    float width = 2f * size.x / window.getWidth();
    float height = 2f * size.y / window.getHeight();
    transform.setScale(width, height);
    transform.setDisplacement(
        2f * position.x / window.getWidth() - 1 + width / 2f,
        2f * position.y / window.getHeight() + 1 - height / 2f);
    update(elapsedTime);
  }

  @Override
  public void update(double elapsedTime) {}
}
