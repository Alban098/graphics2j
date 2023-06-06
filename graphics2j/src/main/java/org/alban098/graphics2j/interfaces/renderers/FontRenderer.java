/*
 * Copyright (c) 2022-2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package org.alban098.graphics2j.interfaces.renderers;

import java.util.*;
import org.alban098.graphics2j.common.Renderer;
import org.alban098.graphics2j.common.resources.InternalResources;
import org.alban098.graphics2j.common.shaders.ShaderAttribute;
import org.alban098.graphics2j.common.shaders.ShaderAttributes;
import org.alban098.graphics2j.common.shaders.ShaderProgram;
import org.alban098.graphics2j.common.shaders.data.Texture;
import org.alban098.graphics2j.common.shaders.data.VertexArrayObject;
import org.alban098.graphics2j.common.shaders.data.uniform.Uniform;
import org.alban098.graphics2j.common.shaders.data.uniform.UniformFloat;
import org.alban098.graphics2j.common.shaders.data.uniform.UniformVec4;
import org.alban098.graphics2j.common.shaders.data.uniform.Uniforms;
import org.alban098.graphics2j.fonts.Font;
import org.alban098.graphics2j.fonts.FontManager;
import org.alban098.graphics2j.interfaces.components.property.Properties;
import org.alban098.graphics2j.interfaces.components.text.Character;
import org.alban098.graphics2j.interfaces.components.text.TextLabel;
import org.alban098.graphics2j.interfaces.components.text.Word;
import org.alban098.graphics2j.interfaces.windows.UserInterface;
import org.joml.Vector4f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An implementation of a Renderer in charge of rendering Text present on a {@link UserInterface}
 * Fonts a rendered by precomputing a quad for each character, then rendering a sub-texture from a
 * font atlas onto it. Only support Bitmap SDF fonts for now
 */
public final class FontRenderer implements Renderer {

  /** Just a Logger to log events */
  private static final Logger LOGGER = LoggerFactory.getLogger(FontRenderer.class);

  /** The {@link ShaderProgram} to use for font rendering */
  private final ShaderProgram shader;
  /** The VAO in which to batch the {@link Character}s for rendering */
  private final VertexArrayObject vao;
  /** A Set of all registered Font Atlas {@link Texture}s */
  private final Set<Texture> textures = new HashSet<>();
  /** A Map of times passed in each {@link ShaderProgram} */
  private final Map<ShaderProgram, Double> shaderTimes = new HashMap<>();
  /** The number of draw calls for the last frame */
  private int drawCalls = 0;
  /** The number of {@link Character}s rendered during the last frame */
  private int nbObjects = 0;
  /** The time passed rendering the frame by this Renderer */
  private long renderingTimeNs = 0;
  /** The number of time the {@link ShaderProgram} has been bound during this frame */
  private int bounds = 0;

  /**
   * Creates a new FontRenderer and create the adequate {@link ShaderProgram}s and {@link
   * VertexArrayObject}s
   */
  public FontRenderer() {
    this.shader =
        new ShaderProgram(
            "Font Shader",
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
    shaderTimes.put(shader, 0d);
    LOGGER.info("Successfully initialized Font Renderer");
  }

  /**
   * Renders a Text into the screen (or the currently bounded render target)
   *
   * @param element the text to render
   */
  public void render(TextLabel element) {
    long startTime = System.nanoTime();
    LOGGER.trace(
        "Rendering Text {} ({} characters)", element.getName(), element.getText().length());
    // skip empty texts
    if (element.getText().equals("")) {
      return;
    }
    // retrieve the font and bind its texture
    Font font =
        FontManager.getFont(element.getProperties().get(Properties.FONT_FAMILY, String.class));
    textures.add(font.getAtlas());
    // bind the ShaderProgram and Texture
    shader.bind();
    bounds++;
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
        nbObjects++;
        // If batching size exceeded, draw and start a new batch
        if (!vao.batch(character.getRenderable(), character.getTransform())) {
          vao.drawBatched();
          drawCalls++;
          vao.batch(character.getRenderable(), character.getTransform());
        }
      }
    }
    // draw all batched Characters
    vao.drawBatched();
    drawCalls++;

    // unbind ShaderProgram and Texture
    font.getAtlas().unbind();
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
    return textures;
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
    drawCalls = 0;
    nbObjects = 0;
    renderingTimeNs = 0;
    bounds = 0;
    textures.clear();
  }
}
