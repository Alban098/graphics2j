/*
 * Copyright (c) 2022-2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package org.alban098.graphics2j.interfaces.renderers;

import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;

import java.util.*;
import org.alban098.graphics2j.common.Renderer;
import org.alban098.graphics2j.common.Window;
import org.alban098.graphics2j.common.resources.InternalResources;
import org.alban098.graphics2j.common.shaders.ShaderAttribute;
import org.alban098.graphics2j.common.shaders.ShaderProgram;
import org.alban098.graphics2j.common.shaders.VertexMode;
import org.alban098.graphics2j.common.shaders.data.FramebufferObject;
import org.alban098.graphics2j.common.shaders.data.Texture;
import org.alban098.graphics2j.common.shaders.data.uniform.*;
import org.alban098.graphics2j.common.shaders.data.vao.ArrayObject;
import org.alban098.graphics2j.interfaces.UIRenderable;
import org.alban098.graphics2j.interfaces.components.Clickable;
import org.alban098.graphics2j.interfaces.components.Hoverable;
import org.alban098.graphics2j.interfaces.components.Line;
import org.alban098.graphics2j.interfaces.components.UIElement;
import org.alban098.graphics2j.interfaces.components.property.Properties;
import org.alban098.graphics2j.interfaces.components.property.RenderingProperties;
import org.alban098.graphics2j.interfaces.components.text.TextLabel;
import org.alban098.graphics2j.interfaces.windows.Modal;
import org.alban098.graphics2j.interfaces.windows.UserInterface;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL30;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An implementation of a Renderer in charge of rendering {@link UserInterface}s. Rendering is done
 * recursively to avoid overflow, the rendering routine is as follows :
 *
 * <ol>
 *   <li>Render the background of the UserInterface
 *   <li>Render each UIElement onto a FBO the size of the UserInterface as follows
 *       <ol>
 *         <li>Render the background of the UIElement
 *         <li>Recursively do the same on all the UIElement's children
 *         <li>Render the FBO onto the background of the UIElement
 *       </ol>
 *   <li>Render the FBO onto the background of the UserInterface
 * </ol>
 */
public final class InterfaceRenderer implements Renderer {

  /** Just a Logger to log events */
  private static final Logger LOGGER = LoggerFactory.getLogger(InterfaceRenderer.class);
  /** The Window where to render to */
  private final Window window;
  /** The {@link FontRenderer} used to render all Text on any {@link UserInterface} */
  private final FontRenderer fontRenderer;

  /** The {@link LineRenderer} used to render all {@link Line} on any {@link UserInterface} */
  private final LineRenderer lineRenderer;
  /** A Collection of all registered {@link UserInterface} to render next frame */
  private final Collection<UserInterface> registered = new HashSet<>();
  /** A Collection of all {@link Texture} registered to the Renderer */
  private final Collection<Texture> registeredTextures = new HashSet<>();
  /**
   * A Collection of all {@link Texture} registered to the Renderer as {@link FramebufferObject}
   * rendering target
   */
  private final Collection<Texture> fboRenderingTarget = new HashSet<>();
  /**
   * The {@link ShaderProgram} used to render the {@link FramebufferObject}s and backgrounds onto
   * the Quads
   */
  private final ShaderProgram simpleShader;
  /** The {@link ShaderProgram} used to render the {@link UIElement} onto the Quads */
  private final ShaderProgram elementShader;
  /** The VAO to batch everything into */
  private final ArrayObject vao;
  /** A Collection of all currently visible {@link Modal}s */
  private final Collection<Modal> modals = new ArrayList<>();
  /** The number of drawcalls during the last frame */
  private int drawCalls = 0;
  /** The time passed rendering using the Simple Shader in nanoseconds */
  private long simpleShaderTime = 0;
  /** The time passed rendering using the Element Shader in nanoseconds */
  private long elementShaderTime = 0;
  /** A Collection of {@link FontRenderer} and {@link LineRenderer} */
  private final Collection<Renderer> renderers;
  /** A Map of times passed in each {@link ShaderProgram} */
  private final Map<ShaderProgram, Double> shaderTimes = new HashMap<>();
  /** The number of time a {@link ShaderProgram} has been bound during this frame */
  private int bounds = 0;

  /**
   * Creates a new FontRenderer and create the adequate {@link ShaderProgram}s and {@link
   * ArrayObject}s
   *
   * @param window the Window where to render to
   * @param fontRenderer the {@link FontRenderer} used to render all Text on any {@link
   *     UserInterface}
   * @param lineRenderer the {@link LineRenderer} used to render all {@link Line} on any {@link
   *     UserInterface}
   */
  public InterfaceRenderer(Window window, FontRenderer fontRenderer, LineRenderer lineRenderer) {
    this.window = window;
    this.simpleShader =
        new ShaderProgram(
            "Interface Container Shader",
            InternalResources.INTERFACE_SIMPLE_VERTEX,
            InternalResources.INTERFACE_SIMPLE_GEOMETRY,
            InternalResources.INTERFACE_SIMPLE_FRAGMENT,
            new ShaderAttribute[0],
            new Uniform[] {
              new UniformVec4(Uniforms.COLOR, new Vector4f(0, 0, 0, 1f)),
              new UniformVec3(Uniforms.BORDER_COLOR, new Vector3f(0, 0, 0)),
              new UniformBoolean(Uniforms.TEXTURED, false),
              new UniformFloat(Uniforms.RADIUS, 0),
              new UniformFloat(Uniforms.BORDER_WIDTH, 0),
              new UniformVec2(Uniforms.VIEWPORT, new Vector2f()),
            });
    this.elementShader =
        new ShaderProgram(
            "Interface Element Shader",
            InternalResources.INTERFACE_SIMPLE_VERTEX,
            InternalResources.INTERFACE_SIMPLE_GEOMETRY,
            InternalResources.INTERFACE_ELEMENT_FRAGMENT,
            new ShaderAttribute[] {},
            new Uniform[] {
              new UniformFloat(Uniforms.TIME, 0),
              new UniformVec4(Uniforms.COLOR, new Vector4f(0, 0, 0, 1f)),
              new UniformVec3(Uniforms.BORDER_COLOR, new Vector3f(0, 0, 0)),
              new UniformBoolean(Uniforms.TEXTURED, false),
              new UniformBoolean(Uniforms.CLICKED, false),
              new UniformBoolean(Uniforms.HOVERED, false),
              new UniformFloat(Uniforms.RADIUS, 0),
              new UniformFloat(Uniforms.BORDER_WIDTH, 0),
              new UniformVec2(Uniforms.VIEWPORT, new Vector2f()),
            });
    this.vao = simpleShader.createCompatibleVao(1, true, VertexMode.INDEX, null);
    this.fontRenderer = fontRenderer;
    this.lineRenderer = lineRenderer;
    shaderTimes.put(simpleShader, 0d);
    shaderTimes.put(elementShader, 0d);
    renderers = List.of(this, fontRenderer, lineRenderer);
    LOGGER.info("Successfully initialized Interface Renderer");
  }

  /** Renders all {@link UserInterface} currently visible on the screen */
  public void render() {
    prepareFrame();
    drawCalls = 0;
    for (UserInterface userInterface : registered) {
      if (userInterface.isVisible()) {
        LOGGER.trace("Rendering UserInterface {}", userInterface.getName());
        // Render container on screen
        renderContainer(userInterface);
        // Render children in the UI's FBO (with id ray-finder texture)
        renderChildren(userInterface.getElements(), userInterface.getFbo());
        // Render the FBO to the screen (only color channels)
        renderFbo(userInterface, userInterface.getFbo(), userInterface.getProperties());
      }
    }
    for (Modal modal : modals) {
      LOGGER.trace("Rendering Modal {}", modal.getName());
      renderContainer(modal);
      if (!modal.isRendered()) {
        renderChildren(modal.getElements(), modal.getFbo());
        modal.setRendered(true);
      }
      renderFbo(modal, modal.getFbo(), modal.getProperties());
    }
    modals.clear();
  }

  /** Prepare everything for the next frame rendering */
  private void prepareFrame() {
    simpleShaderTime = 0;
    elementShaderTime = 0;
    bounds = 0;
    fboRenderingTarget.clear();
    lineRenderer.prepare();
    fontRenderer.prepare();
  }

  /**
   * Renders a set of {@link UIElement} into a {@link FramebufferObject}
   *
   * @param elements the {@link UIElement} to render
   * @param fbo the {@link FramebufferObject} to render to
   */
  private void renderChildren(Collection<UIElement> elements, FramebufferObject fbo) {
    for (UIElement element : elements) {
      LOGGER.trace(
          "Rendering UIElement {} ({})", element.getName(), element.getClass().getSimpleName());
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
        renderFbo(element, element.getFbo(), element.getProperties());
      }

      // Unbind the FBO and reset the viewport
      FramebufferObject.unbind();
      GL30.glViewport(0, 0, window.getWidth(), window.getHeight());
    }
  }

  /**
   * Renders a {@link UIRenderable} texturing it with the rendering result of a {@link
   * FramebufferObject}
   *
   * @param target the {@link UIRenderable} to render
   * @param fbo the {@link FramebufferObject} to texture it with
   * @param properties the {@link RenderingProperties} to use during rendering
   */
  private void renderFbo(
      UIRenderable target, FramebufferObject fbo, RenderingProperties properties) {
    long start = System.nanoTime();
    simpleShader.bind();
    bounds++;
    glActiveTexture(GL_TEXTURE0);
    fbo.getTextureTarget(0).bind();
    simpleShader.getUniform(Uniforms.TEXTURED, UniformBoolean.class).load(true);
    simpleShader
        .getUniform(Uniforms.RADIUS, UniformFloat.class)
        .load(properties.get(Properties.CORNER_RADIUS, Float.class));
    simpleShader
        .getUniform(Uniforms.BORDER_WIDTH, UniformFloat.class)
        .load(properties.get(Properties.BORDER_WIDTH, Float.class));
    simpleShader
        .getUniform(Uniforms.BORDER_COLOR, UniformVec3.class)
        .load(properties.get(Properties.BORDER_COLOR, Vector3f.class));
    simpleShader
        .getUniform(Uniforms.VIEWPORT, UniformVec2.class)
        .load(fbo.getWidth(), fbo.getHeight());
    vao.immediateDraw(target.getRenderable(), target.getTransform());
    drawCalls++;
    fbo.getTextureTarget(0).unbind();
    simpleShader.unbind();
    fboRenderingTarget.add(fbo.getTextureTarget(0));
    simpleShaderTime += System.nanoTime() - start;
  }

  /**
   * Renders the background of a {@link UserInterface}
   *
   * @param userInterface the {@link UserInterface} to render the background of
   */
  private void renderContainer(UserInterface userInterface) {
    long start = System.nanoTime();
    simpleShader.bind();
    bounds++;
    if (userInterface.isTextured()) {
      glActiveTexture(GL_TEXTURE0);
      userInterface.getRenderable().getTexture().bind();
    }
    simpleShader
        .getUniform(Uniforms.COLOR, UniformVec4.class)
        .load(userInterface.getProperties().get(Properties.BACKGROUND_COLOR, Vector4f.class));
    simpleShader
        .getUniform(Uniforms.BORDER_COLOR, UniformVec3.class)
        .load(userInterface.getProperties().get(Properties.BORDER_COLOR, Vector3f.class));
    simpleShader
        .getUniform(Uniforms.TEXTURED, UniformBoolean.class)
        .load(userInterface.isTextured());
    simpleShader
        .getUniform(Uniforms.RADIUS, UniformFloat.class)
        .load(userInterface.getProperties().get(Properties.CORNER_RADIUS, Float.class));
    simpleShader.getUniform(Uniforms.BORDER_WIDTH, UniformFloat.class).load(0f);
    simpleShader
        .getUniform(Uniforms.VIEWPORT, UniformVec2.class)
        .load(userInterface.getProperties().get(Properties.SIZE, Vector2f.class));
    vao.immediateDraw(userInterface.getRenderable(), userInterface.getTransform());
    drawCalls++;
    simpleShader.unbind();
    simpleShaderTime += System.nanoTime() - start;
  }

  /**
   * Renders a {@link UIElement} onto a {@link FramebufferObject}
   *
   * @param uiElement the {@link UIElement} to render
   * @param fbo the {@link FramebufferObject} to render to
   */
  private void renderElement(UIElement uiElement, FramebufferObject fbo) {
    // render background
    if (uiElement.getModal() != null && uiElement.getModal().isVisible()) {
      modals.add(uiElement.getModal());
    }
    if (uiElement instanceof TextLabel) {
      fontRenderer.render((TextLabel) uiElement);
    } else if (uiElement instanceof Line) {
      lineRenderer.setViewport(fbo.getWidth(), fbo.getHeight());
      lineRenderer.render((Line) uiElement);
    } else {
      long start = System.nanoTime();
      elementShader.bind();
      bounds++;
      if (uiElement.isTextured()) {
        glActiveTexture(GL_TEXTURE0);
        uiElement.getRenderable().getTexture().bind();
      }

      elementShader.getUniform(Uniforms.TIME, UniformFloat.class).load((float) GLFW.glfwGetTime());
      elementShader
          .getUniform(Uniforms.COLOR, UniformVec4.class)
          .load(uiElement.getProperties().get(Properties.BACKGROUND_COLOR, Vector4f.class));
      elementShader
          .getUniform(Uniforms.BORDER_COLOR, UniformVec3.class)
          .load(uiElement.getProperties().get(Properties.BORDER_COLOR, Vector3f.class));
      elementShader
          .getUniform(Uniforms.RADIUS, UniformFloat.class)
          .load(uiElement.getProperties().get(Properties.CORNER_RADIUS, Float.class));
      elementShader
          .getUniform(Uniforms.BORDER_WIDTH, UniformFloat.class)
          .load(
              uiElement.getFbo() == null
                  ? uiElement.getProperties().get(Properties.BORDER_WIDTH, Float.class)
                  : 0f);
      elementShader
          .getUniform(Uniforms.VIEWPORT, UniformVec2.class)
          .load(uiElement.getProperties().get(Properties.SIZE, Vector2f.class));
      elementShader
          .getUniform(Uniforms.TEXTURED, UniformBoolean.class)
          .load(uiElement.isTextured());
      elementShader
          .getUniform(Uniforms.CLICKED, UniformBoolean.class)
          .load(uiElement instanceof Clickable && ((Clickable) uiElement).isClicked());
      elementShader
          .getUniform(Uniforms.HOVERED, UniformBoolean.class)
          .load(uiElement instanceof Hoverable && ((Hoverable) uiElement).isHovered());

      vao.immediateDraw(uiElement.getRenderable(), uiElement.getTransform());
      drawCalls++;

      if (uiElement.isTextured()) {
        uiElement.getRenderable().getTexture().unbind();
      }
      elementShader.unbind();
      elementShaderTime += System.nanoTime() - start;
    }
  }

  /**
   * Registers a new {@link UserInterface} to be renderer
   *
   * @param ui the {@link UserInterface} to register
   */
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

  /**
   * Unregisters a new {@link UserInterface} to not be renderer anymore
   *
   * @param ui the {@link UserInterface} to unregister
   */
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

  /**
   * Returns a Collection of all {@link Texture}s the Renderer can use during the rendering of a
   * frame
   *
   * @return a Collection of all {@link Texture}s the Renderer can use during the rendering of a
   *     frame
   */
  @Override
  public Collection<Texture> getTextures() {
    fboRenderingTarget.addAll(registeredTextures);
    return fboRenderingTarget;
  }

  /**
   * Returns the number of drawcalls to the GPU that occurred during the last frame, emanating from
   * this Renderer
   *
   * @return the number of drawcalls to the GPU that occurred during the last frame, emanating from
   *     this Renderer
   */
  @Override
  public int getDrawCalls() {
    return drawCalls;
  }

  /**
   * Returns the number of Objects rendered by this Renderer during the last frame
   *
   * @return the number of Objects rendered by this Renderer during the last frame
   */
  @Override
  public int getNbObjects() {
    return registered.size();
  }

  /**
   * Returns the time passed during rendering by this Renderer, binding {@link ShaderProgram},
   * {@link Texture}s loading {@link org.alban098.graphics2j.common.shaders.data.uniform.Uniform}s,
   * batching and rendering elements
   *
   * @return the total rendering time of this Renderer, in seconds
   */
  @Override
  public double getRenderingTime() {
    return (simpleShaderTime + elementShaderTime) / 1_000_000_000.0;
  }

  /**
   * Returns the number of {@link ShaderProgram#bind()} calls during this rendering pass
   *
   * @return the number of {@link ShaderProgram#bind()} calls during this rendering pass
   */
  @Override
  public int getShaderBoundCount() {
    return bounds;
  }

  /**
   * Returns a Map of the times passed with each {@link ShaderProgram} of the Renderer bound, index
   * by {@link ShaderProgram}
   *
   * @return a Map of time passed in each {@link ShaderProgram} of the Renderer
   */
  @Override
  public Map<ShaderProgram, Double> getShaderTimes() {
    shaderTimes.put(simpleShader, simpleShaderTime / 1_000_000_000.0);
    shaderTimes.put(elementShader, elementShaderTime / 1_000_000_000.0);
    return shaderTimes;
  }

  /**
   * Returns the {@link ArrayObject}s used by this Renderer
   *
   * @return a the {@link ArrayObject}s used by this Renderer
   */
  @Override
  public ArrayObject getVao() {
    return vao;
  }

  /**
   * Return a Collection of all the {@link ShaderProgram}s of this Renderer
   *
   * @return a Collection of all the {@link ShaderProgram}s of this Renderer
   */
  @Override
  public Collection<ShaderProgram> getShaders() {
    return List.of(simpleShader, elementShader);
  }

  /**
   * Returns a Collection of {@link FontRenderer} and {@link LineRenderer}
   *
   * @return a Collection of {@link FontRenderer} and {@link LineRenderer}
   */
  public Collection<Renderer> getRenderers() {
    return renderers;
  }
}
