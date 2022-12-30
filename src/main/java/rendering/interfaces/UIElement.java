/*
 * Copyright (c) 2022, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering.interfaces;

import java.util.*;
import org.joml.Vector2f;
import rendering.MouseInput;
import rendering.data.FrameBufferObject;
import rendering.entities.component.RenderableComponent;
import rendering.entities.component.TransformComponent;
import rendering.interfaces.element.Properties;
import rendering.renderers.Renderable;

public abstract class UIElement implements Renderable {

  private final Map<String, UIElement> uiElements;
  private final RenderableComponent renderable;
  private final TransformComponent transform;

  private UserInterface container;
  private FrameBufferObject fbo;
  private final Properties properties;
  private UIElement parent;

  public UIElement() {
    this.renderable = new RenderableComponent();
    this.transform = new TransformComponent();
    this.properties = new Properties(this::broadcastPropertyChanged);
    this.uiElements = new HashMap<>();
  }

  public final RenderableComponent getRenderable() {
    renderable.setTexture(properties.getBackgroundTexture());
    return renderable;
  }

  public final TransformComponent getTransform() {
    updateTransform();
    return transform;
  }

  public final void broadcastPropertyChanged(
      Properties.Snapshot oldProperties, Properties.Snapshot newProperties) {
    if (!oldProperties.getSize().equals(newProperties.getSize()) && fbo != null) {
      fbo.cleanUp();
      fbo = new FrameBufferObject((int) properties.getSize().x, (int) properties.getSize().y);
    }
    if (uiElements.size() > 0 && fbo == null) {
      fbo = new FrameBufferObject((int) properties.getSize().x, (int) properties.getSize().y);
    }
    onPropertyChange(oldProperties, newProperties);
  }

  protected abstract void onPropertyChange(
      Properties.Snapshot oldProperties, Properties.Snapshot newProperties);

  private void updateTransform() {
    Vector2f size = properties.getSize();
    Vector2f parentSize =
        parent == null ? container.getProperties().getSize() : parent.properties.getSize();
    Vector2f position = new Vector2f(properties.getPosition());
    float width = 2f * size.x / parentSize.x;
    float height = 2f * size.y / parentSize.y;
    transform.setScale(width, height);
    transform.setDisplacement(
        2f * position.x / parentSize.x - 1 + width / 2f,
        2f * -position.y / parentSize.y + 1 - height / 2f);
    transform.update(null);
  }

  public final boolean isTextured() {
    return renderable.getTexture() != null;
  }

  public final void updateInternal(double elapsedTime) {
    uiElements.forEach((k, v) -> v.updateInternal(elapsedTime));
    update(elapsedTime);
  }

  public abstract void update(double elapsedTime);

  protected final void setParent(UIElement parent) {
    this.parent = parent;
  }

  public void cleanUp() {
    renderable.cleanUp();
    transform.cleanUp();
  }

  protected final Vector2f getPositionInWindow() {
    if (parent == null) {
      return new Vector2f(properties.getPosition()).add(container.getProperties().getPosition());
    } else {
      return new Vector2f(properties.getPosition()).add(parent.getPositionInWindow());
    }
  }

  public final void setContainer(UserInterface container) {
    this.container = container;
  }

  public Collection<UIElement> getElements() {
    return uiElements.values();
  }

  public final UIElement getElement(String identifier) {
    return uiElements.get(identifier);
  }

  public void addElement(String identifier, UIElement element) {
    uiElements.put(identifier, element);
    element.setParent(this);
    if (fbo == null) {
      fbo = new FrameBufferObject((int) properties.getSize().x, (int) properties.getSize().y);
    }
  }

  public final FrameBufferObject getFbo() {
    return fbo;
  }

  public final boolean inputInternal(MouseInput input) {
    for (UIElement element : uiElements.values()) {
      if (element.inputInternal(input)) {
        return true;
      }
    }
    return input(input);
  }

  protected final boolean isInside(Vector2f pos) {
    Vector2f topLeft = getPositionInWindow();
    return pos.x >= topLeft.x
        && pos.x <= topLeft.x + properties.getSize().x
        && pos.y >= topLeft.y
        && pos.y <= topLeft.y + properties.getSize().y;
  }

  public UserInterface getContainer() {
    return container;
  }

  public Properties getProperties() {
    return properties;
  }

  public UIElement getParent() {
    return parent;
  }

  public abstract boolean input(MouseInput input);
}
