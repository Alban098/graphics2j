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
import rendering.Window;
import rendering.data.FrameBufferObject;
import rendering.entities.component.RenderableComponent;
import rendering.entities.component.TransformComponent;
import rendering.interfaces.element.CornerProperties;
import rendering.renderers.Renderable;

public abstract class UserInterface implements Renderable {

  private final Window window;
  protected final InterfaceManager manager;

  private FrameBufferObject fbo;

  private final Vector2f position;
  private final Vector2f size;
  private final Vector4f color;

  private final RenderableComponent renderable;
  private final TransformComponent transform;

  private CornerProperties cornerProperties;

  private final Map<String, UIElement<?>> uiElements = new HashMap<>();

  protected final String name;
  private boolean visible = false;

  public UserInterface(Window window, Texture background, String name, InterfaceManager manager) {
    this.name = name;
    this.window = window;
    this.manager = manager;
    this.position = new Vector2f();
    this.size = new Vector2f();
    this.color = new Vector4f();
    this.renderable = new RenderableComponent(background);
    this.transform = new TransformComponent();
    this.cornerProperties = new CornerProperties();
  }

  public UserInterface(Window window, Vector4f background, String name, InterfaceManager manager) {
    this.name = name;
    this.window = window;
    this.manager = manager;
    this.position = new Vector2f();
    this.size = new Vector2f();
    this.color = background;
    this.renderable = new RenderableComponent();
    this.transform = new TransformComponent();
    this.cornerProperties = new CornerProperties();
  }

  public String getName() {
    return name;
  }

  public final void updateInternal(double elapsedTime) {
    uiElements.forEach((k, v) -> v.updateInternal(elapsedTime));
    update(elapsedTime);
  }

  public abstract void update(double elapsedTime);

  public boolean isTextured() {
    return renderable.getTexture() != null;
  }

  public Vector4f getColor() {
    return color;
  }

  public Collection<UIElement<?>> getElements() {
    return uiElements.values();
  }

  public UIElement<?> getElement(String identifier) {
    return uiElements.get(identifier);
  }

  protected void addElement(String identifier, UIElement<?> element) {
    element.setContainer(this);
    element.setParent(null);
    uiElements.put(identifier, element);
  }

  public void cleanUp() {
    renderable.cleanUp();
    transform.cleanUp();
    uiElements.forEach((k, v) -> v.cleanUp());
    uiElements.clear();
  }

  public boolean isVisible() {
    return visible;
  }

  public void toggleVisibility(boolean visible) {
    this.visible = visible;
  }

  public final boolean input(MouseInput input) {
    for (UIElement<?> element : uiElements.values()) {
      if (element.inputInternal(input)) {
        return true;
      }
    }

    // Prevent camera movement when panning inside a User Interface, done after propagating input to
    // children has they have priority
    if (isInside(input.getCurrentPos())
        && input.isLeftButtonPressed()
        && input.canTakeControl(this)) {
      input.halt(this);
    } else if (input.hasControl(this)) {
      input.release();
    }
    return false;
  }

  public Window getWindow() {
    return window;
  }

  public Vector2f getPosition() {
    return new Vector2f(position);
  }

  public Vector2f getSize() {
    return new Vector2f(size);
  }

  public UserInterface setSize(float x, float y) {
    this.size.set(x, y);
    if (fbo != null) {
      fbo.cleanUp();
    }
    fbo = new FrameBufferObject((int) x, (int) y);
    return this;
  }

  public UserInterface setPosition(float x, float y) {
    this.position.set(x, y);
    return this;
  }

  @Override
  public RenderableComponent getRenderable() {
    return renderable;
  }

  @Override
  public TransformComponent getTransform() {
    updateTransform();
    return transform;
  }

  public CornerProperties getCornerProperties() {
    return cornerProperties;
  }

  public UserInterface setCornerProperties(CornerProperties cornerProperties) {
    this.cornerProperties = cornerProperties;
    return this;
  }

  private void updateTransform() {
    Vector2f size = getSize();
    Vector2f position = new Vector2f(getPosition());
    float width = 2f * size.x / window.getWidth();
    float height = 2f * size.y / window.getHeight();
    transform.setScale(width, height);
    transform.setDisplacement(
        2f * position.x / window.getWidth() - 1 + width / 2f,
        2f * -position.y / window.getHeight() + 1 - height / 2f);
    transform.update(null);
  }

  public FrameBufferObject getFbo() {
    return fbo;
  }

  protected final boolean isInside(Vector2f pos) {
    Vector2f topLeft = getPosition();
    return pos.x >= topLeft.x
        && pos.x <= topLeft.x + size.x
        && pos.y >= topLeft.y
        && pos.y <= topLeft.y + size.y;
  }
}
