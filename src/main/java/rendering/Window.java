/*
 * Copyright (c) 2022-2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering;

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
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWFramebufferSizeCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Window {

  private static final Logger LOGGER = LoggerFactory.getLogger(Window.class);

  /** The title of the Window */
  private final String title;
  /** The GLFW implementation used by ImGui */
  private final ImGuiImplGlfw imguiGlfw = new ImGuiImplGlfw();
  /** The OpenGL 3+ implementation used by ImGui */
  private final ImGuiImplGl3 imguiGl3 = new ImGuiImplGl3();
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

  /** Initialize the Window, sets up the OpenGL and GLFW contexts and sets the inputs callbacks */
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

    imGuiCtx = ImGui.createContext();
    plotCtx = ImPlot.createContext();
    ImGuiIO io = ImGui.getIO();
    io.addConfigFlags(ImGuiConfigFlags.ViewportsEnable);
    imguiGlfw.init(windowPtr, true);
    imguiGl3.init(null);
    glClearColor(.2f, .2f, .2f, 1f);
  }

  /** Clear the Window from VRAM */
  public void cleanUp() {
    if (sizeCallback != null) {
      sizeCallback.close();
    }
    ImPlot.destroyContext(plotCtx);
    ImGui.destroyContext(imGuiCtx);
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
    imguiGlfw.newFrame();
    ImGui.newFrame();
  }

  /** Process the frame to draw it to the screen */
  public void endFrame() {
    ImGui.render();
    imguiGl3.renderDrawData(ImGui.getDrawData());

    // ImGui and GLFW standard call to render the frame
    if (ImGui.getIO().hasConfigFlags(ImGuiConfigFlags.ViewportsEnable)) {
      final long backupWindowPtr = org.lwjgl.glfw.GLFW.glfwGetCurrentContext();
      ImGui.updatePlatformWindows();
      ImGui.renderPlatformWindowsDefault();
      glfwMakeContextCurrent(backupWindowPtr);
    }

    glfwSwapBuffers(windowPtr);
    glfwPollEvents();
  }

  /**
   * Returns the current aspect ratio of the viewport
   *
   * @return the current aspect ratio of the viewport
   */
  public float getAspectRatio() {
    return (float) width / height;
  }
}
