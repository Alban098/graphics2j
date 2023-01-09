/*
 * Copyright (c) 2022-2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering.renderers.interfaces;

import java.util.Collection;
import java.util.Collections;
import org.joml.Vector2f;
import org.joml.Vector4f;
import rendering.Texture;
import rendering.data.VertexArrayObject;
import rendering.interfaces.element.Line;
import rendering.interfaces.element.property.Properties;
import rendering.interfaces.element.text.Character;
import rendering.renderers.Renderer;
import rendering.shaders.ShaderAttribute;
import rendering.shaders.ShaderAttributes;
import rendering.shaders.ShaderProgram;
import rendering.shaders.uniform.*;

/**
 * An implementation of {@link Renderer} in charge of rendering {@link Line}s present on a {@link
 * rendering.interfaces.UserInterface}
 */
public class LineRenderer implements Renderer {

  /** The {@link ShaderProgram} to use for {@link Line} rendering */
  private final ShaderProgram shader;
  /** The VAO in which to batch the {@link Line}s for rendering */
  private final VertexArrayObject vao;
  /** The number of draw calls for the last frame */
  private int drawCalls = 0;
  /** The number of {@link Line}s rendered during the last frame */
  private int nbObjects = 0;

  /**
   * Creates a new LineRenderer and create the adequate {@link ShaderProgram}s and {@link
   * VertexArrayObject}s
   */
  public LineRenderer() {
    this.shader =
        new ShaderProgram(
            "src/main/resources/shaders/interface/line/simple.vert",
            "src/main/resources/shaders/interface/line/simple.geom",
            "src/main/resources/shaders/interface/line/simple.frag",
            new ShaderAttribute[] {ShaderAttributes.LINE_START, ShaderAttributes.LINE_END},
            new Uniform[] {
              new UniformVec4(Uniforms.COLOR.getName(), new Vector4f(0, 0, 0, 1f)),
              new UniformVec2(Uniforms.VIEWPORT.getName(), new Vector2f(1, 1)),
              new UniformFloat(Uniforms.LINE_WIDTH.getName(), 0)
            });
    this.vao = shader.createCompatibleVao(1, false);
  }

  /**
   * Renders a {@link Line} into the screen (or the currently bounded render target)
   *
   * @param element the {@link Line} to render
   * @param width the width of the viewport to render to, in pixels
   * @param height the height of the viewport to render to, in pixels
   */
  public void render(Line element, int width, int height) {
    shader.bind();

    shader
        .getUniform(Uniforms.COLOR, UniformVec4.class)
        .load(element.getProperties().get(Properties.BACKGROUND_COLOR, Vector4f.class));
    shader
        .getUniform(Uniforms.LINE_WIDTH, UniformFloat.class)
        .load(element.getProperties().get(Properties.LINE_WIDTH, Float.class));
    shader.getUniform(Uniforms.VIEWPORT, UniformVec2.class).load(width, height);

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
