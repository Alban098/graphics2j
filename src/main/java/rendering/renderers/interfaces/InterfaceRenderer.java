/*
 * Copyright (c) 2022, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering.renderers.interfaces;

import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;

import java.util.*;
import org.joml.Vector4f;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rendering.Texture;
import rendering.Window;
import rendering.data.VAO;
import rendering.interfaces.UIElement;
import rendering.interfaces.UserInterface;
import rendering.interfaces.element.Clickable;
import rendering.interfaces.element.Focusable;
import rendering.interfaces.element.Hoverable;
import rendering.renderers.Renderer;
import rendering.renderers.RenderingMode;
import rendering.scene.Camera;
import rendering.scene.Scene;
import rendering.shaders.ShaderAttribute;
import rendering.shaders.ShaderProgram;
import rendering.shaders.uniform.*;

public abstract class InterfaceRenderer implements Renderer<UserInterface> {

  private static final Logger LOGGER = LoggerFactory.getLogger(InterfaceRenderer.class);

  private final List<UserInterface> registered = new ArrayList<>();
  private final ShaderProgram backgroundShader;
  private final ShaderProgram elementShader;
  private final VAO backgroundVAO;
  private final VAO elementVAO;
  private Vector4f wireframeColor;

  public InterfaceRenderer(Vector4f wireframeColor) {
    this.wireframeColor = wireframeColor;
    this.backgroundShader =
        new ShaderProgram(
            "src/main/resources/shaders/interface/bg.vert",
            "src/main/resources/shaders/interface/bg.geom",
            "src/main/resources/shaders/interface/bg.frag",
            new ShaderAttribute[0],
            new Uniform[] {
              new UniformVec4("color", new Vector4f(0, 0, 0, 1f)),
              new UniformBoolean("textured", false),
            });
    this.elementShader =
        new ShaderProgram(
            "src/main/resources/shaders/interface/element/element.vert",
            "src/main/resources/shaders/interface/element/element.geom",
            "src/main/resources/shaders/interface/element/element.frag",
            new ShaderAttribute[0],
            new Uniform[] {
              new UniformFloat(Uniforms.TIME_MS.getName(), 0),
              new UniformVec4("color", new Vector4f(0, 0, 0, 1f)),
              new UniformVec4("clickTint", new Vector4f(0, 0, 0, 1f)),
              new UniformVec4("hoverTint", new Vector4f(0, 0, 0, 1f)),
              new UniformVec4("focusTint", new Vector4f(0, 0, 0, 1f)),
              new UniformBoolean("textured", false),
              new UniformBoolean("clicked", false),
              new UniformBoolean("hovered", false),
              new UniformBoolean("focused", false)
            });
    this.backgroundVAO = backgroundShader.createCompatibleVao(1);
    this.elementVAO = backgroundShader.createCompatibleVao(1);
  }

  @Override
  public void render(Window window, Camera camera, Scene scene, RenderingMode renderingMode) {
    for (UserInterface userInterface : registered) {
      backgroundShader.bind();
      if (userInterface.isTextured()) {
        glActiveTexture(GL_TEXTURE0);
        userInterface.getRenderable().getTexture().bind();
      }
      backgroundShader
          .getUniform(Uniforms.WIREFRAME, UniformBoolean.class)
          .loadBoolean(renderingMode == RenderingMode.WIREFRAME);
      backgroundShader
          .getUniform(Uniforms.WIREFRAME_COLOR, UniformVec4.class)
          .loadVec4(wireframeColor);
      backgroundShader.getUniform("color", UniformVec4.class).loadVec4(userInterface.getColor());
      backgroundShader
          .getUniform("textured", UniformBoolean.class)
          .loadBoolean(userInterface.isTextured());
      backgroundVAO.draw(userInterface);
      backgroundShader.unbind();

      for (UIElement element : userInterface.getElements().values()) {
        renderElement(element, userInterface, renderingMode);
      }
    }
  }

  private void renderElement(
      UIElement uiElement, UserInterface userInterface, RenderingMode renderingMode) {
    // render background
    elementShader.bind();
    if (uiElement.isTextured()) {
      glActiveTexture(GL_TEXTURE0);
      // bind texture
    }
    elementShader
        .getUniform(Uniforms.WIREFRAME, UniformBoolean.class)
        .loadBoolean(renderingMode == RenderingMode.WIREFRAME);
    elementShader.getUniform(Uniforms.WIREFRAME_COLOR, UniformVec4.class).loadVec4(wireframeColor);
    elementShader
        .getUniform(Uniforms.TIME_MS, UniformFloat.class)
        .loadFloat((float) GLFW.glfwGetTime());
    elementShader.getUniform("color", UniformVec4.class).loadVec4(uiElement.getColor());
    elementShader.getUniform("textured", UniformBoolean.class).loadBoolean(uiElement.isTextured());
    if (uiElement instanceof Clickable) {
      elementShader.getUniform("clickTint", UniformVec4.class).loadVec4(new Vector4f(1, 0, 0, 1));
      elementShader
          .getUniform("clicked", UniformBoolean.class)
          .loadBoolean(((Clickable) uiElement).isClicked());
    }
    if (uiElement instanceof Hoverable) {
      elementShader.getUniform("hoverTint", UniformVec4.class).loadVec4(new Vector4f(1, 0, 0, 1));
      elementShader
          .getUniform("hovered", UniformBoolean.class)
          .loadBoolean(((Hoverable) uiElement).isHovered());
    }
    if (uiElement instanceof Focusable) {
      elementShader.getUniform("focusTint", UniformVec4.class).loadVec4(new Vector4f(1, 0, 0, 1));
      elementShader
          .getUniform("focused", UniformBoolean.class)
          .loadBoolean(((Focusable) uiElement).isFocused());
    }

    elementVAO.draw(uiElement);
    backgroundShader.unbind();

    // render text
  }

  @Override
  public void cleanUp() {
    backgroundShader.cleanUp();
    backgroundVAO.cleanUp();
  }

  @Override
  public void register(UserInterface object) {
    registered.add(object);
  }

  @Override
  public void unregister(UserInterface object) {
    registered.remove(object);
  }

  @Override
  public Collection<Texture> getTextures() {
    return new ArrayList<>();
  }

  @Override
  public int getDrawCalls() {
    return 0;
  }

  @Override
  public int getNbObjects() {
    return 0;
  }

  @Override
  public void setWireframeColor(Vector4f wireframeColor) {
    this.wireframeColor = wireframeColor;
  }

  @Override
  public Vector4f getWireframeColor() {
    return wireframeColor;
  }
}
