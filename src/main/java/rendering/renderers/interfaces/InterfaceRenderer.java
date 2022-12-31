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
import org.joml.Vector3f;
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
import rendering.interfaces.element.Properties;
import rendering.renderers.RegisterableRenderer;
import rendering.renderers.Renderable;
import rendering.shaders.ShaderAttribute;
import rendering.shaders.ShaderProgram;
import rendering.shaders.uniform.*;

public class InterfaceRenderer implements RegisterableRenderer<UserInterface> {

  private static final Logger LOGGER = LoggerFactory.getLogger(InterfaceRenderer.class);
  private final Window window;
  private final FontRenderer fontRenderer;
  private final Collection<UserInterface> registered = new HashSet<>();
  private final Collection<Texture> registeredTextures = new HashSet<>();
  private final Collection<Texture> fboRenderingTarget = new HashSet<>();
  private final ShaderProgram simpleShader;
  private final ShaderProgram elementShader;
  private final VertexArrayObject vao;
  private int drawCalls = 0;

  public InterfaceRenderer(Window window, FontRenderer fontRenderer) {
    this.window = window;
    this.simpleShader =
        new ShaderProgram(
            "src/main/resources/shaders/interface/simple.vert",
            "src/main/resources/shaders/interface/simple.geom",
            "src/main/resources/shaders/interface/simple.frag",
            new ShaderAttribute[0],
            new Uniform[] {
              new UniformVec4(Uniforms.COLOR.getName(), new Vector4f(0, 0, 0, 1f)),
              new UniformVec3(Uniforms.BORDER_COLOR.getName(), new Vector3f(0, 0, 0)),
              new UniformBoolean(Uniforms.TEXTURED.getName(), false),
              new UniformFloat(Uniforms.RADIUS.getName(), 0),
              new UniformFloat(Uniforms.BORDER_WIDTH.getName(), 0),
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
              new UniformVec3(Uniforms.BORDER_COLOR.getName(), new Vector3f(0, 0, 0)),
              new UniformBoolean(Uniforms.TEXTURED.getName(), false),
              new UniformBoolean(Uniforms.CLICKED.getName(), false),
              new UniformBoolean(Uniforms.HOVERED.getName(), false),
              new UniformBoolean(Uniforms.FOCUSED.getName(), false),
              new UniformFloat(Uniforms.RADIUS.getName(), 0),
              new UniformFloat(Uniforms.BORDER_WIDTH.getName(), 0),
              new UniformVec2(Uniforms.DIMENSION.getName(), new Vector2f()),
            });
    this.vao = simpleShader.createCompatibleVao(1);
    this.fontRenderer = fontRenderer;
  }

  @Override
  public void render(Window window, ILogic logic) {
    drawCalls = 0;
    for (UserInterface userInterface : registered) {
      renderContainer(userInterface);
      renderChildren(userInterface.getElements(), userInterface.getFbo());
      renderFbo(userInterface, userInterface.getFbo(), userInterface.getProperties());
    }
  }

  private void renderChildren(Collection<UIElement> elements, FrameBufferObject fbo) {
    for (UIElement element : elements) {
      if (element instanceof TextLabel && ((TextLabel) element).getText().equals("")) {
        continue;
      }

      // Render all children to the FBO recursively
      if (element.getElements().size() > 0) {
        renderChildren(element.getElements(), element.getFbo());
      }

      // Bind the FBO to render to and adjust the viewport to the width of the FBO to ensure the
      // resulting texture is independent of the size of the window
      fbo.bindWithViewport();

      // Render the element
      if (element instanceof TextLabel) {
        fontRenderer.render((TextLabel) element);
      } else {
        renderElement(element);
      }

      // Render the texture containing the children, after rendering the element as children are
      // always on top of their parent
      if (element.getElements().size() > 0) {
        renderFbo(element, element.getFbo(), element.getProperties());
      }

      // Unbind the FBO and reset the viewport
      fbo.unbind();
      GL30.glViewport(0, 0, window.getWidth(), window.getHeight());
    }
  }

  private void renderFbo(Renderable target, FrameBufferObject fbo, Properties properties) {
    simpleShader.bind();
    glActiveTexture(GL_TEXTURE0);
    fbo.getTextureTarget().bind();
    simpleShader.getUniform(Uniforms.TEXTURED, UniformBoolean.class).load(true);
    simpleShader.getUniform(Uniforms.RADIUS, UniformFloat.class).load(properties.getCornerRadius());
    simpleShader
        .getUniform(Uniforms.BORDER_WIDTH, UniformFloat.class)
        .load(properties.getBorderWidth());
    simpleShader
        .getUniform(Uniforms.BORDER_COLOR, UniformVec3.class)
        .load(properties.getBorderColor());
    simpleShader
        .getUniform(Uniforms.DIMENSION, UniformVec2.class)
        .load(fbo.getWidth(), fbo.getHeight());
    vao.draw(target);
    fbo.getTextureTarget().unbind();
    simpleShader.unbind();
    drawCalls++;
    fboRenderingTarget.add(fbo.getTextureTarget());
  }

  private void renderContainer(UserInterface userInterface) {
    simpleShader.bind();
    if (userInterface.isTextured()) {
      glActiveTexture(GL_TEXTURE0);
      userInterface.getRenderable().getTexture().bind();
    }
    simpleShader
        .getUniform(Uniforms.COLOR, UniformVec4.class)
        .load(userInterface.getProperties().getBackgroundColor());
    simpleShader
        .getUniform(Uniforms.BORDER_COLOR, UniformVec3.class)
        .load(userInterface.getProperties().getBorderColor());
    simpleShader
        .getUniform(Uniforms.TEXTURED, UniformBoolean.class)
        .load(userInterface.isTextured());
    simpleShader
        .getUniform(Uniforms.RADIUS, UniformFloat.class)
        .load(userInterface.getProperties().getCornerRadius());
    simpleShader.getUniform(Uniforms.BORDER_WIDTH, UniformFloat.class).load(0f);
    simpleShader
        .getUniform(Uniforms.DIMENSION, UniformVec2.class)
        .load(userInterface.getProperties().getSize());
    vao.draw(userInterface);
    simpleShader.unbind();
    drawCalls++;
  }

  private void renderElement(UIElement uiElement) {
    // render background
    elementShader.bind();
    if (uiElement.isTextured()) {
      glActiveTexture(GL_TEXTURE0);
      uiElement.getRenderable().getTexture().bind();
    }

    elementShader.getUniform(Uniforms.TIME_MS, UniformFloat.class).load((float) GLFW.glfwGetTime());
    elementShader
        .getUniform(Uniforms.COLOR, UniformVec4.class)
        .load(uiElement.getProperties().getBackgroundColor());
    elementShader
        .getUniform(Uniforms.BORDER_COLOR, UniformVec3.class)
        .load(uiElement.getProperties().getBorderColor());
    elementShader
        .getUniform(Uniforms.RADIUS, UniformFloat.class)
        .load(uiElement.getProperties().getCornerRadius());
    elementShader
        .getUniform(Uniforms.BORDER_WIDTH, UniformFloat.class)
        .load(uiElement.getFbo() == null ? uiElement.getProperties().getBorderWidth() : 0);
    elementShader
        .getUniform(Uniforms.DIMENSION, UniformVec2.class)
        .load(uiElement.getProperties().getSize());
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
    for (UIElement element : ui.getElements()) {
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
    for (UIElement element : ui.getElements()) {
      if (element.isTextured()) {
        registeredTextures.remove(element.getRenderable().getTexture());
      }
    }
  }

  @Override
  public Collection<Texture> getTextures() {
    fboRenderingTarget.addAll(registeredTextures);
    return fboRenderingTarget;
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
  public Collection<VertexArrayObject> getVaos() {
    return Collections.singleton(vao);
  }

  @Override
  public Collection<ShaderProgram> getShaders() {
    return List.of(simpleShader, elementShader);
  }

  public void prepare() {
    fboRenderingTarget.clear();
  }
}
