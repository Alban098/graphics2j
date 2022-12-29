/*
 * Copyright (c) 2022, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering.interfaces;

import java.util.*;
import org.joml.Vector2f;
import org.joml.Vector4f;
import rendering.MouseInput;
import rendering.Texture;
import rendering.data.FrameBufferObject;
import rendering.entities.component.RenderableComponent;
import rendering.entities.component.TransformComponent;
import rendering.interfaces.element.CornerProperties;
import rendering.renderers.Renderable;

public abstract class UIElement<T extends UIElement<?>> implements Renderable {

  protected UserInterface container;
  protected FrameBufferObject fbo;

  protected final Vector2f position;
  protected final Vector2f size;
  protected final Vector4f color;
  protected final RenderableComponent renderable;
  protected final TransformComponent transform;
  protected CornerProperties cornerProperties;

  protected UIElement<?> parent;
  protected final Map<String, UIElement<?>> uiElements;

  public UIElement(Vector4f color) {
    this.position = new Vector2f();
    this.size = new Vector2f();
    this.color = color;
    this.renderable = new RenderableComponent();
    this.transform = new TransformComponent();
    this.cornerProperties = new CornerProperties();
    this.uiElements = new HashMap<>();
  }

  public UIElement(Texture texture) {
    this.position = new Vector2f();
    this.size = new Vector2f();
    this.color = new Vector4f();
    this.renderable = new RenderableComponent(texture);
    this.transform = new TransformComponent();
    this.cornerProperties = new CornerProperties();
    this.uiElements = new HashMap<>();
  }

  public CornerProperties getCornerProperties() {
    return cornerProperties;
  }

  public T setCornerProperties(CornerProperties cornerProperties) {
    this.cornerProperties = cornerProperties;
    return (T) this;
  }

  public RenderableComponent getRenderable() {
    return renderable;
  }

  public TransformComponent getTransform() {
    updateTransform();
    return transform;
  }

  public Vector2f getPosition() {
    return new Vector2f(position);
  }

  public Vector2f getSize() {
    return new Vector2f(size);
  }

  public T setSize(float x, float y) {
    this.size.set(x, y);
    if (fbo != null) {
      fbo.cleanUp();
    }
    if (uiElements.size() > 0) {
      fbo = new FrameBufferObject((int) x, (int) y);
    }
    return (T) this;
  }

  public T setPosition(float x, float y) {
    this.position.set(x, y);
    return (T) this;
  }

  private void updateTransform() {
    Vector2f size = getSize();
    Vector2f parentSize = parent == null ? container.getSize() : parent.getSize();
    Vector2f position = new Vector2f(getPosition());
    float width = 2f * size.x / parentSize.x;
    float height = 2f * size.y / parentSize.y;
    transform.setScale(width, height);
    transform.setDisplacement(
        2f * position.x / parentSize.x - 1 + width / 2f,
        2f * -position.y / parentSize.y + 1 - height / 2f);
    transform.update(null);
  }

  public boolean isTextured() {
    return renderable.getTexture() != null;
  }

  public Vector4f getColor() {
    return color;
  }

  public void updateInternal(double elapsedTime) {
    uiElements.forEach((k, v) -> v.updateInternal(elapsedTime));
    update(elapsedTime);
  }

  public abstract void update(double elapsedTime);

  protected void setParent(UIElement<?> parent) {
    this.parent = parent;
  }

  public void cleanUp() {
    renderable.cleanUp();
    transform.cleanUp();
  }

  protected Vector2f getPositionInWindow() {
    if (parent == null) {
      return new Vector2f(position).add(container.getPosition());
    } else {
      return new Vector2f(position).add(parent.getPositionInWindow());
    }
  }

  public void setContainer(UserInterface container) {
    this.container = container;
  }

  public Collection<UIElement<?>> getElements() {
    return uiElements.values();
  }

  public UIElement<?> getElement(String identifier) {
    return uiElements.get(identifier);
  }

  public void addElement(String identifier, UIElement<?> element) {
    uiElements.put(identifier, element);
    element.setParent(this);
    if (fbo == null) {
      fbo = new FrameBufferObject((int) getSize().x, (int) getSize().y);
    }
  }

  public FrameBufferObject getFbo() {
    return fbo;
  }

  public boolean inputInternal(MouseInput input) {
    for (UIElement<?> element : uiElements.values()) {
      if (element.inputInternal(input)) {
        return true;
      }
    }
    return input(input);
  }

  protected final boolean isInside(Vector2f pos) {
    Vector2f topLeft = getPositionInWindow();
    return pos.x >= topLeft.x
        && pos.x <= topLeft.x + size.x
        && pos.y >= topLeft.y
        && pos.y <= topLeft.y + size.y;
  }

  public abstract boolean input(MouseInput input);
}
