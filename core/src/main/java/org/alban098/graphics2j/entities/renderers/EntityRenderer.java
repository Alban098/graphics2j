/*
 * Copyright (c) 2022-2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package org.alban098.graphics2j.entities.renderers;

import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;

import java.util.*;
import org.alban098.graphics2j.common.Renderer;
import org.alban098.graphics2j.common.Window;
import org.alban098.graphics2j.common.components.Camera;
import org.alban098.graphics2j.common.components.RenderElement;
import org.alban098.graphics2j.common.shaders.ShaderProgram;
import org.alban098.graphics2j.common.shaders.data.Texture;
import org.alban098.graphics2j.common.shaders.data.VertexArrayObject;
import org.alban098.graphics2j.common.shaders.data.uniform.Uniform;
import org.alban098.graphics2j.common.shaders.data.uniform.UniformMat4;
import org.alban098.graphics2j.common.shaders.data.uniform.Uniforms;
import org.alban098.graphics2j.entities.Entity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** A higher level abstraction of a Renderer in charge of rendering {@link Entity} */
public abstract class EntityRenderer<T extends Entity> implements Renderer {

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
  /** The number of drawcalls during the last frame */
  protected int drawCalls = 0;
  /** The number of {@link Entity} rendered during the last frame */
  protected int nbObjects = 0;

  protected long renderingTimeNs = 0;
  private final Map<ShaderProgram, Double> shaderTimes = new HashMap<>();

  /**
   * Creates a new Renderer with the attached {@link ShaderProgram}
   *
   * @param shader the {@link ShaderProgram} to attach
   */
  protected EntityRenderer(ShaderProgram shader) {
    this.shader = shader;
    this.vao = shader.createCompatibleVao(8096, true);
    shaderTimes.put(shader, 0d);
    LOGGER.info(
        "Successfully initialized {} with a VAO of capacity 8096 quads",
        getClass().getSimpleName());
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
    renderingTimeNs = System.nanoTime();
    shader.bind();
    glActiveTexture(GL_TEXTURE0);
    loadUniforms(window, camera);
    drawCalls = 0;

    for (Map.Entry<Texture, Collection<T>> entry : registered.entrySet()) {
      // Texture binding
      if (entry.getKey() != null) {
        entry.getKey().bind();
      }
      for (T object : entry.getValue()) {
        // Apply all non applied transform modifications
        object.getTransform().commit();
        if (camera.isInsidePseudoViewport(
            object.getTransform().getDisplacement(), object.getTransform().getScale())) {
          if (!vao.batch(object)) {
            // If the VAO is full, draw it and start a new batch
            vao.drawBatched();
            drawCalls++;
            vao.batch(object);
          }
        }
      }
      vao.drawBatched();
      drawCalls++;
    }
    shader.unbind();
    renderingTimeNs = System.nanoTime() - renderingTimeNs;
    shaderTimes.put(shader, getRenderingTime());
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
      if (registered.get(renderable.getTexture()).add(object)) {
        nbObjects++;
        LOGGER.debug("Registered an object of type [{}]", object.getClass().getName());
      }
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

  /**
   * Returns a Collection of all {@link Texture}s the Renderer can use during the rendering of a
   * frame
   *
   * @return a Collection of all {@link Texture}s the Renderer can use during the rendering of a
   *     frame
   */
  @Override
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
  @Override
  public final int getDrawCalls() {
    return drawCalls;
  }

  /**
   * Returns the number of Objects rendered by this Renderer during the last frame
   *
   * @return the number of Objects rendered by this Renderer during the last frame
   */
  @Override
  public final int getNbObjects() {
    return nbObjects;
  }

  /**
   * Returns the {@link VertexArrayObject}s used by this Renderer
   *
   * @return a the {@link VertexArrayObject}s used by this Renderer
   */
  @Override
  public final VertexArrayObject getVao() {
    return vao;
  }

  /**
   * Returns the number of {@link ShaderProgram#bind()} calls during this rendering pass
   *
   * @return the number of {@link ShaderProgram#bind()} calls during this rendering pass
   */
  @Override
  public final int getShaderBoundCount() {
    return 1;
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

  @Override
  public Map<ShaderProgram, Double> getShaderTimes() {
    return shaderTimes;
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
}
