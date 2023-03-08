/*
 * Copyright (c) 2022-2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package org.alban098.engine2j.interfaces.renderers;

import org.alban098.engine2j.common.resources.InternalResources;
import org.alban098.engine2j.common.shaders.ShaderAttribute;
import org.alban098.engine2j.common.shaders.ShaderAttributes;
import org.alban098.engine2j.common.shaders.ShaderProgram;
import org.alban098.engine2j.common.shaders.data.VertexArrayObject;
import org.alban098.engine2j.common.shaders.data.uniform.*;
import org.alban098.engine2j.interfaces.components.Line;
import org.alban098.engine2j.interfaces.components.property.Properties;
import org.alban098.engine2j.interfaces.windows.UserInterface;
import org.joml.Vector2f;
import org.joml.Vector4f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An implementation of Renderer in charge of rendering {@link Line}s present
 * inside a {@link UserInterface}
 */
public final class LineRenderer {

  /** Just a Logger to log events */
  private static final Logger LOGGER = LoggerFactory.getLogger(LineRenderer.class);

  /** The {@link ShaderProgram} to use for {@link Line} rendering */
  private final ShaderProgram shader;
  /** The VAO in which to batch the {@link Line}s for rendering */
  private final VertexArrayObject vao;
  /** The current viewport to render into in pixels */
  private final Vector2f viewport = new Vector2f();

  /**
   * Creates a new LineRenderer and create the adequate {@link ShaderProgram}s and {@link
   * VertexArrayObject}s
   */
  public LineRenderer() {
    this.shader =
        new ShaderProgram(
            InternalResources.INTERFACE_LINE_VERTEX,
            InternalResources.INTERFACE_LINE_GEOMETRY,
            InternalResources.INTERFACE_LINE_FRAGMENT,
            new ShaderAttribute[] {ShaderAttributes.LINE_START, ShaderAttributes.LINE_END},
            new Uniform[] {
              new UniformVec4(Uniforms.COLOR, new Vector4f(0, 0, 0, 1f)),
              new UniformVec2(Uniforms.VIEWPORT, new Vector2f(1, 1)),
              new UniformFloat(Uniforms.LINE_WIDTH, 0)
            });
    this.vao = shader.createCompatibleVao(1, false);
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
    LOGGER.trace("Rendering Line {}", element.getName());
    shader.bind();
    shader
        .getUniform(Uniforms.COLOR, UniformVec4.class)
        .load(element.getProperties().get(Properties.BACKGROUND_COLOR, Vector4f.class));
    shader
        .getUniform(Uniforms.LINE_WIDTH, UniformFloat.class)
        .load(element.getProperties().get(Properties.LINE_WIDTH, Float.class));
    shader.getUniform(Uniforms.VIEWPORT, UniformVec2.class).load(viewport.x, viewport.y);

    vao.immediateDraw(element);
    shader.unbind();
  }
}
