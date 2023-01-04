/*
 * Copyright (c) 2022-2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering.interfaces;

import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11C.*;
import static org.lwjgl.opengl.GL45.glGetTextureSubImage;

import java.nio.IntBuffer;
import java.util.*;
import org.joml.Vector2f;
import org.lwjgl.system.MemoryUtil;
import rendering.MouseInput;
import rendering.Window;
import rendering.data.FrameBufferObject;
import rendering.entities.component.RenderableComponent;
import rendering.entities.component.TransformComponent;
import rendering.interfaces.element.Properties;
import rendering.interfaces.element.UIElement;
import rendering.renderers.Renderable;

public abstract class UserInterface implements Renderable {

  private final Window window;
  protected final InterfaceManager manager;
  private FrameBufferObject fbo;
  private final RenderableComponent renderable;
  private final TransformComponent transform;
  private final Properties properties;
  private final TreeMap<String, UIElement> uiElements = new TreeMap<>();

  protected final String name;
  private boolean visible = false;
  private final IntBuffer selectedId;

  public UserInterface(Window window, String name, InterfaceManager manager) {
    this.name = name;
    this.window = window;
    this.manager = manager;
    this.renderable = new RenderableComponent();
    this.transform = new TransformComponent();
    this.properties = new Properties(this::broadcastPropertyChanged);
    this.selectedId = MemoryUtil.memAllocInt(1);
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
      fbo = new FrameBufferObject((int) properties.getSize().x, (int) properties.getSize().y, 2);
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
    if (inside) {
      selectedId.clear();
      fbo.getTextureTarget(0).bind();
      glGetTextureSubImage(
          GL_TEXTURE_2D,
          0,
          (int) (input.getCurrentPos().x - getProperties().getPosition().x),
          (int) (input.getCurrentPos().y - getProperties().getPosition().y),
          0,
          1,
          1,
          0,
          GL_RGB,
          GL_UNSIGNED_INT,
          selectedId);
    }
    for (String key : uiElements.descendingKeySet()) {
      UIElement element = uiElements.get(key);
      if (element.propagateInput(input)) {
        return true;
      }
    }

    // Prevent camera movement when panning inside a User Interface, done after propagating input to
    // children has they have priority
    if (inside && input.canTakeControl(this)) {
      input.halt(this);
    } else if (!inside && input.hasControl(this)) {
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
    Vector2f size = properties.getSize();
    Vector2f position = new Vector2f(properties.getPosition());
    float width = 2f * size.x / window.getWidth();
    float height = 2f * size.y / window.getHeight();
    transform.setScale(width, height);
    transform.setDisplacement(
        2f * position.x / window.getWidth() - 1 + width / 2f,
        2f * -position.y / window.getHeight() + 1 - height / 2f);
    transform.update(null);
  }

  public final void broadcastPropertyChanged(
      Properties.Snapshot oldProperties, Properties.Snapshot newProperties) {
    if (!oldProperties.getSize().equals(newProperties.getSize()) && fbo != null) {
      fbo.cleanUp();
      fbo = new FrameBufferObject((int) properties.getSize().x, (int) properties.getSize().y, 2);
    }
    if (uiElements.size() > 0 && fbo == null) {
      fbo = new FrameBufferObject((int) properties.getSize().x, (int) properties.getSize().y, 2);
    }
    onPropertyChange(oldProperties, newProperties);
  }

  protected abstract void onPropertyChange(
      Properties.Snapshot oldProperties, Properties.Snapshot newProperties);

  public FrameBufferObject getFbo() {
    return fbo;
  }

  public Properties getProperties() {
    return properties;
  }

  protected final boolean isInside(Vector2f pos) {
    Vector2f topLeft = properties.getPosition();
    return pos.x >= topLeft.x
        && pos.x <= topLeft.x + properties.getSize().x
        && pos.y >= topLeft.y
        && pos.y <= topLeft.y + properties.getSize().y;
  }
}
