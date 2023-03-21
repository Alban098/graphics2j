/*
 * Copyright (c) 2022-2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package org.alban098.graphics2j.common;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_MULTISAMPLE;
import static org.lwjgl.system.MemoryUtil.NULL;

import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.extension.implot.ImPlot;
import imgui.extension.implot.ImPlotContext;
import imgui.flag.ImGuiConfigFlags;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import imgui.internal.ImGuiContext;
import org.alban098.graphics2j.debug.DebugInterface;
import org.alban098.graphics2j.debug.DebugTab;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWFramebufferSizeCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents the main GLFW Windows implementation, and will handle everything related to window
 * interaction, window state and high level rendering
 */
public final class Window implements Cleanable {

  /** Just a Logger to log events */
  private static final Logger LOGGER = LoggerFactory.getLogger(Window.class);

  /** The title of the Window */
  private final String title;
  /** The GLFW implementation used by ImGui */
  private final ImGuiImplGlfw imguiGlfw = new ImGuiImplGlfw();
  /** The OpenGL 3+ implementation used by ImGui */
  private final ImGuiImplGl3 imguiGl3 = new ImGuiImplGl3();
  /** Returns whether ImGui is activated or not */
  private final boolean imGuiActivated;
  /** The context of ImPlot used to by ImGui */
  private ImPlotContext plotCtx;
  /** The main ImGui context */
  private ImGuiContext imGuiCtx;
  /** The current width of the screen in pixels */
  private int width;
  /** The current height of the screen in pixels */
  private int height;
  /** A flag indicating that the window has been resized since last draw */
  private boolean resized;
  /** The pointer of the GLFW window */
  private long windowPtr;
  /** The callback for handling resize events */
  private GLFWFramebufferSizeCallback sizeCallback;
  /** The implementation of the Interface used to display debug information */
  private final DebugInterface debugInterface;
  /** The total tile passed computing the last frame in nanoseconds */
  private long frametime = 0;
  /** The time elapsed since the last frame has finished being computed in nanoseconds */
  private long timeSinceLastFrame = 0;
  /** The time at which the current frame has started in nanoseconds */
  private long frameStartTimeNs = 0;
  /** The time at which the last frame has finished being computed in nanoseconds */
  private long lastFrameTimeEnd = 0;

  /**
   * Create a new Window
   *
   * @param title the Window title
   * @param width the Window width in pixels
   * @param height the Window height in pixels
   */
  public Window(String title, int width, int height, boolean imGuiCapability) {
    this.title = title;
    this.width = width;
    this.height = height;
    this.resized = false;
    this.imGuiActivated = imGuiCapability;
    this.debugInterface = new DebugInterface("Debugger");
    this.init();
  }

  /** Initialize the Window, sets up the OpenGL and GLFW contexts and sets the inputs callbacks */
  private void init() {
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
    sizeCallback =
        glfwSetFramebufferSizeCallback(
            windowPtr,
            (window, width, height) -> {
              this.width = width;
              this.height = height;
              this.setResized(true);
              glViewport(0, 0, width, height);
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
    glEnable(GL_BLEND);
    glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

    // Set the clear color
    if (imGuiActivated) {
      imGuiCtx = ImGui.createContext();
      plotCtx = ImPlot.createContext();
      ImGuiIO io = ImGui.getIO();
      io.addConfigFlags(ImGuiConfigFlags.ViewportsEnable);
      imguiGlfw.init(windowPtr, true);
      imguiGl3.init(null);
    }
    glClearColor(.2f, .2f, .2f, 1f);
  }

  /** Clear the Window from VRAM */
  @Override
  public void cleanUp() {
    if (sizeCallback != null) {
      sizeCallback.close();
    }
    if (imGuiActivated) {
      ImPlot.destroyContext(plotCtx);
      ImGui.destroyContext(imGuiCtx);
    }
    GL.destroy();
    glfwDestroyWindow(windowPtr);
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

  /** Initialize the window to draw a new frame */
  public void newFrame() {
    glClear(GL_COLOR_BUFFER_BIT);
    if (imGuiActivated) {
      imguiGlfw.newFrame();
      ImGui.newFrame();
    }
    frameStartTimeNs = System.nanoTime();
  }

  /** Process the frame to draw it to the screen */
  public void endFrame() {
    if (imGuiActivated) {
      frameStartTimeNs = System.nanoTime();

      if (debugInterface.isVisible()) {
        debugInterface.render();
      }

      ImGui.render();
      imguiGl3.renderDrawData(ImGui.getDrawData());

      // ImGui and GLFW standard call to render the frame
      if (ImGui.getIO().hasConfigFlags(ImGuiConfigFlags.ViewportsEnable)) {
        final long backupWindowPtr = org.lwjgl.glfw.GLFW.glfwGetCurrentContext();
        ImGui.updatePlatformWindows();
        ImGui.renderPlatformWindowsDefault();
        glfwMakeContextCurrent(backupWindowPtr);
      }
    }
    glfwSwapBuffers(windowPtr);
    glfwPollEvents();

    frametime = System.nanoTime() - frameStartTimeNs;
    timeSinceLastFrame = System.nanoTime() - lastFrameTimeEnd;
    lastFrameTimeEnd = System.nanoTime();
  }

  /**
   * Returns the current aspect ratio of the viewport
   *
   * @return the current aspect ratio of the viewport
   */
  public float getAspectRatio() {
    return (float) width / height;
  }

  /**
   * Returns the frametime of the last completed frame in seconds
   *
   * @return the frametime of the last completed frame in seconds
   */
  public double getFrametime() {
    return frametime / 1_000_000_000.0;
  }

  /**
   * Returns the time elapsed since the last frame has finished being computed in seconds
   *
   * @return the time elapsed since the last frame has finished being computed in seconds
   */
  public double getTimeSinceLastFrame() {
    return timeSinceLastFrame / 1_000_000_000.0;
  }

  /**
   * Adds a new {@link DebugTab} to the {@link DebugInterface} of the Window
   *
   * @param tab the {@link DebugTab} to add
   */
  public void addDebugInterface(DebugTab tab) {
    debugInterface.addTab(tab);
  }
}
