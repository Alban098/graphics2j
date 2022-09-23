/*
 * Copyright (c) 2022, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering;

import static org.lwjgl.glfw.GLFW.*;

import org.joml.Vector2d;
import org.joml.Vector2f;

/** This class represent a state of the mouse */
public class MouseInput {

  private final Vector2d previousPos;
  private final Vector2d currentPos;
  private final Vector2f displVec;

  private boolean inWindow = false;
  private boolean leftButtonPressed = false;
  private boolean rightButtonPressed = false;

  /** Create a new MouseInput */
  public MouseInput() {
    previousPos = new Vector2d(-1, -1);
    currentPos = new Vector2d(0, 0);
    displVec = new Vector2f();
  }

  /**
   * Initialize the MouseInput
   *
   * @param window the window used to capture inputs from
   */
  public void init(Window window) {
    glfwSetCursorPosCallback(
        window.getWindowPtr(),
        (windowHandle, xpos, ypos) -> {
          currentPos.x = xpos;
          currentPos.y = ypos;
        });
    glfwSetCursorEnterCallback(
        window.getWindowPtr(),
        (windowHandle, entered) -> {
          inWindow = entered;
        });
    glfwSetMouseButtonCallback(
        window.getWindowPtr(),
        (windowHandle, button, action, mode) -> {
          leftButtonPressed = button == GLFW_MOUSE_BUTTON_1 && action == GLFW_PRESS;
          rightButtonPressed = button == GLFW_MOUSE_BUTTON_2 && action == GLFW_PRESS;
        });
  }

  /**
   * get the displacement Vector of the mouse cursor
   *
   * @return the displacement vector of the mouse
   */
  public Vector2f getDisplVec() {
    return displVec;
  }

  /**
   * Compute the current state of the mouse
   *
   * @param window the Window to capture inputs from
   */
  public void input(Window window) {
    displVec.x = 0;
    displVec.y = 0;
    if (previousPos.x > 0 && previousPos.y > 0 && inWindow) {
      double deltax = currentPos.x - previousPos.x;
      double deltay = currentPos.y - previousPos.y;
      boolean rotateX = deltax != 0;
      boolean rotateY = deltay != 0;
      if (rotateX) {
        displVec.y = (float) deltax;
      }
      if (rotateY) {
        displVec.x = (float) deltay;
      }
    }
    previousPos.x = currentPos.x;
    previousPos.y = currentPos.y;
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
}
