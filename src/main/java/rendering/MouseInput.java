/*
 * Copyright (c) 2022-2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering;

import static org.lwjgl.glfw.GLFW.*;

import org.joml.Vector2f;
import org.lwjgl.glfw.GLFWCursorEnterCallback;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.lwjgl.glfw.GLFWScrollCallback;
import rendering.interfaces.UserInterface;
import rendering.interfaces.element.UIElement;

/** This class represent a state of the mouse */
public final class MouseInput {

  /** The position of the cursor at the last update inside the window */
  private final Vector2f previousPos;
  /** The current position of the cursor inside the Window */
  private final Vector2f currentPos;
  /** The displacement of the cursor since the last update */
  private final Vector2f displacementVector;
  /** The value of the scroll wheel at the last update */
  private float previousScroll = 0;
  /** The scroll offset since the last update */
  private float scrollOffset = 0;
  /** Is the cursor in the window */
  private boolean inWindow = false;
  /** Is the left mouse button clicked */
  private boolean leftButtonPressed = false;
  /** Is the right mouse button clicked */
  private boolean rightButtonPressed = false;
  /** Callback for when the mouse position is changed */
  private GLFWCursorPosCallback cursorPosCallback;
  /** Callback for when the mouse inters the screen */
  private GLFWCursorEnterCallback cursorEnterCallback;
  /** Callback for when a button is clicked on the mouse */
  private GLFWMouseButtonCallback mouseButtonCallback;
  /** Callback for when the scroll of the mouse is changed */
  private GLFWScrollCallback scrollCallback;
  /**
   * A reference to an Object holding the input, used to block further interaction while one is
   * currently appending
   */
  private Object holder = null;

  /** Create a new MouseInput */
  public MouseInput() {
    previousPos = new Vector2f(-1, -1);
    currentPos = new Vector2f(0, 0);
    displacementVector = new Vector2f();
  }

  /**
   * Initialize the MouseInput
   *
   * @param window the window used to capture inputs from
   */
  public void linkCallbacks(Window window) {
    cursorPosCallback =
        glfwSetCursorPosCallback(
            window.getWindowPtr(),
            (windowHandle, xPos, yPos) -> {
              currentPos.x = (float) xPos;
              currentPos.y = (float) yPos;
            });
    cursorEnterCallback =
        glfwSetCursorEnterCallback(
            window.getWindowPtr(), (windowHandle, entered) -> inWindow = entered);
    mouseButtonCallback =
        glfwSetMouseButtonCallback(
            window.getWindowPtr(),
            (windowHandle, button, action, mode) -> {
              leftButtonPressed = button == GLFW_MOUSE_BUTTON_1 && action == GLFW_PRESS;
              rightButtonPressed = button == GLFW_MOUSE_BUTTON_2 && action == GLFW_PRESS;
            });
    scrollCallback =
        glfwSetScrollCallback(
            window.getWindowPtr(), (windowHandle, unused, offset) -> scrollOffset = (float) offset);
  }

  /**
   * get the displacement Vector of the mouse cursor
   *
   * @return the displacement vector of the mouse
   */
  public Vector2f getDisplacementVector() {
    return new Vector2f(displacementVector);
  }

  /**
   * Returns the current scroll offset since the last updated
   *
   * @return the current scroll offset since the last updated
   */
  public float getScrollOffset() {
    return scrollOffset;
  }

  /** Compute the current state of the mouse */
  public void update() {
    if (previousScroll == scrollOffset) {
      scrollOffset = 0;
    }
    previousScroll = scrollOffset;

    displacementVector.x = 0;
    displacementVector.y = 0;
    if (previousPos.x > 0 && previousPos.y > 0 && inWindow) {
      displacementVector.x = currentPos.x - previousPos.x;
      displacementVector.y = currentPos.y - previousPos.y;
    }
    previousPos.x = currentPos.x;
    previousPos.y = currentPos.y;
  }

  /**
   * Return whether the cursor in inside the window or not
   *
   * @return whether the cursor in inside the window or not
   */
  public boolean isInWindow() {
    return inWindow;
  }

  /**
   * Returns the current position of the cursor inside the window in pixels
   *
   * @return the current position of the cursor inside the window in pixels
   */
  public Vector2f getCurrentPos() {
    return new Vector2f(currentPos);
  }

  /**
   * Is left click held
   *
   * @return is left click help
   */
  public boolean isLeftButtonPressed() {
    return leftButtonPressed;
  }

  /**
   * Is right click held
   *
   * @return is right click help
   */
  public boolean isRightButtonPressed() {
    return rightButtonPressed;
  }

  /** Clear the input state */
  public void cleanUp() {
    if (cursorPosCallback != null) {
      cursorPosCallback.close();
    }
    cursorEnterCallback.close();
    mouseButtonCallback.close();
    scrollCallback.close();
  }

  /**
   * Returns whether an Object can hold interactions or not
   *
   * @param sender the Object asking for halting interactions
   * @return can the sender halt further interaction
   */
  public boolean canTakeControl(Object sender) {
    if (sender instanceof UIElement
        && (holder instanceof UIElement || holder instanceof UserInterface)) {
      do {
        if (sender.equals(holder)) {
          return true;
        }
        sender = ((UIElement) sender).getParent();
      } while (sender != null);
    }
    return holder == null || holder.equals(sender);
  }

  /**
   * Halts all further interaction not coming from the halter until released
   *
   * @param holder the Object halting interactions
   */
  public void halt(Object holder) {
    this.holder = holder;
  }

  /** Release the input to allow further interactions */
  public void release() {
    holder = null;
  }

  /**
   * Returns whether an Object is currently halting interactions
   *
   * @param holder the Object to check
   * @return whether the Object is currently halting interactions
   */
  public boolean hasControl(Object holder) {
    return holder.equals(this.holder);
  }
}
