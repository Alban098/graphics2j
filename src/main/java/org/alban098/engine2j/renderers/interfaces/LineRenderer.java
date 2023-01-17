/*
 * Copyright (c) 2022-2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package org.alban098.engine2j.renderers.interfaces;

import java.util.Collection;
import java.util.Collections;
import org.alban098.engine2j.objects.interfaces.element.Line;
import org.alban098.engine2j.objects.interfaces.element.property.Properties;
import org.alban098.engine2j.objects.interfaces.element.text.Character;
import org.alban098.engine2j.renderers.SingleElementRenderer;
import org.alban098.engine2j.shaders.ShaderAttribute;
import org.alban098.engine2j.shaders.ShaderAttributes;
import org.alban098.engine2j.shaders.ShaderProgram;
import org.alban098.engine2j.shaders.data.Texture;
import org.alban098.engine2j.shaders.data.VertexArrayObject;
import org.alban098.engine2j.shaders.data.uniform.*;
import org.joml.Vector2f;
import org.joml.Vector4f;

/**
 * An implementation of {@link SingleElementRenderer} in charge of rendering {@link Line}s present
 * inside a {@link org.alban098.engine2j.objects.interfaces.UserInterface}
 */
public final class LineRenderer implements SingleElementRenderer<Line> {

  /** The {@link ShaderProgram} to use for {@link Line} rendering */
  private final ShaderProgram shader;
  /** The VAO in which to batch the {@link Line}s for rendering */
  private final VertexArrayObject vao;
  /** The number of draw calls for the last frame */
  private int drawCalls = 0;
  /** The number of {@link Line}s rendered during the last frame */
  private int nbObjects = 0;
  /** The current viewport to render into in pixels */
  private final Vector2f viewport = new Vector2f();

  /**
   * Creates a new LineRenderer and create the adequate {@link ShaderProgram}s and {@link
   * VertexArrayObject}s
   */
  public LineRenderer() {
    this.shader =
        new ShaderProgram(
            "engine2j/shaders/interface/line/simple.vert",
            "engine2j/shaders/interface/line/simple.geom",
            "engine2j/shaders/interface/line/simple.frag",
            new ShaderAttribute[] {ShaderAttributes.LINE_START, ShaderAttributes.LINE_END},
            new Uniform[] {
              new UniformVec4(Uniforms.COLOR, new Vector4f(0, 0, 0, 1f)),
              new UniformVec2(Uniforms.VIEWPORT, new Vector2f(1, 1)),
              new UniformFloat(Uniforms.LINE_WIDTH, 0)
            });
    this.vao = shader.createCompatibleVao(1, false);
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
    shader.bind();

    shader
        .getUniform(Uniforms.COLOR, UniformVec4.class)
        .load(element.getProperties().get(Properties.BACKGROUND_COLOR, Vector4f.class));
    shader
        .getUniform(Uniforms.LINE_WIDTH, UniformFloat.class)
        .load(element.getProperties().get(Properties.LINE_WIDTH, Float.class));
    shader.getUniform(Uniforms.VIEWPORT, UniformVec2.class).load(viewport.x, viewport.y);

    nbObjects++;
    vao.immediateDraw(element);
    drawCalls++;
    shader.unbind();
  }

  /** Clear the Renderer by clearing its {@link ShaderProgram}s and {@link VertexArrayObject}s */
  @Override
  public void cleanUp() {
    shader.cleanUp();
    vao.cleanUp();
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
    return drawCalls;
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
  public Collection<VertexArrayObject> getVaos() {
    return Collections.singleton(vao);
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
    drawCalls = 0;
    nbObjects = 0;
  }
}
