/*
 * Copyright (c) 2022, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Renderer {

  private static final Logger LOGGER = LoggerFactory.getLogger(Renderer.class);

  private long windowPtr;

  public void init() {
    if (!glfwInit()) {
      LOGGER.error("Failed to initialize GLFW !");
      throw new IllegalStateException("Failed to initialize GLFW !");
    }

    glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);

    windowPtr = glfwCreateWindow(640, 480, "DemoApp", 0, 0);

    if (windowPtr == 0) {
      LOGGER.error("Failed to create window !");
      throw new IllegalStateException("Failed to create window !");
    }

    GLFWVidMode videoMode = glfwGetVideoMode(glfwGetPrimaryMonitor());
    if (videoMode == null) {
      LOGGER.error("Failed to get window mode !");
      throw new IllegalStateException("Failed to get window mode !");
    }
    glfwSetWindowPos(windowPtr, (videoMode.width() - 640) / 2, (videoMode.height() - 480) / 2);

    glfwShowWindow(windowPtr);
    glfwMakeContextCurrent(windowPtr);
    GL.createCapabilities();
    glEnable(GL_TEXTURE_2D);
  }

  public void run() {
    Quad quad = new Quad();
    Texture texture = ResourceLoader.loadTexture("src/main/resources/textures/texture.png");
    TexturedObject object = new TexturedObject(quad, texture);

    ShaderProgram shader =
        new ShaderProgram(
            "src/main/resources/shaders/vertex.glsl", "src/main/resources/shaders/fragment.glsl");

    if (texture == null) {
      throw new IllegalStateException("Texture is null !");
    }

    while (!glfwWindowShouldClose(windowPtr)) {
      glfwPollEvents();
      glClear(GL_COLOR_BUFFER_BIT);

      shader.bind();
      object.render();
      shader.unbind();

      glfwSwapBuffers(windowPtr);
    }
    object.cleanUp();
    glfwTerminate();
  }
}
