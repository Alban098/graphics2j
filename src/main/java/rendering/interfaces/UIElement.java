/*
 * Copyright (c) 2022, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering.interfaces;

import org.joml.Vector2f;
import org.joml.Vector4f;
import rendering.Texture;
import rendering.Window;
import rendering.entities.component.RenderableComponent;
import rendering.entities.component.TransformComponent;
import rendering.renderers.Renderable;

public abstract class UIElement<T extends UIElement<?>> implements Renderable {

  private final Window mainWindow;

  protected final Vector2f position;
  protected final Vector2f size;
  protected final Vector4f color;
  protected final RenderableComponent renderable;
  protected final TransformComponent transform;

  protected UIElement<?> parent;

  public UIElement(Window mainWindow, Vector4f color, UIElement<?> parent) {
    this.mainWindow = mainWindow;
    this.position = new Vector2f();
    this.size = new Vector2f();
    this.color = color;
    this.renderable = new RenderableComponent();
    this.transform = new TransformComponent();
    this.parent = parent;
  }

  public UIElement(Window mainWindow, Texture texture, UIElement<?> parent) {
    this.mainWindow = mainWindow;
    this.position = new Vector2f();
    this.size = new Vector2f();
    this.color = new Vector4f();
    this.renderable = new RenderableComponent(texture);
    this.transform = new TransformComponent();
    this.parent = parent;
  }

  public RenderableComponent getRenderable() {
    return renderable;
  }

  public TransformComponent getTransform() {
    updateTransform();
    return transform;
  }

  public Vector2f getPosition() {
    return position;
  }

  public Vector2f getSize() {
    return size;
  }

  public T setSize(int x, int y) {
    this.size.set(x, y);
    return (T) this;
  }

  public T setPosition(int x, int y) {
    this.position.set(x, y);
    return (T) this;
  }

  private void updateTransform() {
    Vector2f size = getSize();
    Vector2f position = new Vector2f(getPosition());
    if (parent != null) {
      position.add(parent.getPosition());
    }
    float width = 2f * size.x / mainWindow.getWidth();
    float height = 2f * size.y / mainWindow.getHeight();
    transform.setScale(width, height);
    transform.setDisplacement(
        2f * position.x / mainWindow.getWidth() - 1 + width / 2f,
        2f * -position.y / mainWindow.getHeight() + 1 - height / 2f);
    transform.update(null);
  }

  public boolean isTextured() {
    return renderable.getTexture() != null;
  }

  public Vector4f getColor() {
    return color;
  }

  public abstract void update(double elapsedTime, UIElement<?> parent);

  protected void setParent(UIElement<?> parent) {
    this.parent = parent;
  }

  public void cleanUp() {
    renderable.cleanUp();
    transform.cleanUp();
  }

  protected Vector2f getAbsolutePosition() {
    if (parent == null) {
      return position;
    } else {
      return new Vector2f(position).add(parent.getAbsolutePosition());
    }
  }
}
