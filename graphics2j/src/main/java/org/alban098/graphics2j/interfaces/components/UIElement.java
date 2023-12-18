/*
 * Copyright (c) 2022-2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package org.alban098.graphics2j.interfaces.components;

import java.util.ArrayList;
import java.util.Collection;
import java.util.TreeMap;
import java.util.function.Consumer;
import org.alban098.common.MemoryManager;
import org.alban098.common.Transform;
import org.alban098.graphics2j.common.components.RenderElement;
import org.alban098.graphics2j.common.shaders.data.FramebufferObject;
import org.alban098.graphics2j.common.shaders.data.Texture;
import org.alban098.graphics2j.common.shaders.data.model.Models;
import org.alban098.graphics2j.input.MouseState;
import org.alban098.graphics2j.interfaces.UIRenderable;
import org.alban098.graphics2j.interfaces.components.property.Properties;
import org.alban098.graphics2j.interfaces.components.property.RenderingProperties;
import org.alban098.graphics2j.interfaces.components.text.Textable;
import org.alban098.graphics2j.interfaces.renderers.InterfaceRenderer;
import org.alban098.graphics2j.interfaces.windows.Modal;
import org.alban098.graphics2j.interfaces.windows.UserInterface;
import org.joml.Vector2f;

/**
 * Represents an abstract UIElement that can be placed in a {@link UserInterface} or another
 * UIElement and me be interacted with depending on concrete implementation
 */
public abstract class UIElement implements UIRenderable {

  /** A Map of all the direct children of this element */
  private final TreeMap<String, UIElement> uiElements;
  /** A Collection of all direct children that are {@link Textable} */
  private final Collection<Textable> textables;
  /** The {@link RenderElement} used to render this element to the screen */
  private final RenderElement renderable;
  /** The {@link Transform} used to place this element in its container */
  private final Transform transform;
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
  private Consumer<MouseState> onClickEnd = (input) -> {};
  /**
   * A callback to call when element start being clicked, only relevant if concrete implementation
   * implements {@link Clickable}
   */
  private Consumer<MouseState> onClickStart = (input) -> {};
  /**
   * A callback to call when element is being clicked, only relevant if concrete implementation
   * implements {@link Clickable}
   */
  private Consumer<MouseState> onHold = (input) -> {};
  /**
   * A callback to call when element start being hovered, only relevant if concrete implementation
   * implements {@link Hoverable}
   */
  private Consumer<MouseState> onEnter = (input) -> {};
  /**
   * A callback to call when element stop being hovered, only relevant if concrete implementation
   * implements {@link Hoverable}
   */
  private Consumer<MouseState> onExit = (input) -> {};
  /**
   * A callback to call when element is being hovered, only relevant if concrete implementation
   * implements {@link Hoverable}
   */
  private Consumer<MouseState> onInside = (input) -> {};

  /** Creates a new UIElement and create the necessary element for rendering */
  public UIElement() {
    this.renderable = new RenderElement(Models.POINT);
    this.transform = new Transform();
    this.properties = new RenderingProperties(this::broadcastPropertyChanged);
    this.uiElements = new TreeMap<>();
    this.textables = new ArrayList<>();
  }

  /**
   * Returns the {@link RenderElement} used to render the UIElement
   *
   * @return the {@link RenderElement} used to render the UIElement
   */
  public final RenderElement getRenderable() {
    renderable.setTexture(properties.get(Properties.BACKGROUND_TEXTURE, Texture.class));
    return renderable;
  }

  /**
   * Returns the {@link Transform} used to place the UIElement
   *
   * @return the {@link Transform} used to place the UIElement
   */
  public final Transform getTransform() {
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
   * <p>/!\ Do not override, used by the {@link InterfaceRenderer} /!\
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
      if (size.x != 0 && size.y != 0) {
        fbo = new FramebufferObject((int) size.x, (int) size.y, 1);
      }
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
   * Return whether the UserInterface's background is a {@link Texture} or not
   *
   * @return is the UserInterface's background a {@link Texture} or not
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
  public void onClickEnd(Consumer<MouseState> callback) {
    this.onClickEnd = callback;
  }

  /**
   * Sets the callback to call when the UIElement start being clicked, only relevant if the concrete
   * implementation implements {@link Clickable}
   *
   * @param callback the callback to set
   */
  public void onClickStart(Consumer<MouseState> callback) {
    this.onClickStart = callback;
  }

  /**
   * Sets the callback to call when the UIElement is being clicked, only relevant if the concrete
   * implementation implements {@link Clickable}
   *
   * @param callback the callback to set
   */
  public void onHold(Consumer<MouseState> callback) {
    this.onHold = callback;
  }

  /**
   * Applies the onClickEnd callback if defined
   *
   * @param input the input to process
   */
  public void onClickEnd(MouseState input) {
    if (onClickEnd != null) {
      onClickEnd.accept(input);
    }
  }

  /**
   * Applies the onClickStart callback if defined
   *
   * @param input the input to process
   */
  public void onClickStart(MouseState input) {
    if (onClickStart != null) {
      onClickStart.accept(input);
    }
  }

  /**
   * Applies the onHold callback if defined
   *
   * @param input the input to process
   */
  public void onHold(MouseState input) {
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
  public void onEnter(Consumer<MouseState> callback) {
    this.onEnter = callback;
  }

  /**
   * Sets the callback to call when the UIElement stop being hovered, only relevant if the concrete
   * implementation implements {@link Hoverable}
   *
   * @param callback the callback to set
   */
  public void onExit(Consumer<MouseState> callback) {
    this.onExit = callback;
  }

  /**
   * Sets the callback to call when the UIElement is being hovered, only relevant if the concrete
   * implementation implements {@link Hoverable}
   *
   * @param callback the callback to set
   */
  public void onInside(Consumer<MouseState> callback) {
    this.onInside = callback;
  }

  /**
   * Applies the onEnter callback if defined, also displays the {@link Modal} if defined
   *
   * @param input the input to process
   */
  public void onEnter(MouseState input) {
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
  public void onExit(MouseState input) {
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
  public void onInside(MouseState input) {
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

  /**
   * Processes user inputs recursively, by firstly propagating them to the UIElement's children,
   * then calling {@link UIElement#update(double)}
   *
   * @param input a wrapper for the current state of user inputs
   * @return true if this UIElement or one of its children has caught the input, false otherwise
   */
  public final boolean propagateInput(MouseState input) {
    for (String key : uiElements.descendingKeySet()) {
      UIElement element = uiElements.get(key);
      if (element.propagateInput(input)) {
        return true;
      }
    }
    return input(input);
  }

  /**
   * Updates the {@link Transform} to reflect the current position and size of the UIElement in its
   * container
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
    transform.commit();
  }

  /**
   * Return whether a point is inside of this UIElement or not, being on the edge is considered
   * being inside
   *
   * <p>! Being on the edge must be considered being inside
   *
   * <p>! Must be overridden if not a Rectangle
   *
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
  private boolean input(MouseState input) {
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
      Vector2f size = (Vector2f) value;
      if (size.x != 0 && size.y != 0) {
        MemoryManager.free(fbo);
        fbo = new FramebufferObject((int) size.x, (int) size.y, 1);
      }
      for (Textable textable : textables) {
        textable.precomputeModels();
      }
    }
    if (uiElements.size() > 0 && fbo == null) {
      Vector2f size = properties.get(Properties.SIZE, Vector2f.class);
      if (size.x != 0 && size.y != 0) {
        fbo = new FramebufferObject((int) size.x, (int) size.y, 1);
      }
    }
    onPropertyChange(property, value);
  }

  /**
   * Updates the UserInterface
   *
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

  /**
   * Returns a display name for the UIRenderable
   *
   * @return a display name for the UIRenderable
   */
  @Override
  public String getName() {
    return "UIElement";
  }

  /**
   * Returns the numbers of elements inside this Interface, recursively
   *
   * @return the numbers of elements inside this Interface, recursively
   */
  public int getNbElements() {
    int nbElements = uiElements.size();
    for (UIElement e : uiElements.values()) {
      nbElements += e.getNbElements();
    }
    return nbElements;
  }
}
