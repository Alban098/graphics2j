/*
 * Copyright (c) 2022, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering.renderers.interfaces;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.joml.Vector4f;
import rendering.Texture;
import rendering.data.VertexArrayObject;
import rendering.fonts.Font;
import rendering.fonts.FontManager;
import rendering.interfaces.element.TextLabel;
import rendering.renderers.Renderable;
import rendering.renderers.Renderer;
import rendering.shaders.ShaderAttribute;
import rendering.shaders.ShaderAttributes;
import rendering.shaders.ShaderProgram;
import rendering.shaders.uniform.*;

public class FontRenderer implements Renderer {

  private final ShaderProgram shader;
  private final VertexArrayObject vao;

  private final Set<Texture> textures = new HashSet<>();

  private int drawCalls = 0;
  private int nbObjects = 0;

  public FontRenderer() {
    this.shader =
        new ShaderProgram(
            "src/main/resources/shaders/interface/font/simple.vert",
            "src/main/resources/shaders/interface/font/simple.geom",
            "src/main/resources/shaders/interface/font/simple.frag",
            new ShaderAttribute[] {
              ShaderAttributes.TEXT_TEXTURE_POS, ShaderAttributes.TEXT_TEXTURE_SIZE,
            },
            new Uniform[] {
              new UniformVec4(Uniforms.COLOR.getName(), new Vector4f(0, 0, 0, 1f)),
              new UniformFloat(Uniforms.FONT_WIDTH.getName(), 0.4f),
              new UniformFloat(Uniforms.FONT_BLUR.getName(), 0.15f),
            });
    this.vao = shader.createCompatibleVao(1024);
  }

  public void render(TextLabel element) {
    if (element.getText().equals("")) {
      return;
    }
    Font font = FontManager.getFont(element.getProperties().getFontFamily());
    textures.add(font.getAtlas());

    shader.bind();
    font.getAtlas().bind();

    shader
        .getUniform(Uniforms.COLOR, UniformVec4.class)
        .load(element.getProperties().getFontColor());
    shader
        .getUniform(Uniforms.FONT_WIDTH, UniformFloat.class)
        .load(element.getProperties().getFontWidth());
    shader
        .getUniform(Uniforms.FONT_BLUR, UniformFloat.class)
        .load(element.getProperties().getFontBlur());

    element.createRenderableCharacters();
    nbObjects += element.getCharacters().size();
    for (Renderable character : element.getCharacters()) {
      if (!vao.batch(character)) {
        vao.draw();
        drawCalls++;
        vao.batch(character);
      }
    }
    vao.draw();
    drawCalls++;
    font.getAtlas().unbind();
    shader.unbind();
  }

  @Override
  public void cleanUp() {
    shader.cleanUp();
    vao.cleanUp();
  }

  @Override
  public Collection<Texture> getTextures() {
    return textures;
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
    textures.clear();
  }
}
