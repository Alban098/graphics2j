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
import rendering.renderers.Renderer;
import rendering.shaders.ShaderAttribute;
import rendering.shaders.ShaderAttributes;
import rendering.shaders.ShaderProgram;
import rendering.shaders.uniform.*;

public class LineRenderer implements Renderer {

  private final ShaderProgram shader;
  private final VertexArrayObject vao;
  private int drawCalls = 0;
  private int nbObjects = 0;

  public LineRenderer() {
    this.shader =
        new ShaderProgram(
            "src/main/resources/shaders/interface/line/simple.vert",
            "src/main/resources/shaders/interface/line/simple.geom",
            "src/main/resources/shaders/interface/line/simple.frag",
            new ShaderAttribute[] {
              ShaderAttributes.LINE_START, ShaderAttributes.LINE_END, ShaderAttributes.UI_ELEMENT_ID
            },
            new Uniform[] {
              new UniformVec4(Uniforms.COLOR.getName(), new Vector4f(0, 0, 0, 1f)),
              new UniformVec2(Uniforms.VIEWPORT.getName(), new Vector2f(1, 1)),
              new UniformFloat(Uniforms.LINE_WIDTH.getName(), 0)
            });
    this.vao = shader.createCompatibleVao(1, false);
  }

  public void render(Line element, int width, int height) {
    shader.bind();

    shader
        .getUniform(Uniforms.COLOR, UniformVec4.class)
        .load(element.getProperties().getBackgroundColor());
    shader
        .getUniform(Uniforms.LINE_WIDTH, UniformFloat.class)
        .load(element.getProperties().getLineWidth());
    shader.getUniform(Uniforms.VIEWPORT, UniformVec2.class).load(width, height);

    nbObjects++;
    vao.draw(element);
    drawCalls++;
    shader.unbind();
  }

  @Override
  public void cleanUp() {
    shader.cleanUp();
    vao.cleanUp();
  }

  @Override
  public Collection<Texture> getTextures() {
    return Collections.emptyList();
  }

  @Override
  public int getDrawCalls() {
    return drawCalls;
  }

  @Override
  public int getNbObjects() {
    return nbObjects;
  }

  @Override
  public Collection<VertexArrayObject> getVaos() {
    return Collections.singleton(vao);
  }

  @Override
  public Collection<ShaderProgram> getShaders() {
    return Collections.singleton(shader);
  }

  public void prepare() {
    drawCalls = 0;
    nbObjects = 0;
  }
}
