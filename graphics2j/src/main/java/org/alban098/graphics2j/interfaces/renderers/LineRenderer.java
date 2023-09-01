/*
 * Copyright (c) 2022-2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package org.alban098.graphics2j.interfaces.renderers;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.alban098.graphics2j.common.Renderer;
import org.alban098.graphics2j.common.resources.InternalResources;
import org.alban098.graphics2j.common.shaders.ShaderAttribute;
import org.alban098.graphics2j.common.shaders.ShaderAttributes;
import org.alban098.graphics2j.common.shaders.ShaderProgram;
import org.alban098.graphics2j.common.shaders.data.Primitive;
import org.alban098.graphics2j.common.shaders.data.Texture;
import org.alban098.graphics2j.common.shaders.data.VertexArrayObject;
import org.alban098.graphics2j.common.shaders.data.uniform.*;
import org.alban098.graphics2j.interfaces.components.Line;
import org.alban098.graphics2j.interfaces.components.property.Properties;
import org.alban098.graphics2j.interfaces.windows.UserInterface;
import org.joml.Vector2f;
import org.joml.Vector4f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An implementation of Renderer in charge of rendering {@link Line}s present inside a {@link
 * UserInterface}
 */
public final class LineRenderer implements Renderer {

  /** Just a Logger to log events */
  private static final Logger LOGGER = LoggerFactory.getLogger(LineRenderer.class);

  /** The {@link ShaderProgram} to use for {@link Line} rendering */
  private final ShaderProgram shader;
  /** The VAO in which to batch the {@link Line}s for rendering */
  private final VertexArrayObject vao;
  /** The current viewport to render into in pixels */
  private final Vector2f viewport = new Vector2f();
  /** A Map of times passed in each {@link ShaderProgram} */
  private final Map<ShaderProgram, Double> shaderTimes = new HashMap<>();
  /** The number of {@link Line}s rendered during the last frame */
  private int nbObjects = 0;
  /** The time of the last rendering pass in nanoseconds */
  private long renderingTimeNs = 0;
  /** The number of time a {@link ShaderProgram} has been bound during this frame */
  private int bounds = 0;

  /**
   * Creates a new LineRenderer and create the adequate {@link ShaderProgram}s and {@link
   * VertexArrayObject}s
   */
  public LineRenderer() {
    this.shader =
        new ShaderProgram(
            "Line Shader",
            InternalResources.INTERFACE_LINE_VERTEX,
            InternalResources.INTERFACE_LINE_FRAGMENT,
            new ShaderAttribute[] {ShaderAttributes.LINE_START, ShaderAttributes.LINE_END},
            new Uniform[] {
              new UniformVec4(Uniforms.COLOR, new Vector4f(0, 0, 0, 1f)),
              new UniformVec2(Uniforms.VIEWPORT, new Vector2f(1, 1)),
              new UniformFloat(Uniforms.LINE_WIDTH, 0)
            });
    this.vao = shader.createCompatibleVao(1, false, Primitive.BIG_QUAD);
    shaderTimes.put(shader, 0d);
    LOGGER.info("Successfully initialized Line Renderer");
  }

  /**
   * Resizes the viewport to render into
   *
   * @param width the width of the viewport to render to, in pixels
   * @param height the height of the viewport to render to, in pixels
   */
  public void setViewport(int width, int height) {
    viewport.set(width, height);
  }

  /**
   * Renders a {@link Line} into the screen (or the currently bounded render target)
   *
   * @param element the {@link Line} to render
   */
  public void render(Line element) {
    long startTime = System.nanoTime();
    LOGGER.trace("Rendering Line {}", element.getName());
    shader.bind();
    bounds++;
    shader
        .getUniform(Uniforms.COLOR, UniformVec4.class)
        .load(element.getProperties().get(Properties.BACKGROUND_COLOR, Vector4f.class));
    shader
        .getUniform(Uniforms.LINE_WIDTH, UniformFloat.class)
        .load(element.getProperties().get(Properties.LINE_WIDTH, Float.class));
    shader.getUniform(Uniforms.VIEWPORT, UniformVec2.class).load(viewport.x, viewport.y);

    vao.immediateDraw(element.getRenderable(), element.getTransform());
    nbObjects++;
    shader.unbind();
    renderingTimeNs += System.nanoTime() - startTime;
  }

  /**
   * Returns all the currently used {@link Texture}s (used for the last frame)
   *
   * @return all the currently used {@link Texture}s
   */
  @Override
  public Collection<Texture> getTextures() {
    return Collections.emptyList();
  }

  /**
   * Returns the number of draw calls for the last frame
   *
   * @return the number of draw calls for the last frame
   */
  @Override
  public int getDrawCalls() {
    return nbObjects;
  }

  /**
   * Returns the number of rendered {@link Character}s for the last frame
   *
   * @return the number of rendered {@link Character}s for the last frame
   */
  @Override
  public int getNbObjects() {
    return nbObjects;
  }

  /**
   * Return a Collection of all the {@link VertexArrayObject}s of this Renderer
   *
   * @return a Collection of all the {@link VertexArrayObject}s of this Renderer
   */
  @Override
  public VertexArrayObject getVao() {
    return vao;
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
    return renderingTimeNs / 1_000_000_000.0;
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
    shaderTimes.put(shader, getRenderingTime());
    return shaderTimes;
  }

  /**
   * Return a Collection of all the {@link ShaderProgram}s of this Renderer
   *
   * @return a Collection of all the {@link ShaderProgram}s of this Renderer
   */
  @Override
  public Collection<ShaderProgram> getShaders() {
    return Collections.singleton(shader);
  }

  /** Prepare the Renderer for the next frame */
  public void prepare() {
    nbObjects = 0;
    bounds = 0;
    renderingTimeNs = 0;
    shaderTimes.put(shader, 0d);
  }
}
