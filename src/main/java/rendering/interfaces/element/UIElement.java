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
import rendering.data.FrameBufferObject;
import rendering.entities.component.RenderableComponent;
import rendering.entities.component.TransformComponent;
import rendering.interfaces.Modal;
import rendering.interfaces.UserInterface;
import rendering.renderers.Renderable;

public abstract class UIElement implements Renderable {

  private final TreeMap<String, UIElement> uiElements;
  private final RenderableComponent renderable;
  private final Properties properties;
  private final TransformComponent transform;

  private UserInterface container;
  private FrameBufferObject fbo;
  private UIElement parent;
  private boolean clicked;
  private boolean hovered;

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
    this.properties = new Properties(this::broadcastPropertyChanged);
    this.uiElements = new TreeMap<>();
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
      fbo = new FrameBufferObject((int) properties.getSize().x, (int) properties.getSize().y, 2);
    }
    if (uiElements.size() > 0 && fbo == null) {
      fbo = new FrameBufferObject((int) properties.getSize().x, (int) properties.getSize().y, 2);
    }
    onPropertyChange(oldProperties, newProperties);
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

  public Properties getProperties() {
    return properties;
  }

  public UIElement getParent() {
    return parent;
  }

  public void addElement(String identifier, UIElement element) {
    uiElements.put(identifier, element);
    element.setParent(this);
    if (fbo == null) {
      fbo = new FrameBufferObject((int) properties.getSize().x, (int) properties.getSize().y, 2);
    }
  }

  protected abstract void onPropertyChange(
      Properties.Snapshot oldProperties, Properties.Snapshot newProperties);

  protected void updateTransform() {
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

  protected final Vector2f getPositionInWindow() {
    if (parent == null) {
      return new Vector2f(properties.getPosition()).add(container.getProperties().getPosition());
    } else {
      return new Vector2f(properties.getPosition()).add(parent.getPositionInWindow());
    }
  }

  protected boolean isInside(Vector2f pos) {
    Vector2f topLeft = getPositionInWindow();
    return pos.x >= topLeft.x
        && pos.x <= topLeft.x + properties.getSize().x
        && pos.y >= topLeft.y
        && pos.y <= topLeft.y + properties.getSize().y;
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

  public Consumer<MouseInput> onClickEnd() {
    return onClickEnd;
  }

  public Consumer<MouseInput> onClickStart() {
    return onClickStart;
  }

  public Consumer<MouseInput> onHold() {
    return onHold;
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

  public Consumer<MouseInput> onEnter() {
    return onEnter;
  }

  public Consumer<MouseInput> onExit() {
    return onExit;
  }

  public Consumer<MouseInput> onInside() {
    return onInside;
  }
}
