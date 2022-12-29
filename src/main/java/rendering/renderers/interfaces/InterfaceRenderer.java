/*
 * Copyright (c) 2022, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering.renderers.interfaces;

import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;

import java.util.*;
import org.joml.Vector2f;
import org.joml.Vector4f;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rendering.ILogic;
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
import rendering.shaders.ShaderAttribute;
import rendering.shaders.ShaderProgram;
import rendering.shaders.uniform.*;

public class InterfaceRenderer implements Renderer<UserInterface> {

  private static final Logger LOGGER = LoggerFactory.getLogger(InterfaceRenderer.class);
  private final Collection<UserInterface> registered = new HashSet<>();
  private final Collection<Texture> registeredTextures = new HashSet<>();
  private final ShaderProgram backgroundShader;
  private final ShaderProgram elementShader;
  private final VAO backgroundVAO;
  private final VAO elementVAO;
  private Vector4f wireframeColor;
  private int drawCalls = 0;

  public InterfaceRenderer(Vector4f wireframeColor) {
    this.wireframeColor = wireframeColor;
    this.backgroundShader =
        new ShaderProgram(
            "src/main/resources/shaders/interface/bg.vert",
            "src/main/resources/shaders/interface/bg.geom",
            "src/main/resources/shaders/interface/bg.frag",
            new ShaderAttribute[0],
            new Uniform[] {
              new UniformVec4(Uniforms.COLOR.getName(), new Vector4f(0, 0, 0, 1f)),
              new UniformBoolean(Uniforms.TEXTURED.getName(), false),
              new UniformVec4(Uniforms.RADIUS.getName(), new Vector4f(1, 1, 1, 1)),
              new UniformVec2(Uniforms.DIMENSION.getName(), new Vector2f()),
            });
    this.elementShader =
        new ShaderProgram(
            "src/main/resources/shaders/interface/element/element.vert",
            "src/main/resources/shaders/interface/element/element.geom",
            "src/main/resources/shaders/interface/element/element.frag",
            new ShaderAttribute[0],
            new Uniform[] {
              new UniformFloat(Uniforms.TIME_MS.getName(), 0),
              new UniformVec4(Uniforms.COLOR.getName(), new Vector4f(0, 0, 0, 1f)),
              new UniformBoolean(Uniforms.TEXTURED.getName(), false),
              new UniformBoolean(Uniforms.CLICKED.getName(), false),
              new UniformBoolean(Uniforms.HOVERED.getName(), false),
              new UniformBoolean(Uniforms.FOCUSED.getName(), false),
              new UniformVec4(Uniforms.RADIUS.getName(), new Vector4f(1, 1, 1, 1)),
              new UniformVec2(Uniforms.DIMENSION.getName(), new Vector2f()),
            });
    this.backgroundVAO = backgroundShader.createCompatibleVao(1);
    this.elementVAO = backgroundShader.createCompatibleVao(1);
  }

  @Override
  public void render(Window window, ILogic logic, RenderingMode renderingMode) {
    drawCalls = 0;
    for (UserInterface userInterface : registered) {
      backgroundShader.bind();
      if (userInterface.isTextured()) {
        glActiveTexture(GL_TEXTURE0);
        userInterface.getRenderable().getTexture().bind();
      }
      backgroundShader
          .getUniform(Uniforms.WIREFRAME, UniformBoolean.class)
          .load(renderingMode == RenderingMode.WIREFRAME);
      backgroundShader.getUniform(Uniforms.WIREFRAME_COLOR, UniformVec4.class).load(wireframeColor);
      backgroundShader.getUniform("color", UniformVec4.class).load(userInterface.getColor());
      backgroundShader
          .getUniform(Uniforms.TEXTURED, UniformBoolean.class)
          .load(userInterface.isTextured());
      backgroundShader
          .getUniform(Uniforms.RADIUS, UniformVec4.class)
          .load(userInterface.getCornerProperties().toVec4());
      backgroundShader
          .getUniform(Uniforms.DIMENSION, UniformVec2.class)
          .load(userInterface.getSize());
      backgroundVAO.draw(userInterface);
      backgroundShader.unbind();
      drawCalls++;

      for (UIElement<?> element : userInterface.getElements()) {
        renderElement(element, renderingMode);
      }

      for (UIElement<?> element : userInterface.getFixedElements()) {
        renderElement(element, renderingMode);
      }
    }
  }

  private void renderElement(UIElement<?> uiElement, RenderingMode renderingMode) {
    // render background
    elementShader.bind();
    if (uiElement.isTextured()) {
      glActiveTexture(GL_TEXTURE0);
      uiElement.getRenderable().getTexture().bind();
    }

    elementShader.getUniform(Uniforms.TIME_MS, UniformFloat.class).load((float) GLFW.glfwGetTime());
    elementShader.getUniform(Uniforms.COLOR, UniformVec4.class).load(uiElement.getColor());
    elementShader.getUniform(Uniforms.WIREFRAME_COLOR, UniformVec4.class).load(wireframeColor);
    elementShader
        .getUniform(Uniforms.RADIUS, UniformVec4.class)
        .load(uiElement.getCornerProperties().toVec4());
    elementShader.getUniform(Uniforms.DIMENSION, UniformVec2.class).load(uiElement.getSize());
    elementShader
        .getUniform(Uniforms.WIREFRAME, UniformBoolean.class)
        .load(renderingMode == RenderingMode.WIREFRAME);
    elementShader.getUniform(Uniforms.TEXTURED, UniformBoolean.class).load(uiElement.isTextured());
    elementShader
        .getUniform(Uniforms.CLICKED, UniformBoolean.class)
        .load(uiElement instanceof Clickable && ((Clickable) uiElement).isClicked());
    elementShader
        .getUniform(Uniforms.HOVERED, UniformBoolean.class)
        .load(uiElement instanceof Hoverable && ((Hoverable) uiElement).isHovered());
    elementShader
        .getUniform(Uniforms.FOCUSED, UniformBoolean.class)
        .load(uiElement instanceof Focusable && ((Focusable) uiElement).isFocused());

    elementVAO.draw(uiElement);
    drawCalls++;

    if (uiElement.isTextured()) {
      uiElement.getRenderable().getTexture().unbind();
    }
    backgroundShader.unbind();

    // render text
  }

  @Override
  public void cleanUp() {
    backgroundShader.cleanUp();
    backgroundVAO.cleanUp();
  }

  @Override
  public void register(UserInterface ui) {
    registered.add(ui);
    if (ui.isTextured()) {
      registeredTextures.add(ui.getRenderable().getTexture());
    }
    for (UIElement<?> element : ui.getElements()) {
      if (element.isTextured()) {
        registeredTextures.add(element.getRenderable().getTexture());
      }
    }
  }

  public void unregister(UserInterface ui) {
    registered.remove(ui);
    if (ui.isTextured()) {
      registeredTextures.remove(ui.getRenderable().getTexture());
    }
    for (UIElement<?> element : ui.getElements()) {
      if (element.isTextured()) {
        registeredTextures.remove(element.getRenderable().getTexture());
      }
    }
  }

  @Override
  public Collection<Texture> getTextures() {
    return registeredTextures;
  }

  @Override
  public int getDrawCalls() {
    return drawCalls;
  }

  @Override
  public int getNbObjects() {
    return registered.size();
  }

  @Override
  public void setWireframeColor(Vector4f wireframeColor) {
    this.wireframeColor = wireframeColor;
  }

  @Override
  public Vector4f getWireframeColor() {
    return wireframeColor;
  }

  @Override
  public Collection<VAO> getVaos() {
    return List.of(backgroundVAO, elementVAO);
  }

  @Override
  public Collection<ShaderProgram> getShaders() {
    return List.of(backgroundShader, elementShader);
  }
}
