/*
 * Copyright (c) 2022-2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering.renderers.interfaces;

import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL13C.GL_TEXTURE1;

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
import rendering.interfaces.Modal;
import rendering.interfaces.UserInterface;
import rendering.interfaces.element.*;
import rendering.interfaces.element.Properties;
import rendering.interfaces.element.UIElement;
import rendering.renderers.RegisterableRenderer;
import rendering.shaders.ShaderAttribute;
import rendering.shaders.ShaderAttributes;
import rendering.shaders.ShaderProgram;
import rendering.shaders.uniform.*;

public class InterfaceRenderer implements RegisterableRenderer<UserInterface> {

  private static final Logger LOGGER = LoggerFactory.getLogger(InterfaceRenderer.class);
  private final Window window;
  private final FontRenderer fontRenderer;
  private final LineRenderer lineRenderer;
  private final Collection<UserInterface> registered = new HashSet<>();
  private final Collection<Texture> registeredTextures = new HashSet<>();
  private final Collection<Texture> fboRenderingTarget = new HashSet<>();
  private final ShaderProgram simpleShader;
  private final ShaderProgram doubleShader;
  private final ShaderProgram elementShader;
  private final VertexArrayObject vao;
  private final VertexArrayObject doubleVao;

  private final Collection<Modal> modals = new ArrayList<>();
  private int drawCalls = 0;

  public InterfaceRenderer(Window window, FontRenderer fontRenderer, LineRenderer lineRenderer) {
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
              new UniformVec2(Uniforms.VIEWPORT.getName(), new Vector2f()),
            });
    this.doubleShader =
        new ShaderProgram(
            "src/main/resources/shaders/interface/doubleAttachments/double.vert",
            "src/main/resources/shaders/interface/doubleAttachments/double.geom",
            "src/main/resources/shaders/interface/doubleAttachments/double.frag",
            new ShaderAttribute[] {ShaderAttributes.UI_ELEMENT_ID},
            new Uniform[] {
              new UniformVec4(Uniforms.COLOR.getName(), new Vector4f(0, 0, 0, 1f)),
              new UniformVec3(Uniforms.BORDER_COLOR.getName(), new Vector3f(0, 0, 0)),
              new UniformBoolean(Uniforms.TEXTURED.getName(), false),
              new UniformFloat(Uniforms.RADIUS.getName(), 0),
              new UniformFloat(Uniforms.BORDER_WIDTH.getName(), 0),
              new UniformVec2(Uniforms.VIEWPORT.getName(), new Vector2f()),
            });
    this.elementShader =
        new ShaderProgram(
            "src/main/resources/shaders/interface/doubleAttachments/double.vert",
            "src/main/resources/shaders/interface/doubleAttachments/double.geom",
            "src/main/resources/shaders/interface/doubleAttachments/element.frag",
            new ShaderAttribute[] {ShaderAttributes.UI_ELEMENT_ID},
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
              new UniformVec2(Uniforms.VIEWPORT.getName(), new Vector2f()),
            });
    this.vao = simpleShader.createCompatibleVao(1, true);
    this.doubleVao = doubleShader.createCompatibleVao(1, true);
    this.fontRenderer = fontRenderer;
    this.lineRenderer = lineRenderer;
  }

  @Override
  public void render(Window window, ILogic logic) {
    drawCalls = 0;
    for (UserInterface userInterface : registered) {
      // Render container on screen
      renderContainer(userInterface);
      // Render children in the UI's FBO (with id ray-finder texture)
      renderChildren(userInterface.getElements(), userInterface.getFbo());
      // Render the FBO to the screen (only color channels)
      renderUIFbo(userInterface, userInterface.getFbo(), userInterface.getProperties());
    }
    for (Modal modal : modals) {
      renderContainer(modal);
      if (!modal.isPreRendered()) {
        renderChildren(modal.getElements(), modal.getFbo());
      }
      renderUIFbo(modal, modal.getFbo(), modal.getProperties());
    }
    modals.clear();
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
      fbo.setViewportAndBind();

      // Render the element
      renderElement(element, fbo);

      // Render the texture containing the children, after rendering the element as children are
      // always on top of their parent
      if (element.getElements().size() > 0) {
        renderElementFbo(element, element.getFbo(), element.getProperties());
      }

      // Unbind the FBO and reset the viewport
      fbo.unbind();
      GL30.glViewport(0, 0, window.getWidth(), window.getHeight());
    }
  }

  private void renderElementFbo(UIElement target, FrameBufferObject fbo, Properties properties) {
    doubleShader.bind();
    glActiveTexture(GL_TEXTURE0);
    fbo.getTextureTarget(0).bind();
    glActiveTexture(GL_TEXTURE1);
    fbo.getTextureTarget(1).bind();
    doubleShader.getUniform(Uniforms.TEXTURED, UniformBoolean.class).load(true);
    doubleShader.getUniform(Uniforms.RADIUS, UniformFloat.class).load(properties.getCornerRadius());
    doubleShader
        .getUniform(Uniforms.BORDER_WIDTH, UniformFloat.class)
        .load(properties.getBorderWidth());
    doubleShader
        .getUniform(Uniforms.BORDER_COLOR, UniformVec3.class)
        .load(properties.getBorderColor());
    doubleShader
        .getUniform(Uniforms.VIEWPORT, UniformVec2.class)
        .load(fbo.getWidth(), fbo.getHeight());
    doubleVao.draw(target);
    fbo.getTextureTarget(1).unbind();
    glActiveTexture(GL_TEXTURE0);
    fbo.getTextureTarget(0).unbind();
    doubleShader.unbind();
    drawCalls++;
    fboRenderingTarget.add(fbo.getTextureTarget(0));
  }

  private void renderUIFbo(UserInterface target, FrameBufferObject fbo, Properties properties) {
    simpleShader.bind();
    glActiveTexture(GL_TEXTURE0);
    fbo.getTextureTarget(1).bind();
    simpleShader.getUniform(Uniforms.TEXTURED, UniformBoolean.class).load(true);
    simpleShader.getUniform(Uniforms.RADIUS, UniformFloat.class).load(properties.getCornerRadius());
    simpleShader
        .getUniform(Uniforms.BORDER_WIDTH, UniformFloat.class)
        .load(properties.getBorderWidth());
    simpleShader
        .getUniform(Uniforms.BORDER_COLOR, UniformVec3.class)
        .load(properties.getBorderColor());
    simpleShader
        .getUniform(Uniforms.VIEWPORT, UniformVec2.class)
        .load(fbo.getWidth(), fbo.getHeight());
    vao.draw(target);
    fbo.getTextureTarget(1).unbind();
    simpleShader.unbind();
    drawCalls++;
    fboRenderingTarget.add(fbo.getTextureTarget(0));
    fboRenderingTarget.add(fbo.getTextureTarget(1));
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
        .getUniform(Uniforms.VIEWPORT, UniformVec2.class)
        .load(userInterface.getProperties().getSize());
    vao.draw(userInterface);
    simpleShader.unbind();
    drawCalls++;
  }

  private void renderElement(UIElement uiElement, FrameBufferObject fbo) {
    // render background
    if (uiElement.getModal() != null && uiElement.getModal().isVisible()) {
      modals.add(uiElement.getModal());
    }
    if (uiElement instanceof TextLabel) {
      fontRenderer.render((TextLabel) uiElement);
    } else if (uiElement instanceof Line) {
      lineRenderer.render((Line) uiElement, fbo.getWidth(), fbo.getHeight());
    } else {
      elementShader.bind();
      if (uiElement.isTextured()) {
        glActiveTexture(GL_TEXTURE0);
        uiElement.getRenderable().getTexture().bind();
      }

      elementShader
          .getUniform(Uniforms.TIME_MS, UniformFloat.class)
          .load((float) GLFW.glfwGetTime());
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
          .getUniform(Uniforms.VIEWPORT, UniformVec2.class)
          .load(uiElement.getProperties().getSize());
      elementShader
          .getUniform(Uniforms.TEXTURED, UniformBoolean.class)
          .load(uiElement.isTextured());
      elementShader
          .getUniform(Uniforms.CLICKED, UniformBoolean.class)
          .load(uiElement instanceof Clickable && ((Clickable) uiElement).isClicked());
      elementShader
          .getUniform(Uniforms.HOVERED, UniformBoolean.class)
          .load(uiElement instanceof Hoverable && ((Hoverable) uiElement).isHovered());

      doubleVao.draw(uiElement);
      drawCalls++;

      if (uiElement.isTextured()) {
        uiElement.getRenderable().getTexture().unbind();
      }
      elementShader.unbind();
    }
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
    return List.of(simpleShader, elementShader, doubleShader);
  }

  public void prepare() {
    fboRenderingTarget.clear();
  }
}
