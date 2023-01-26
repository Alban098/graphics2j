/*
 * Copyright (c) 2022-2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package org.alban098.engine2j.core.renderers.entities;

import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;

import java.util.*;
import org.alban098.engine2j.core.Scene;
import org.alban098.engine2j.core.Window;
import org.alban098.engine2j.core.objects.Camera;
import org.alban098.engine2j.core.objects.entities.Entity;
import org.alban098.engine2j.core.objects.entities.component.RenderableComponent;
import org.alban098.engine2j.core.renderers.RegisterableRenderer;
import org.alban098.engine2j.core.shaders.ShaderProgram;
import org.alban098.engine2j.core.shaders.data.Texture;
import org.alban098.engine2j.core.shaders.data.VertexArrayObject;
import org.alban098.engine2j.core.shaders.data.uniform.UniformMat4;
import org.alban098.engine2j.core.shaders.data.uniform.Uniforms;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A higher level abstraction of a {@link RegisterableRenderer} in charge of rendering {@link
 * Entity}
 */
public abstract class EntityRenderer<T extends Entity> implements RegisterableRenderer<T> {

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
  /** The number of drawcalls during the last frame */
  protected int drawCalls = 0;
  /** The number of {@link Entity} rendered during the last frame */
  protected int nbObjects = 0;

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

  /**
   * Draws all batched entity of the {@link VertexArrayObject} to the screen
   *
   * @return the number of drawcalls (always 1)
   */
  private int drawVao() {
    vao.drawBatched();
    return 1;
  }

  /**
   * Loads mandatory {@link org.alban098.engine2j.core.shaders.data.uniform.Uniform}s and call the
   * subsequent {@link EntityRenderer#loadAdditionalUniforms(Window, Scene)}
   *
   * @param window the {@link Window} to render into
   * @param scene the {@link Scene} to render, mandatory for {@link Camera} state
   */
  private void loadUniforms(Window window, Scene scene) {
    shader
        .getUniform(Uniforms.VIEW_MATRIX, UniformMat4.class)
        .load(scene.getCamera().getViewMatrix());
    shader
        .getUniform(Uniforms.PROJECTION_MATRIX, UniformMat4.class)
        .load(scene.getCamera().getProjectionMatrix());
    loadAdditionalUniforms(window, scene);
  }

  /**
   * Renders all registered element to the screen
   *
   * @param window the {@link Window} to render into
   * @param scene the {@link Scene} to render (only used for {@link Camera} placement as all Object
   *     have been previously registered)
   */
  public final void render(Window window, Scene scene) {
    shader.bind();
    glActiveTexture(GL_TEXTURE0);
    loadUniforms(window, scene);
    drawCalls = 0;

    for (Map.Entry<Texture, Collection<T>> entry : registered.entrySet()) {
      // Texture binding
      if (entry.getKey() != null) {
        entry.getKey().bind();
      }
      for (T object : entry.getValue()) {
        if (!vao.batch(object)) {
          // If the VAO is full, draw it and start a new batch
          drawCalls += drawVao();
          vao.batch(object);
        }
      }
      drawCalls += drawVao();
    }
    shader.unbind();
  }

  /**
   * Registers an {@link Entity} to be rendered each frame until unregistered
   *
   * @param object the {@link Entity} to register
   */
  public void register(T object) {
    RenderableComponent renderable = object.getRenderable();
    if (renderable != null) {
      registered.computeIfAbsent(renderable.getTexture(), t -> new HashSet<>());
      registered.get(renderable.getTexture()).add(object);
      nbObjects++;
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
    RenderableComponent renderable = object.getRenderable();
    if (renderable != null) {
      Collection<T> list = registered.get(renderable.getTexture());
      if (list.remove(object)) {
        nbObjects--;
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
   * Returns a Collection of all {@link Texture}s the Renderer can use during the rendering of a
   * frame
   *
   * @return a Collection of all {@link Texture}s the Renderer can use during the rendering of a
   *     frame
   */
  public final Collection<Texture> getTextures() {
    return registered.keySet();
  }

  /**
   * Returns the number of drawcalls to the GPU that occurred during the last frame, emanating from
   * this Renderer
   *
   * @return the number of drawcalls to the GPU that occurred during the last frame, emanating from
   *     this Renderer
   */
  public final int getDrawCalls() {
    return drawCalls;
  }

  /**
   * Returns the number of Objects rendered by this Renderer during the last frame
   *
   * @return the number of Objects rendered by this Renderer during the last frame
   */
  public final int getNbObjects() {
    return nbObjects;
  }

  /**
   * Returns a Collection of all {@link VertexArrayObject}s used by this Renderer
   *
   * @return a Collection of all {@link VertexArrayObject}s used by this Renderer
   */
  public final Collection<VertexArrayObject> getVaos() {
    return Collections.singleton(vao);
  }

  /**
   * Returns a Collection of all {@link ShaderProgram}s used by this Renderer
   *
   * @return a Collection of all {@link ShaderProgram}s used by this Renderer
   */
  public final Collection<ShaderProgram> getShaders() {
    return Collections.singleton(shader);
  }

  /**
   * Clears this Renderer from RAM and VRAM by clearing {@link VertexArrayObject} and {@link
   * ShaderProgram}
   */
  public void cleanUp() {
    vao.cleanUp();
    shader.cleanUp();
  }

  /**
   * Loads all additional {@link org.alban098.engine2j.core.shaders.data.uniform.Uniform}s if
   * necessary for derived classes
   *
   * @param window the {@link Window} to render into
   * @param scene the {@link Scene} to render
   */
  protected abstract void loadAdditionalUniforms(Window window, Scene scene);
}
