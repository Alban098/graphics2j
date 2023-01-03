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
public class MouseInput {

  private final Vector2f previousPos;
  private final Vector2f currentPos;
  private final Vector2f displacementVector;

  private float previousScroll = 0;
  private float scrollOffset = 0;
  private boolean inWindow = false;
  private boolean leftButtonPressed = false;
  private boolean rightButtonPressed = false;
  private GLFWCursorPosCallback cursorPosCallback;
  private GLFWCursorEnterCallback cursorEnterCallback;
  private GLFWMouseButtonCallback mouseButtonCallback;
  private GLFWScrollCallback scrollCallback;

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

  public boolean isInWindow() {
    return inWindow;
  }

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

  public void cleanUp() {
    if (cursorPosCallback != null) {
      cursorPosCallback.close();
    }
    cursorEnterCallback.close();
    mouseButtonCallback.close();
    scrollCallback.close();
  }

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

  public void halt(Object holder) {
    this.holder = holder;
  }

  public void release() {
    holder = null;
  }

  public boolean hasControl(Object holder) {
    return holder.equals(this.holder);
  }
}
