/*
 * Copyright (c) 2022, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

import java.util.ArrayList;
import java.util.List;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rendering.entities.Entity;
import rendering.entities.Renderable;
import rendering.entities.Transform;
import rendering.shaders.ShaderProgram;
import rendering.shaders.VertexAttribute;

public class Window {

  private static final Logger LOGGER = LoggerFactory.getLogger(Window.class);

  private long windowPtr;
  private EntityRenderer entityRenderer;

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
    glfwSetWindowPos(windowPtr, (videoMode.width() - 64) / 2, (videoMode.height() - 48) / 2);

    glfwShowWindow(windowPtr);
    glfwMakeContextCurrent(windowPtr);
    GL.createCapabilities();
    glEnable(GL_TEXTURE_2D);

    ShaderProgram shader =
        new ShaderProgram(
            "src/main/resources/shaders/vertex.glsl",
            "src/main/resources/shaders/fragment.glsl",
            new VertexAttribute(2, "color", GL_FLOAT, 3));
    entityRenderer = new EntityRenderer(shader);
  }

  public void run() {
    VertexAttribute colorAttrib = new VertexAttribute(2, "color", GL_FLOAT, 3);

    Texture texture = ResourceLoader.loadTexture("src/main/resources/textures/texture.png");
    Texture texture2 = ResourceLoader.loadTexture("src/main/resources/textures/texture2.png");

    List<Entity> entities = new ArrayList<>();
    for (int i = 0; i < 20000; i++) {
      Transform transform =
          new Transform(
              new Vector2f((float) (Math.random() * 2 - 1f), (float) (Math.random() * 2 - 1f)),
              (float) (Math.random() * 0.3f + 0.1f),
              (float) (Math.random() * 2 * Math.PI));
      Renderable renderable = new Renderable(Math.random() <= .5f ? texture : texture2);
      float color = (float) Math.random();
      renderable
          .getQuad()
          .setAttribute(
              colorAttrib,
              new Float[] {color, 1f, 1f, color, 1f, 1f, color, 1f, 1f, color, 1f, 1f});
      Entity entity = new Entity(transform, renderable);
      entities.add(entity);
    }

    entityRenderer.register(entities);

    double previousTime = glfwGetTime();
    long fps = 0;

    while (!glfwWindowShouldClose(windowPtr)) {
      double currentTime = glfwGetTime();
      fps++;
      if (currentTime - previousTime >= 1.0) {
        glfwSetWindowTitle(
            windowPtr, fps + " fps - " + entityRenderer.getFrameDrawCall() + " draw call(s)");

        fps = 0;
        previousTime = currentTime;
      }

      glfwPollEvents();
      glClear(GL_COLOR_BUFFER_BIT);
      for (Entity e : entities) {
        e.update();
      }

      entityRenderer.render();

      glfwSwapBuffers(windowPtr);
    }
    entities.forEach(Entity::cleanUp);
    entityRenderer.cleanUp();
    glfwTerminate();
  }
}
