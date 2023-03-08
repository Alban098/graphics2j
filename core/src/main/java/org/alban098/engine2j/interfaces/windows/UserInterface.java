/*
 * Copyright (c) 2022-2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package org.alban098.engine2j.interfaces.windows;

import java.util.Collection;
import java.util.TreeMap;
import org.alban098.engine2j.common.Renderable;
import org.alban098.engine2j.common.Window;
import org.alban098.engine2j.common.components.RenderElement;
import org.alban098.engine2j.common.components.Transform;
import org.alban098.engine2j.common.shaders.data.FramebufferObject;
import org.alban098.engine2j.common.shaders.data.Texture;
import org.alban098.engine2j.input.MouseInputManager;
import org.alban098.engine2j.interfaces.InterfaceRenderingManager;
import org.alban098.engine2j.interfaces.components.UIElement;
import org.alban098.engine2j.interfaces.components.property.Properties;
import org.alban098.engine2j.interfaces.components.property.RenderingProperties;
import org.alban098.engine2j.interfaces.components.text.Textable;
import org.alban098.engine2j.interfaces.renderers.InterfaceRenderer;
import org.joml.Vector2f;

/**
 * This class represent a base User Interface, all GUIs must be derived from this class a {@link
 * UserInterface} is simply a container for a tree of {@link UIElement} and is responsible for
 * displaying them recursively to the screen and propagate the user inputs to them
 */
public abstract class UserInterface implements Renderable {

  /** The {@link Window} containing the UserInterface */
  private final Window window;
  /** The {@link RenderElement} used to render the rendered UserInterface to the screen */
  private final RenderElement renderable;
  /** The {@link Transform} used to place the UserInterface to the screen */
  private final Transform transform;
  /** The {@link RenderingProperties} of this UserInterface, not recursive for now */
  private final RenderingProperties properties;
  /** a Tree of {@link UIElement} that are direct children of this UserInterface */
  private final TreeMap<String, UIElement> uiElements = new TreeMap<>();
  /** The {@link FramebufferObject} where this UserInterface is rendered */
  private FramebufferObject fbo;
  /** Is the UserInterface currently visible on screen */
  private boolean active = false;
  /** The {@link InterfaceRenderingManager} managing this UserInterface */
  protected InterfaceRenderingManager manager;
  /** The title of the UserInterface */
  protected final String name;

  /**
   * Creates a new UserInterface contained in a {@link Window}, with a name and managed by an {@link
   * InterfaceRenderingManager}
   *
   * @param window the {@link Window} containing this UserInterface
   * @param name the name of this UserInterface
   */
  public UserInterface(Window window, String name) {
    this.name = name;
    this.window = window;
    this.renderable = new RenderElement();
    this.transform = new Transform();
    this.properties = new RenderingProperties(this::broadcastPropertyChanged);
  }

  /**
   * Returns the {@link RenderElement} used to render the UserInterface
   *
   * @return the {@link RenderElement} used to render the UserInterface
   */
  @Override
  public final RenderElement getRenderable() {
    return renderable;
  }

  /**
   * Returns the {@link Transform} used to render the UserInterface
   *
   * @return the {@link Transform} used to render the UserInterface
   */
  @Override
  public final Transform getTransform() {
    // update the transform before returning it
    updateTransform();
    return transform;
  }

  /**
   * Returns the name of this UserInterface
   *
   * @return the name of this UserInterface
   */
  public final String getName() {
    return name;
  }

  /**
   * Returns the {@link Window} containing this UserInterface
   *
   * @return the {@link Window} containing this UserInterface
   */
  public final Window getWindow() {
    return window;
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
  public UIElement getElement(String identifier) {
    return uiElements.get(identifier);
  }

  /**
   * Adds a new child to this UserInterface
   *
   * @param identifier the identifier of the new {@link UIElement}
   * @param element the {@link UIElement} to add
   */
  public void addElement(String identifier, UIElement element) {
    element.setContainer(this);
    element.setParent(null);
    uiElements.put(identifier, element);
    if (fbo == null) {
      Vector2f size = properties.get(Properties.SIZE, Vector2f.class);
      if (size.x != 0 && size.y != 0) {
        fbo = new FramebufferObject((int) size.x, (int) size.y, 1);
      }
    }
    if (element instanceof Textable) ((Textable) element).precomputeModels();
  }

  /**
   * Return the {@link FramebufferObject} the UserInterface is rendered into
   *
   * @return the {@link FramebufferObject} the UserInterface is rendered into
   */
  public final FramebufferObject getFbo() {
    return fbo;
  }

  /**
   * Returns the {@link RenderingProperties} of the UserInterface, can be called to edit them
   *
   * @return the {@link RenderingProperties} of the UserInterface
   */
  public final RenderingProperties getProperties() {
    return properties;
  }

  /**
   * Returns the {@link InterfaceRenderingManager} in charge of this User Interface
   *
   * @return the {@link InterfaceRenderingManager} in charge of this User Interface
   */
  public InterfaceRenderingManager getManager() {
    return manager;
  }

  /**
   * Sets the new {@link InterfaceRenderingManager} in charge of this User Interface
   *
   * @param manager the new {@link InterfaceRenderingManager} in charge of this User Interface
   */
  public void setManager(InterfaceRenderingManager manager) {
    this.manager = manager;
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
   * Returns whether the UserInterface is visible or not
   *
   * @return is the UserInterface visible or not
   */
  public final boolean isVisible() {
    return active;
  }

  /**
   * Changes the visibility of the UserInterface
   *
   * <p>/!\ Do not use this method to directly change the visibility of a window, prefer {@link
   * InterfaceRenderingManager#showInterface(UserInterface)} and {@link
   * InterfaceRenderingManager#hideInterface(UserInterface)}
   *
   * @param visible the new visibility of the UserInterface
   */
  public final void setVisible(boolean visible) {
    this.active = visible;
  }

  /**
   * Updates the UserInterface and all its direct children
   *
   * @param elapsedTime the time elapsed since last call to update in seconds
   */
  public final void updateInternal(double elapsedTime) {
    // update children before updating the UI
    uiElements.forEach((k, v) -> v.updateInternal(elapsedTime));
    update(elapsedTime);
  }

  /**
   * Clears the UserInterface and all its children recursively
   *
   * <p>/!\ call this {@link UserInterface#cleanUp} base method when overriding
   */
  public void cleanUp() {
    renderable.cleanUp();
    transform.cleanUp();
    uiElements.forEach((k, v) -> v.cleanUp());
    uiElements.clear();
  }

  /**
   * Processes user inputs recursively, by propagating them to the UserInterface's children, also
   * lock the camera panning if necessary
   *
   * @param input a wrapper for the current state of user inputs
   * @return true if this interface or one of its children has caught the input, false otherwise
   */
  public final boolean propagateInput(MouseInputManager input) {
    boolean caught = false;
    boolean inside = isInside(input.getCurrentPos());
    for (String key : uiElements.descendingKeySet()) {
      UIElement element = uiElements.get(key);
      if (element.propagateInput(input)) {
        caught = true;
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
    return caught;
  }

  /** Updates the {@link Transform} to reflect the current position and size of the UserInterface */
  private void updateTransform() {
    Vector2f size = properties.get(Properties.SIZE, Vector2f.class);
    Vector2f position = properties.get(Properties.POSITION, Vector2f.class);

    // transform the size from pixels space to OpenGL coordinate system
    float width = 2f * size.x / window.getWidth();
    float height = 2f * size.y / window.getHeight();
    transform.setScale(width, height);

    // set the position of the UserInterface in OpenGL coordinate system
    transform.setDisplacement(
        2f * position.x / window.getWidth() - 1 + width / 2f,
        2f * -position.y / window.getHeight() + 1 - height / 2f);

    // apply those transformations to the component
    transform.update();
  }

  /**
   * Called every time a {@link Properties} of the UserInterface is changed, resizes the FBO if
   * necessary and calls the standard {@link UserInterface#onPropertyChange(Properties, Object)}
   * routine
   *
   * @param property the changed {@link Properties}
   * @param value the new value
   */
  private void broadcastPropertyChanged(Properties property, Object value) {
    if (property == Properties.SIZE && fbo != null) {
      fbo.cleanUp();
      Vector2f size = (Vector2f) value;
      if (size.x != 0 && size.y != 0) {
        fbo = new FramebufferObject((int) size.x, (int) size.y, 1);
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
   * Return whether a point is inside of this UserInterface or not, being on the edge is considered
   * being inside
   *
   * @param pos the position of the point to check
   * @return true if the point is inside the UserInterface, false otherwise
   */
  private boolean isInside(Vector2f pos) {
    Vector2f topLeft = properties.get(Properties.POSITION, Vector2f.class);
    Vector2f size = properties.get(Properties.SIZE, Vector2f.class);
    return pos.x >= topLeft.x
        && pos.x <= topLeft.x + size.x
        && pos.y >= topLeft.y
        && pos.y <= topLeft.y + size.y;
  }

  /**
   * Updates the UserInterface, this method is called once every update.
   *
   * <p>/!\ This method is called once every update, thus can be called multiple time per frame
   *
   * @param elapsedTime the elapsed time since last update in seconds
   */
  protected abstract void update(double elapsedTime);

  /**
   * Called every time a {@link Properties} of the UserInterface is changed
   *
   * @param property the changed {@link Properties}
   * @param value the new value
   */
  protected abstract void onPropertyChange(Properties property, Object value);

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
