/*
 * Copyright (c) 2022-2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering.interfaces;

import static org.lwjgl.opengl.GL11C.*;

import java.util.*;
import org.joml.Vector2f;
import rendering.MouseInput;
import rendering.Window;
import rendering.data.FrameBufferObject;
import rendering.entities.component.RenderableComponent;
import rendering.entities.component.TransformComponent;
import rendering.interfaces.element.UIElement;
import rendering.interfaces.element.property.Properties;
import rendering.interfaces.element.property.RenderingProperties;
import rendering.renderers.Renderable;

public abstract class UserInterface implements Renderable {

  private final Window window;
  protected final InterfaceManager manager;
  private FrameBufferObject fbo;
  private final RenderableComponent renderable;
  private final TransformComponent transform;
  private final RenderingProperties properties;
  private final TreeMap<String, UIElement> uiElements = new TreeMap<>();

  protected final String name;
  private boolean visible = false;

  public UserInterface(Window window, String name, InterfaceManager manager) {
    this.name = name;
    this.window = window;
    this.manager = manager;
    this.renderable = new RenderableComponent();
    this.transform = new TransformComponent();
    this.properties = new RenderingProperties(this::broadcastPropertyChanged);
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

  public Collection<UIElement> getElements() {
    return uiElements.values();
  }

  public UIElement getElement(String identifier) {
    return uiElements.get(identifier);
  }

  public void addElement(String identifier, UIElement element) {
    element.setContainer(this);
    element.setParent(null);
    uiElements.put(identifier, element);
    if (fbo == null) {
      Vector2f size = properties.get(Properties.SIZE, Vector2f.class);
      fbo = new FrameBufferObject((int) size.x, (int) size.y, 1);
    }
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
    boolean inside = isInside(input.getCurrentPos());
    // Not working but the concept is there
    for (String key : uiElements.descendingKeySet()) {
      UIElement element = uiElements.get(key);
      if (element.propagateInput(input)) {
        break;
      }
    }

    // Prevent camera movement when panning inside a User Interface, done after propagating input to
    // children has they have priority
    if (inside && input.canTakeControl(this)) {
      input.halt(this);
    } else if (!inside && input.hasControl(this) && !input.isLeftButtonPressed()) {
      input.release();
    }
    return false;
  }

  public Window getWindow() {
    return window;
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

  private void updateTransform() {
    Vector2f size = properties.get(Properties.SIZE, Vector2f.class);
    Vector2f position = properties.get(Properties.POSITION, Vector2f.class);
    float width = 2f * size.x / window.getWidth();
    float height = 2f * size.y / window.getHeight();
    transform.setScale(width, height);
    transform.setDisplacement(
        2f * position.x / window.getWidth() - 1 + width / 2f,
        2f * -position.y / window.getHeight() + 1 - height / 2f);
    transform.update(null);
  }

  public final void broadcastPropertyChanged(Properties property, Object value) {
    if (property == Properties.SIZE && fbo != null) {
      fbo.cleanUp();
      Vector2f size = (Vector2f) value;
      fbo = new FrameBufferObject((int) size.x, (int) size.y, 1);
    }
    if (uiElements.size() > 0 && fbo == null) {
      Vector2f size = properties.get(Properties.SIZE, Vector2f.class);
      fbo = new FrameBufferObject((int) size.x, (int) size.y, 1);
    }
    onPropertyChange(property, value);
  }

  protected abstract void onPropertyChange(Properties property, Object value);

  public FrameBufferObject getFbo() {
    return fbo;
  }

  public RenderingProperties getProperties() {
    return properties;
  }

  protected final boolean isInside(Vector2f pos) {
    Vector2f topLeft = properties.get(Properties.POSITION, Vector2f.class);
    Vector2f size = properties.get(Properties.SIZE, Vector2f.class);
    return pos.x >= topLeft.x
        && pos.x <= topLeft.x + size.x
        && pos.y >= topLeft.y
        && pos.y <= topLeft.y + size.y;
  }
}
