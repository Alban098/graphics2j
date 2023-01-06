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
import rendering.data.FramebufferObject;
import rendering.entities.component.RenderableComponent;
import rendering.entities.component.TransformComponent;
import rendering.interfaces.Modal;
import rendering.interfaces.UserInterface;
import rendering.interfaces.element.property.Properties;
import rendering.interfaces.element.property.RenderingProperties;
import rendering.interfaces.element.text.Textable;
import rendering.renderers.Renderable;

/**
 * Represents an abstract UIElement that can be placed in a {@link UserInterface} or another
 * UIElement and me be interacted with depending on concrete implementation
 */
public abstract class UIElement implements Renderable {

  /** A Map of all the direct children of this element */
  private final TreeMap<String, UIElement> uiElements;
  /** A Collection of all direct children that are {@link Textable} */
  private final Collection<Textable> textables;
  /** The {@link RenderableComponent} used to render this element to the screen */
  private final RenderableComponent renderable;
  /** The {@link TransformComponent} used to place this element in its container */
  private final TransformComponent transform;
  /** The {@link RenderingProperties} of this UserInterface, not recursive for now */
  private final RenderingProperties properties;
  /** The {@link UserInterface} at the top of the hierarchy containing this element */
  private UserInterface container;
  /** The {@link FramebufferObject} where this UserInterface is rendered */
  private FramebufferObject fbo;
  /** The parent of this element, may be null if at the top of the hierarchy */
  private UIElement parent;
  /**
   * A flag indicating if the element is clicked, only relevant if concrete implementation
   * implements {@link Clickable}
   */
  private boolean clicked = false;
  /**
   * A flag indicating if the element is hovered, only relevant if concrete implementation
   * implements {@link Hoverable}
   */
  private boolean hovered = false;
  /**
   * A {@link Modal} to be displayed when hovering, only relevant if concrete implementation
   * implements {@link Hoverable}
   */
  private Modal modal;
  /**
   * A callback to call when element stop being clicked, only relevant if concrete implementation
   * implements {@link Clickable}
   */
  private Consumer<MouseInput> onClickEnd = (input) -> {};
  /**
   * A callback to call when element start being clicked, only relevant if concrete implementation
   * implements {@link Clickable}
   */
  private Consumer<MouseInput> onClickStart = (input) -> {};
  /**
   * A callback to call when element is being clicked, only relevant if concrete implementation
   * implements {@link Clickable}
   */
  private Consumer<MouseInput> onHold = (input) -> {};
  /**
   * A callback to call when element start being hovered, only relevant if concrete implementation
   * implements {@link Hoverable}
   */
  private Consumer<MouseInput> onEnter = (input) -> {};
  /**
   * A callback to call when element stop being hovered, only relevant if concrete implementation
   * implements {@link Hoverable}
   */
  private Consumer<MouseInput> onExit = (input) -> {};
  /**
   * A callback to call when element is being hovered, only relevant if concrete implementation
   * implements {@link Hoverable}
   */
  private Consumer<MouseInput> onInside = (input) -> {};

  /** Creates a new UIElement and create the necessary element for rendering */
  public UIElement() {
    this.renderable = new RenderableComponent();
    this.transform = new TransformComponent();
    this.properties = new RenderingProperties(this::broadcastPropertyChanged);
    this.uiElements = new TreeMap<>();
    this.textables = new ArrayList<>();
  }

  /**
   * Returns the {@link RenderableComponent} used to render the UIElement
   *
   * @return the {@link RenderableComponent} used to render the UIElement
   */
  public final RenderableComponent getRenderable() {
    renderable.setTexture(properties.get(Properties.BACKGROUND_TEXTURE, Texture.class));
    return renderable;
  }

  /**
   * Returns the {@link TransformComponent} used to place the UIElement
   *
   * @return the {@link TransformComponent} used to place the UIElement
   */
  public final TransformComponent getTransform() {
    updateTransform();
    return transform;
  }

  /**
   * Returns the {@link UIElement} parenting this UIElement
   *
   * @return the {@link UIElement} parenting this UIElement
   */
  public UIElement getParent() {
    return parent;
  }

  /**
   * Sets the parent of this UIElement
   *
   * @param parent the new parent of this UIElement
   */
  public final void setParent(UIElement parent) {
    this.parent = parent;
  }

  /**
   * Returns the {@link UserInterface} containing this UIElement
   *
   * @return the {@link UserInterface} containing this UIElement
   */
  public UserInterface getContainer() {
    return container;
  }

  /**
   * Sets the {@link UserInterface} containing this UIElement
   *
   * @param container the new container of this UIElement
   */
  public final void setContainer(UserInterface container) {
    this.container = container;
  }

  /**
   * Returns a {@link Collection} of all the direct children of this UserInterface
   *
   * <p>/!\ Do not override, used by the {@link rendering.renderers.interfaces.InterfaceRenderer}
   * /!\
   *
   * @return a {@link Collection} of all the direct children of this UserInterface
   */
  public final Collection<UIElement> getElements() {
    return uiElements.values();
  }

  /**
   * Retrieves a child by its identifier
   *
   * @param identifier the identifier of the child to retrieve
   * @return the retrieves {@link UIElement}, null if not found
   */
  public final UIElement getElement(String identifier) {
    return uiElements.get(identifier);
  }

  /**
   * Adds a new child to this UIElement
   *
   * @param identifier the identifier of the new {@link UIElement}
   * @param element the {@link UIElement} to add
   */
  public void addElement(String identifier, UIElement element) {
    uiElements.put(identifier, element);
    element.setParent(this);
    if (fbo == null) {
      Vector2f size = properties.get(Properties.SIZE, Vector2f.class);
      fbo = new FramebufferObject((int) size.x, (int) size.y, 2);
    }
    if (element instanceof Textable) {
      textables.add((Textable) element);
      ((Textable) element).precomputeModels();
    }
  }

  /**
   * Return the {@link FramebufferObject} the UIElement is rendered into
   *
   * @return the {@link FramebufferObject} the UIElement is rendered into
   */
  public final FramebufferObject getFbo() {
    return fbo;
  }

  /**
   * Returns the {@link RenderingProperties} of this UIElement, can be edited
   *
   * @return the {@link RenderingProperties} of this UIElement
   */
  public RenderingProperties getProperties() {
    return properties;
  }

  /**
   * Return whether the UserInterface's background is a {@link rendering.Texture} or not
   *
   * @return is the UserInterface's background a {@link rendering.Texture} or not
   */
  public final boolean isTextured() {
    return renderable.getTexture() != null;
  }

  /**
   * Returns the {@link Modal} attached to this UIElement
   *
   * @return the {@link Modal} attached to this UIElement
   */
  public Modal getModal() {
    return modal;
  }

  /**
   * Attaches a {@link Modal} to this UIElement
   *
   * @param modal the {@link Modal} to attach
   */
  public void setModal(Modal modal) {
    this.modal = modal;
  }

  /**
   * Returns whether the UIElement is clicked or not, only relevant if the concrete implementation
   * implements {@link Clickable}
   *
   * @return whether the UIElement is clicked or not
   */
  public boolean isClicked() {
    return clicked;
  }

  /**
   * Sets the clicked state of this UIElement, only relevant if the concrete implementation
   * implements {@link Clickable}
   *
   * @param clicked the new state of the UIElement
   */
  public void setClicked(boolean clicked) {
    this.clicked = clicked;
  }

  /**
   * Sets the callback to call when the UIElement stop being clicked, only relevant if the concrete
   * implementation implements {@link Clickable}
   *
   * @param callback the callback to set
   */
  public void onClickEnd(Consumer<MouseInput> callback) {
    this.onClickEnd = callback;
  }

  /**
   * Sets the callback to call when the UIElement start being clicked, only relevant if the concrete
   * implementation implements {@link Clickable}
   *
   * @param callback the callback to set
   */
  public void onClickStart(Consumer<MouseInput> callback) {
    this.onClickStart = callback;
  }

  /**
   * Sets the callback to call when the UIElement is being clicked, only relevant if the concrete
   * implementation implements {@link Clickable}
   *
   * @param callback the callback to set
   */
  public void onHold(Consumer<MouseInput> callback) {
    this.onHold = callback;
  }

  /**
   * Applies the onClickEnd callback if defined
   *
   * @param input the input to process
   */
  public void onClickEnd(MouseInput input) {
    if (onClickEnd != null) {
      onClickEnd.accept(input);
    }
  }

  /**
   * Applies the onClickStart callback if defined
   *
   * @param input the input to process
   */
  public void onClickStart(MouseInput input) {
    if (onClickStart != null) {
      onClickStart.accept(input);
    }
  }

  /**
   * Applies the onHold callback if defined
   *
   * @param input the input to process
   */
  public void onHold(MouseInput input) {
    if (onHold != null) {
      onHold.accept(input);
    }
  }

  /**
   * Returns whether the UIElement is hovered or not, only relevant if the concrete implementation
   * implements {@link Hoverable}
   *
   * @return whether the UIElement is hovered or not
   */
  public boolean isHovered() {
    return hovered;
  }

  /**
   * Sets the hovered state of this UIElement, only relevant if the concrete implementation
   * implements {@link Hoverable}
   *
   * @param hovered the new state of the UIElement
   */
  public void setHovered(boolean hovered) {
    this.hovered = hovered;
  }

  /**
   * Sets the callback to call when the UIElement start being hovered, only relevant if the concrete
   * implementation implements {@link Hoverable}
   *
   * @param callback the callback to set
   */
  public void onEnter(Consumer<MouseInput> callback) {
    this.onEnter = callback;
  }

  /**
   * Sets the callback to call when the UIElement stop being hovered, only relevant if the concrete
   * implementation implements {@link Hoverable}
   *
   * @param callback the callback to set
   */
  public void onExit(Consumer<MouseInput> callback) {
    this.onExit = callback;
  }

  /**
   * Sets the callback to call when the UIElement is being hovered, only relevant if the concrete
   * implementation implements {@link Hoverable}
   *
   * @param callback the callback to set
   */
  public void onInside(Consumer<MouseInput> callback) {
    this.onInside = callback;
  }

  /**
   * Applies the onEnter callback if defined, also displays the {@link Modal} if defined
   *
   * @param input the input to process
   */
  public void onEnter(MouseInput input) {
    if (getModal() != null) {
      getModal().setVisible(true);
      getModal().getProperties().set(Properties.POSITION, input.getCurrentPos());
    }
    if (onEnter != null) {
      onEnter.accept(input);
    }
  }

  /**
   * Applies the onExit callback if defined, also hide the {@link Modal} if defined
   *
   * @param input the input to process
   */
  public void onExit(MouseInput input) {
    if (getModal() != null) {
      getModal().setVisible(false);
    }
    if (onExit != null) {
      onExit.accept(input);
    }
  }

  /**
   * Applies the onInside callback if defined, also move the {@link Modal} if defined
   *
   * @param input the input to process
   */
  public void onInside(MouseInput input) {
    if (getModal() != null) {
      getModal().getProperties().set(Properties.POSITION, input.getCurrentPos());
    }
    if (onInside != null) {
      onInside.accept(input);
    }
  }

  /**
   * Updates the UserInterface and all its direct children
   *
   * @param elapsedTime the time elapsed since last call to update in seconds
   */
  public final void updateInternal(double elapsedTime) {
    uiElements.forEach((k, v) -> v.updateInternal(elapsedTime));
    update(elapsedTime);
  }

  /** Clear this UIElement by clearing its component and children */
  public void cleanUp() {
    uiElements.values().forEach(UIElement::cleanUp);
    uiElements.clear();
    renderable.cleanUp();
    transform.cleanUp();
  }

  /**
   * Processes user inputs recursively, by firstly propagating them to the UIElement's children,
   * then calling {@link UIElement#update(double)}
   *
   * @param input a wrapper for the current state of user inputs
   * @return true if this UIElement or one of its children has caught the input, false otherwise
   */
  public final boolean propagateInput(MouseInput input) {
    for (String key : uiElements.descendingKeySet()) {
      UIElement element = uiElements.get(key);
      if (element.propagateInput(input)) {
        return true;
      }
    }
    return input(input);
  }

  /**
   * Updates the {@link TransformComponent} to reflect the current position and size of the
   * UIElement in its container
   */
  protected void updateTransform() {
    Vector2f size = properties.get(Properties.SIZE, Vector2f.class);
    Vector2f parentSize =
        parent == null
            ? container.getProperties().get(Properties.SIZE, Vector2f.class)
            : parent.properties.get(Properties.SIZE, Vector2f.class);
    Vector2f position = new Vector2f(properties.get(Properties.POSITION, Vector2f.class));

    // transform the size from pixels space to OpenGL coordinate system
    float width = 2f * size.x / parentSize.x;
    float height = 2f * size.y / parentSize.y;
    transform.setScale(width, height);

    // set the position of the UserInterface in OpenGL coordinate system
    transform.setDisplacement(
        2f * position.x / parentSize.x - 1 + width / 2f,
        2f * -position.y / parentSize.y + 1 - height / 2f);

    // apply those transformations to the component
    transform.update(null);
  }

  /**
   * Return whether a point is inside of this UIElement or not, being on the edge is considered
   * being inside
   *
   * @implSpec Being on the edge must be considered being inside
   * @implNote Must be overridden if not a Rectangle
   * @param pos the position of the point to check
   * @return true if the point is inside the UIElement, false otherwise
   */
  protected boolean isInside(Vector2f pos) {
    Vector2f topLeft = getPositionInWindow();
    Vector2f size = properties.get(Properties.SIZE, Vector2f.class);
    return pos.x >= topLeft.x
        && pos.x <= topLeft.x + size.x
        && pos.y >= topLeft.y
        && pos.y <= topLeft.y + size.y;
  }

  /**
   * Returns the position of the element in the Window instead of in ts container, in pixels
   *
   * @return the position of the element in the Window
   */
  protected final Vector2f getPositionInWindow() {
    if (parent == null) {
      return new Vector2f(properties.get(Properties.POSITION, Vector2f.class))
          .add(container.getProperties().get(Properties.POSITION, Vector2f.class));
    } else {
      return new Vector2f(properties.get(Properties.POSITION, Vector2f.class))
          .add(parent.getPositionInWindow());
    }
  }

  /**
   * Processes user inputs if necessary, only relevant if concrete implementation implements {@link
   * Clickable} and/or {@link Hoverable}
   *
   * @param input the user input to process
   * @return true if the input has been caught, false otherwise
   */
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

  /**
   * Called every time a {@link Properties} of the UserInterface is changed, resizes the FBO if
   * necessary and calls the standard {@link UIElement#onPropertyChange(Properties, Object)} routine
   *
   * @param property the changed {@link Properties}
   * @param value the new value
   */
  private void broadcastPropertyChanged(Properties property, Object value) {
    if (property == Properties.SIZE && fbo != null) {
      fbo.cleanUp();
      Vector2f size = (Vector2f) value;
      fbo = new FramebufferObject((int) size.x, (int) size.y, 2);
      for (Textable textable : textables) {
        textable.precomputeModels();
      }
    }
    if (uiElements.size() > 0 && fbo == null) {
      Vector2f size = properties.get(Properties.SIZE, Vector2f.class);
      fbo = new FramebufferObject((int) size.x, (int) size.y, 2);
    }
    onPropertyChange(property, value);
  }

  /**
   * Updates the UserInterface, this method is called once every update
   *
   * @implNote This method is called once every update, thus can be called multiple time per frame
   * @param elapsedTime the elapsed time since last update in seconds
   */
  public abstract void update(double elapsedTime);

  /**
   * Called every time a {@link Properties} of the UIElement is changed
   *
   * @param property the changed {@link Properties}
   * @param value the new value
   */
  protected abstract void onPropertyChange(Properties property, Object value);
}
