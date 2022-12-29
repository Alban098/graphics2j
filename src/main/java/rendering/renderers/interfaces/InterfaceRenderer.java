/*
 * Copyright (c) 2022, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering.renderers.interfaces;

import static org.lwjgl.opengl.GL11C.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;

import java.util.*;
import org.joml.Vector2f;
import org.joml.Vector4f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL30;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rendering.ILogic;
import rendering.Texture;
import rendering.Window;
import rendering.data.FrameBufferObject;
import rendering.data.VertexArrayObject;
import rendering.interfaces.UIElement;
import rendering.interfaces.UserInterface;
import rendering.interfaces.element.*;
import rendering.renderers.Renderable;
import rendering.renderers.Renderer;
import rendering.renderers.RenderingMode;
import rendering.shaders.ShaderAttribute;
import rendering.shaders.ShaderProgram;
import rendering.shaders.uniform.*;

public class InterfaceRenderer implements Renderer<UserInterface> {

  private static final Logger LOGGER = LoggerFactory.getLogger(InterfaceRenderer.class);
  private final Window window;
  private final Collection<UserInterface> registered = new HashSet<>();
  private final Collection<Texture> registeredTextures = new HashSet<>();
  private final ShaderProgram simpleShader;
  private final ShaderProgram elementShader;
  private final VertexArrayObject vao;
  private Vector4f wireframeColor;
  private int drawCalls = 0;

  public InterfaceRenderer(Window window, Vector4f wireframeColor) {
    this.window = window;
    this.wireframeColor = wireframeColor;
    this.simpleShader =
        new ShaderProgram(
            "src/main/resources/shaders/interface/simple.vert",
            "src/main/resources/shaders/interface/simple.geom",
            "src/main/resources/shaders/interface/simple.frag",
            new ShaderAttribute[0],
            new Uniform[] {
              new UniformVec4(Uniforms.COLOR.getName(), new Vector4f(0, 0, 0, 1f)),
              new UniformBoolean(Uniforms.TEXTURED.getName(), false),
              new UniformVec4(Uniforms.RADIUS.getName(), new Vector4f(1, 1, 1, 1)),
              new UniformVec2(Uniforms.DIMENSION.getName(), new Vector2f()),
            });
    this.elementShader =
        new ShaderProgram(
            "src/main/resources/shaders/interface/simple.vert",
            "src/main/resources/shaders/interface/simple.geom",
            "src/main/resources/shaders/interface/element.frag",
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
    this.vao = simpleShader.createCompatibleVao(1);
  }

  @Override
  public void render(Window window, ILogic logic, RenderingMode renderingMode) {
    drawCalls = 0;
    for (UserInterface userInterface : registered) {
      renderContainer(userInterface, renderingMode);
      renderChildren(userInterface.getElements(), userInterface.getFbo(), renderingMode);
      renderFbo(userInterface, userInterface.getFbo(), userInterface.getCornerProperties());
    }
  }

  private void renderChildren(
      Collection<UIElement<?>> elements, FrameBufferObject fbo, RenderingMode renderingMode) {
    for (UIElement<?> element : elements) {

      // Render all children to the FBO recursively
      if (element.getElements().size() > 0) {
        renderChildren(element.getElements(), element.getFbo(), renderingMode);
      }

      // Bind the FBO to render to and adjust the viewport to the width of the FBO to ensure the
      // resulting texture is independent of the size of the window
      fbo.bindWithViewport();

      // Render the element
      if (element instanceof TextLabel) {
        renderText((TextLabel) element, renderingMode);
      } else {
        renderElement(element, renderingMode);
      }

      // Render the texture containing the children, after rendering the element as children are
      // always on top of their parent
      if (element.getElements().size() > 0) {
        renderFbo(element, element.getFbo(), element.getCornerProperties());
      }

      // Unbind the FBO and reset the viewport
      fbo.unbind();
      GL30.glViewport(0, 0, window.getWidth(), window.getHeight());
    }
  }

  private void renderFbo(
      Renderable target, FrameBufferObject fbo, CornerProperties cornerProperties) {
    simpleShader.bind();
    glActiveTexture(GL_TEXTURE0);
    fbo.getTextureTarget().bind();
    simpleShader.getUniform(Uniforms.WIREFRAME, UniformBoolean.class).load(false);
    simpleShader.getUniform(Uniforms.TEXTURED, UniformBoolean.class).load(true);
    simpleShader.getUniform(Uniforms.RADIUS, UniformVec4.class).load(cornerProperties.toVec4());
    simpleShader
        .getUniform(Uniforms.DIMENSION, UniformVec2.class)
        .load(fbo.getWidth(), fbo.getHeight());
    vao.draw(target);
    fbo.getTextureTarget().unbind();
    simpleShader.unbind();
    drawCalls++;
  }

  private void renderContainer(UserInterface userInterface, RenderingMode renderingMode) {
    simpleShader.bind();
    if (userInterface.isTextured()) {
      glActiveTexture(GL_TEXTURE0);
      userInterface.getRenderable().getTexture().bind();
    }
    simpleShader
        .getUniform(Uniforms.WIREFRAME, UniformBoolean.class)
        .load(renderingMode == RenderingMode.WIREFRAME);
    simpleShader.getUniform(Uniforms.WIREFRAME_COLOR, UniformVec4.class).load(wireframeColor);
    simpleShader.getUniform("color", UniformVec4.class).load(userInterface.getColor());
    simpleShader
        .getUniform(Uniforms.TEXTURED, UniformBoolean.class)
        .load(userInterface.isTextured());
    simpleShader
        .getUniform(Uniforms.RADIUS, UniformVec4.class)
        .load(userInterface.getCornerProperties().toVec4());
    simpleShader.getUniform(Uniforms.DIMENSION, UniformVec2.class).load(userInterface.getSize());
    vao.draw(userInterface);
    simpleShader.unbind();
    drawCalls++;
  }

  private void renderText(TextLabel element, RenderingMode renderingMode) {
    // TODO Call TextRenderer
    // Create a quad for each character, computing its Transform and offset in the character Atlas
    // and storing them in the TextLabel object
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

    vao.draw(uiElement);
    drawCalls++;

    if (uiElement.isTextured()) {
      uiElement.getRenderable().getTexture().unbind();
    }
    elementShader.unbind();
  }

  @Override
  public void cleanUp() {
    simpleShader.cleanUp();
    elementShader.cleanUp();
    vao.cleanUp();
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
  public Collection<VertexArrayObject> getVaos() {
    return Collections.singleton(vao);
  }

  @Override
  public Collection<ShaderProgram> getShaders() {
    return List.of(simpleShader, elementShader);
  }
}
