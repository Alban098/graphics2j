/*
 * Copyright (c) 2022-2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package org.alban098.engine2j.interfaces.renderers;

import org.alban098.engine2j.common.Cleanable;
import org.alban098.engine2j.common.resources.InternalResources;
import org.alban098.engine2j.common.shaders.ShaderAttribute;
import org.alban098.engine2j.common.shaders.ShaderAttributes;
import org.alban098.engine2j.common.shaders.ShaderProgram;
import org.alban098.engine2j.common.shaders.data.VertexArrayObject;
import org.alban098.engine2j.common.shaders.data.uniform.Uniform;
import org.alban098.engine2j.common.shaders.data.uniform.UniformFloat;
import org.alban098.engine2j.common.shaders.data.uniform.UniformVec4;
import org.alban098.engine2j.common.shaders.data.uniform.Uniforms;
import org.alban098.engine2j.fonts.Font;
import org.alban098.engine2j.fonts.FontManager;
import org.alban098.engine2j.interfaces.components.property.Properties;
import org.alban098.engine2j.interfaces.components.text.Character;
import org.alban098.engine2j.interfaces.components.text.TextLabel;
import org.alban098.engine2j.interfaces.components.text.Word;
import org.alban098.engine2j.interfaces.windows.UserInterface;
import org.joml.Vector4f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An implementation of a Renderer in charge of rendering Text present on a {@link UserInterface}
 * Fonts a rendered by precomputing a quad for each character, then rendering a sub-texture from a
 * font atlas onto it. Only support Bitmap SDF fonts for now
 */
public final class FontRenderer implements Cleanable {

  /** Just a Logger to log events */
  private static final Logger LOGGER = LoggerFactory.getLogger(FontRenderer.class);

  /** The {@link ShaderProgram} to use for font rendering */
  private final ShaderProgram shader;
  /** The VAO in which to batch the {@link Character}s for rendering */
  private final VertexArrayObject vao;

  /**
   * Creates a new FontRenderer and create the adequate {@link ShaderProgram}s and {@link
   * VertexArrayObject}s
   */
  public FontRenderer() {
    this.shader =
        new ShaderProgram(
            InternalResources.INTERFACE_FONT_VERTEX,
            InternalResources.INTERFACE_FONT_GEOMETRY,
            InternalResources.INTERFACE_FONT_FRAGMENT,
            new ShaderAttribute[] {
              ShaderAttributes.TEXT_TEXTURE_POS, ShaderAttributes.TEXT_TEXTURE_SIZE
            },
            new Uniform[] {
              new UniformVec4(Uniforms.COLOR, new Vector4f(0, 0, 0, 1f)),
              new UniformFloat(Uniforms.FONT_WIDTH, 0.4f),
              new UniformFloat(Uniforms.FONT_BLUR, 0.15f),
            });
    this.vao = shader.createCompatibleVao(64, true);
    LOGGER.info("Successfully initialized Font Renderer");
  }

  /**
   * Renders a Text into the screen (or the currently bounded render target)
   *
   * @param element the text to render
   */
  public void render(TextLabel element) {
    LOGGER.trace(
        "Rendering Text {} ({} characters)", element.getName(), element.getText().length());
    // skip empty texts
    if (element.getText().equals("")) {
      return;
    }
    // retrieve the font and bind its texture
    Font font =
        FontManager.getFont(element.getProperties().get(Properties.FONT_FAMILY, String.class));

    // bind the ShaderProgram and Texture
    shader.bind();
    font.getAtlas().bind();

    // loads all the uniforms for rendering
    shader
        .getUniform(Uniforms.COLOR, UniformVec4.class)
        .load(element.getProperties().get(Properties.FONT_COLOR, Vector4f.class));
    shader
        .getUniform(Uniforms.FONT_WIDTH, UniformFloat.class)
        .load(element.getProperties().get(Properties.FONT_WIDTH, Float.class));
    shader
        .getUniform(Uniforms.FONT_BLUR, UniformFloat.class)
        .load(element.getProperties().get(Properties.FONT_BLUR, Float.class));

    // batch all the Characters of the text
    for (Word word : element) {
      for (Character character : word) {
        // If batching size exceeded, draw and start a new batch
        if (!vao.batch(character)) {
          vao.drawBatched();
          vao.batch(character);
        }
      }
    }
    // draw all batched Characters
    vao.drawBatched();

    // unbind ShaderProgram and Texture
    font.getAtlas().unbind();
    shader.unbind();
  }

  /** Clear the Renderer by clearing its {@link ShaderProgram}s and {@link VertexArrayObject}s */
  @Override
  public void cleanUp() {
    shader.cleanUp();
    vao.cleanUp();
  }
}
