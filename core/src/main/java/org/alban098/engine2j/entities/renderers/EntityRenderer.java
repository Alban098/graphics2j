/*
 * Copyright (c) 2022-2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package org.alban098.engine2j.entities.renderers;

import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;

import java.util.*;

import org.alban098.engine2j.common.Cleanable;
import org.alban098.engine2j.common.Window;
import org.alban098.engine2j.common.components.Camera;
import org.alban098.engine2j.common.components.RenderElement;
import org.alban098.engine2j.common.shaders.ShaderProgram;
import org.alban098.engine2j.common.shaders.data.Texture;
import org.alban098.engine2j.common.shaders.data.VertexArrayObject;
import org.alban098.engine2j.common.shaders.data.uniform.Uniform;
import org.alban098.engine2j.common.shaders.data.uniform.UniformMat4;
import org.alban098.engine2j.common.shaders.data.uniform.Uniforms;
import org.alban098.engine2j.entities.Entity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** A higher level abstraction of a Renderer in charge of rendering {@link Entity} */
public abstract class EntityRenderer<T extends Entity> implements Cleanable {

  /** Just a Logger to log events */
  protected static final Logger LOGGER = LoggerFactory.getLogger(EntityRenderer.class);
  /** The {@link VertexArrayObject} used to buffer {@link Entity} for rendering */
  protected final VertexArrayObject vao;
  /** The {@link ShaderProgram} used to render buffered {@link Entity} */
  protected final ShaderProgram shader;
  /**
   * A Map of all registered {@link Entity} classed by {@link Texture} to allow for batch rendering
   * (Work with untextured object because of HashMap null key)
   */
  protected final Map<Texture, Collection<T>> registered = new HashMap<>();

  /**
   * Creates a new Renderer with the attached {@link ShaderProgram}
   *
   * @param shader the {@link ShaderProgram} to attach
   */
  protected EntityRenderer(ShaderProgram shader) {
    this.shader = shader;
    this.vao = shader.createCompatibleVao(8096, true);
    LOGGER.info(
        "Successfully initialized {} with a VAO of capacity 8096 quads",
        getClass().getSimpleName());
  }

  /** Draws all batched entity of the {@link VertexArrayObject} to the screen */
  private void drawVao() {
    vao.drawBatched();
  }

  /**
   * Loads mandatory {@link Uniform}s and call the subsequent {@link
   * EntityRenderer#loadAdditionalUniforms(Window, Camera)}
   *
   * @param window the {@link Window} to render into
   * @param camera the {@link Camera} to render from
   */
  private void loadUniforms(Window window, Camera camera) {
    shader.getUniform(Uniforms.VIEW_MATRIX, UniformMat4.class).load(camera.getViewMatrix());
    shader
        .getUniform(Uniforms.PROJECTION_MATRIX, UniformMat4.class)
        .load(camera.getProjectionMatrix());
    loadAdditionalUniforms(window, camera);
  }

  /**
   * Renders all registered element to the screen
   *
   * @param window the {@link Window} to render into
   * @param camera the {@link Camera} to render from
   */
  public final void render(Window window, Camera camera) {
    shader.bind();
    glActiveTexture(GL_TEXTURE0);
    loadUniforms(window, camera);

    for (Map.Entry<Texture, Collection<T>> entry : registered.entrySet()) {
      // Texture binding
      if (entry.getKey() != null) {
        entry.getKey().bind();
      }
      for (T object : entry.getValue()) {
        if (!vao.batch(object)) {
          // If the VAO is full, draw it and start a new batch
          drawVao();
          vao.batch(object);
        }
      }
      drawVao();
    }
    shader.unbind();
  }

  /**
   * Registers an {@link Entity} to be rendered each frame until unregistered
   *
   * @param object the {@link Entity} to register
   */
  public void register(T object) {
    RenderElement renderable = object.getRenderable();
    if (renderable != null) {
      registered.computeIfAbsent(renderable.getTexture(), t -> new HashSet<>());
      registered.get(renderable.getTexture()).add(object);
      LOGGER.debug("Registered an object of type [{}]", object.getClass().getName());
    } else {
      LOGGER.warn(
          "Trying to register an object of type [{}] that has no RenderableComponent attached",
          object.getClass().getName());
    }
  }

  /**
   * Unregisters an {@link Entity} to no longer be rendered each frame
   *
   * @param object the {@link Entity} to unregister
   */
  public void unregister(T object) {
    RenderElement renderable = object.getRenderable();
    if (renderable != null) {
      Collection<T> list = registered.get(renderable.getTexture());
      if (list.remove(object)) {
        if (list.isEmpty()) {
          registered.remove(renderable.getTexture());
        }
        LOGGER.debug("Unregistered an object of type [{}]", object.getClass().getName());
      } else {
        LOGGER.warn(
            "Trying to unregister an object of type [{}] that is not registered",
            object.getClass().getName());
      }
    } else {
      LOGGER.warn(
          "Trying to unregister an object of type [{}] that is not registered",
          object.getClass().getName());
    }
  }

  /**
   * Clears this Renderer from RAM and VRAM by clearing {@link VertexArrayObject} and {@link
   * ShaderProgram}
   */
  @Override
  public void cleanUp() {
    vao.cleanUp();
    shader.cleanUp();
  }

  /**
   * Loads all additional {@link Uniform}s if necessary for derived classes
   *
   * @param window the {@link Window} to render into
   * @param camera the {@link Camera} to render from
   */
  protected abstract void loadAdditionalUniforms(Window window, Camera camera);
}
