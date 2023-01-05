/*
 * Copyright (c) 2022-2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering.interfaces.element;

import java.util.*;
import java.util.function.Consumer;
import org.joml.Vector2f;
import rendering.MouseInput;
import rendering.Texture;
import rendering.data.FrameBufferObject;
import rendering.entities.component.RenderableComponent;
import rendering.entities.component.TransformComponent;
import rendering.interfaces.Modal;
import rendering.interfaces.UserInterface;
import rendering.interfaces.element.property.Properties;
import rendering.interfaces.element.property.RenderingProperties;
import rendering.renderers.Renderable;

public abstract class UIElement implements Renderable {

  private final TreeMap<String, UIElement> uiElements;
  private final RenderableComponent renderable;
  private final RenderingProperties properties;
  private final TransformComponent transform;

  private UserInterface container;
  private FrameBufferObject fbo;
  private UIElement parent;
  private boolean clicked = false;
  private boolean hovered = false;
  private Modal modal;

  private Consumer<MouseInput> onClickEnd = (input) -> {};
  private Consumer<MouseInput> onClickStart = (input) -> {};
  private Consumer<MouseInput> onHold = (input) -> {};
  private Consumer<MouseInput> onEnter = (input) -> {};
  private Consumer<MouseInput> onExit = (input) -> {};
  private Consumer<MouseInput> onInside = (input) -> {};

  public UIElement() {
    this.renderable = new RenderableComponent();
    this.transform = new TransformComponent();
    this.properties = new RenderingProperties(this::broadcastPropertyChanged);
    this.uiElements = new TreeMap<>();
  }

  public final RenderableComponent getRenderable() {
    renderable.setTexture(properties.get(Properties.BACKGROUND_TEXTURE, Texture.class));
    return renderable;
  }

  public final TransformComponent getTransform() {
    updateTransform();
    return transform;
  }

  public final void broadcastPropertyChanged(Properties property, Object value) {
    if (property == Properties.SIZE && fbo != null) {
      fbo.cleanUp();
      Vector2f size = (Vector2f) value;
      fbo = new FrameBufferObject((int) size.x, (int) size.y, 2);
    }
    if (uiElements.size() > 0 && fbo == null) {
      Vector2f size = properties.get(Properties.SIZE, Vector2f.class);
      fbo = new FrameBufferObject((int) size.x, (int) size.y, 2);
    }
    onPropertyChange(property, value);
  }

  public final boolean isTextured() {
    return renderable.getTexture() != null;
  }

  public final void updateInternal(double elapsedTime) {
    uiElements.forEach((k, v) -> v.updateInternal(elapsedTime));
    update(elapsedTime);
  }

  public final void setParent(UIElement parent) {
    this.parent = parent;
  }

  public final void setContainer(UserInterface container) {
    this.container = container;
  }

  public final Collection<UIElement> getElements() {
    return uiElements.values();
  }

  public final UIElement getElement(String identifier) {
    return uiElements.get(identifier);
  }

  public final FrameBufferObject getFbo() {
    return fbo;
  }

  public final boolean propagateInput(MouseInput input) {
    for (String key : uiElements.descendingKeySet()) {
      UIElement element = uiElements.get(key);
      if (element.propagateInput(input)) {
        return true;
      }
    }
    return input(input);
  }

  public void cleanUp() {
    renderable.cleanUp();
    transform.cleanUp();
  }

  public UserInterface getContainer() {
    return container;
  }

  public RenderingProperties getProperties() {
    return properties;
  }

  public UIElement getParent() {
    return parent;
  }

  public void addElement(String identifier, UIElement element) {
    uiElements.put(identifier, element);
    element.setParent(this);
    if (fbo == null) {
      Vector2f size = properties.get(Properties.SIZE, Vector2f.class);
      fbo = new FrameBufferObject((int) size.x, (int) size.y, 2);
    }
  }

  protected abstract void onPropertyChange(Properties property, Object value);

  protected void updateTransform() {
    Vector2f size = properties.get(Properties.SIZE, Vector2f.class);
    Vector2f parentSize =
        parent == null
            ? container.getProperties().get(Properties.SIZE, Vector2f.class)
            : parent.properties.get(Properties.SIZE, Vector2f.class);
    Vector2f position = new Vector2f(properties.get(Properties.POSITION, Vector2f.class));
    float width = 2f * size.x / parentSize.x;
    float height = 2f * size.y / parentSize.y;
    transform.setScale(width, height);
    transform.setDisplacement(
        2f * position.x / parentSize.x - 1 + width / 2f,
        2f * -position.y / parentSize.y + 1 - height / 2f);
    transform.update(null);
  }

  protected final Vector2f getPositionInWindow() {
    if (parent == null) {
      return new Vector2f(properties.get(Properties.POSITION, Vector2f.class))
          .add(container.getProperties().get(Properties.POSITION, Vector2f.class));
    } else {
      return new Vector2f(properties.get(Properties.POSITION, Vector2f.class))
          .add(parent.getPositionInWindow());
    }
  }

  protected boolean isInside(Vector2f pos) {
    Vector2f topLeft = getPositionInWindow();
    Vector2f size = properties.get(Properties.SIZE, Vector2f.class);
    return pos.x >= topLeft.x
        && pos.x <= topLeft.x + size.x
        && pos.y >= topLeft.y
        && pos.y <= topLeft.y + size.y;
  }

  private boolean input(MouseInput input) {
    boolean inside = isInside(input.getCurrentPos());
    if (this instanceof Hoverable) {
      ((Hoverable) this).hoverRoutine(input, inside);
    }
    if (this instanceof Clickable) {
      ((Clickable) this).clickRoutine(input, inside);
    }
    return this.hovered || this.clicked;
  }

  public Modal getModal() {
    return modal;
  }

  public void setModal(Modal modal) {
    this.modal = modal;
  }

  public abstract void update(double elapsedTime);

  public boolean isClicked() {
    return clicked;
  }

  public void setClicked(boolean clicked) {
    this.clicked = clicked;
  }

  public void onClickEnd(Consumer<MouseInput> callback) {
    this.onClickEnd = callback;
  }

  public void onClickStart(Consumer<MouseInput> callback) {
    this.onClickStart = callback;
  }

  public void onHold(Consumer<MouseInput> callback) {
    this.onHold = callback;
  }

  public void onClickEnd(MouseInput input) {
    onClickEnd.accept(input);
  }

  public void onClickStart(MouseInput input) {
    onClickStart.accept(input);
  }

  public void onHold(MouseInput input) {
    onHold.accept(input);
  }

  public boolean isHovered() {
    return hovered;
  }

  public void setHovered(boolean hovered) {
    this.hovered = hovered;
  }

  public void onEnter(Consumer<MouseInput> callback) {
    this.onEnter = callback;
  }

  public void onExit(Consumer<MouseInput> callback) {
    this.onExit = callback;
  }

  public void onInside(Consumer<MouseInput> callback) {
    this.onInside = callback;
  }

  public void onEnter(MouseInput input) {
    if (getModal() != null) {
      getModal().toggleVisibility(true);
      getModal().getProperties().set(Properties.POSITION, input.getCurrentPos());
    }
    onEnter.accept(input);
  }

  public void onExit(MouseInput input) {
    if (getModal() != null) {
      getModal().toggleVisibility(false);
    }
    onExit.accept(input);
  }

  public void onInside(MouseInput input) {
    if (getModal() != null) {
      getModal().getProperties().set(Properties.POSITION, input.getCurrentPos());
    }
    onInside.accept(input);
  }
}
