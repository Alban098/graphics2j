/*
 * Copyright (c) 2022, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_MULTISAMPLE;
import static org.lwjgl.system.MemoryUtil.NULL;

import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Window {

  private static final Logger LOGGER = LoggerFactory.getLogger(Window.class);

  private final String title;

  private int width;
  private int height;
  private boolean resized;
  private long windowPtr;

  /**
   * Create a new Window
   *
   * @param title the Window title
   * @param width the Window width in pixels
   * @param height the Window height in pixels
   */
  public Window(String title, int width, int height) {
    this.title = title;
    this.width = width;
    this.height = height;
    this.resized = false;
  }

  public void init() {
    GLFWErrorCallback.createPrint(System.err).set();

    if (!glfwInit()) {
      LOGGER.error("Failed to initialize GLFW !");
      throw new IllegalStateException("Failed to initialize GLFW !");
    }

    glfwDefaultWindowHints(); // optional, the current window hints are already the default
    glfwWindowHint(GLFW_VISIBLE, GL_FALSE); // the window will stay hidden after creation
    glfwWindowHint(GLFW_RESIZABLE, GL_TRUE); // the window will be resizable
    glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
    glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2);
    glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
    glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE);
    glfwWindowHint(GLFW_SAMPLES, 4);

    windowPtr = glfwCreateWindow(width, height, title, NULL, NULL);

    if (windowPtr == 0) {
      LOGGER.error("Failed to create window !");
      throw new IllegalStateException("Failed to create window !");
    }

    // Setup resize callback
    glfwSetFramebufferSizeCallback(
        windowPtr,
        (window, width, height) -> {
          this.width = width;
          this.height = height;
          this.setResized(true);
        });

    // Set up a key callback. It will be called every time a key is pressed, repeated or released.
    glfwSetKeyCallback(
        windowPtr,
        (window, key, scancode, action, mods) -> {
          if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) {
            glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
          }
        });

    GLFWVidMode videoMode = glfwGetVideoMode(glfwGetPrimaryMonitor());
    if (videoMode == null) {
      LOGGER.error("Failed to get window mode !");
      throw new IllegalStateException("Failed to get window mode !");
    }
    glfwSetWindowPos(windowPtr, (videoMode.width() - width) / 2, (videoMode.height() - height) / 2);

    glfwMakeContextCurrent(windowPtr);

    glfwShowWindow(windowPtr);
    GL.createCapabilities();

    glEnable(GL_MULTISAMPLE); // Enabled Multisample

    // Set the clear color
    glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
  }

  /**
   * Return the Window id
   *
   * @return the Window id
   */
  public long getWindowPtr() {
    return windowPtr;
  }

  /**
   * Return whether a key is pressed in the Window of not
   *
   * @param keyCode the key to test for
   * @return is the Key pressed or not in the Window context
   */
  public boolean isKeyPressed(int keyCode) {
    return glfwGetKey(windowPtr, keyCode) == GLFW_PRESS;
  }

  /**
   * Return whether the window should be closed or not
   *
   * @return Should the window be closed or not
   */
  public boolean windowShouldClose() {
    return glfwWindowShouldClose(windowPtr);
  }

  /**
   * Return the Window width in pixels
   *
   * @return the Window width in pixels
   */
  public int getWidth() {
    return width;
  }

  /**
   * Return the Window height in pixels
   *
   * @return the Window height in pixels
   */
  public int getHeight() {
    return height;
  }

  /**
   * Return whether the Window has been resized or not
   *
   * @return Has the Window been resized or not
   */
  public boolean isResized() {
    return resized;
  }

  /**
   * Notify that the Window has been resized or not
   *
   * @param resized the resized flag
   */
  public void setResized(boolean resized) {
    this.resized = resized;
  }

  /** Update the Window by swapping the buffers */
  public void update() {
    glfwSwapBuffers(windowPtr);
    glfwPollEvents();
  }

  public float getAspectRatio() {
    return (float) width / height;
  }
}
