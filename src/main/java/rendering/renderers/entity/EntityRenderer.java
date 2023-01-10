/*
 * Copyright (c) 2022-2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering.renderers.entity;

import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;

import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rendering.Texture;
import rendering.Window;
import rendering.renderers.RegisterableRenderer;
import rendering.scene.Scene;
import rendering.scene.entities.Entity;
import rendering.scene.entities.component.RenderableComponent;
import rendering.shaders.ShaderProgram;
import rendering.shaders.data.VertexArrayObject;
import rendering.shaders.data.uniform.UniformMat4;
import rendering.shaders.data.uniform.Uniforms;

public abstract class EntityRenderer<T extends Entity> implements RegisterableRenderer<T> {

  private static final Logger LOGGER = LoggerFactory.getLogger(EntityRenderer.class);
  protected final VertexArrayObject vao;
  protected final ShaderProgram shader;
  // Work with untextured object because of Hashmap null key
  protected final Map<Texture, Collection<T>> registered = new HashMap<>();
  protected int drawCalls = 0;
  protected int nbObjects = 0;

  protected EntityRenderer(ShaderProgram shader) {
    this.shader = shader;
    this.vao = shader.createCompatibleVao(8096, true);
  }

  protected final int drawVao() {
    vao.drawBatched();
    return 1;
  }

  protected void loadUniforms(Window window, Scene scene) {
    shader
        .getUniform(Uniforms.VIEW_MATRIX, UniformMat4.class)
        .load(scene.getCamera().getViewMatrix());
    shader
        .getUniform(Uniforms.PROJECTION_MATRIX, UniformMat4.class)
        .load(scene.getCamera().getProjectionMatrix());
    loadAdditionalUniforms(window, scene);
  }

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
          drawVao();
          vao.batch(object);
        }
      }
      drawCalls += drawVao();
    }
    shader.unbind();
  }

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
        LOGGER.debug(
            "Trying to unregister an object of type [{}] that is not registered",
            object.getClass().getName());
      }
    } else {
      LOGGER.debug(
          "Trying to unregister an object of type [{}] that is not registered",
          object.getClass().getName());
    }
  }

  public final Collection<Texture> getTextures() {
    return registered.keySet();
  }

  public final int getDrawCalls() {
    return drawCalls;
  }

  public final int getNbObjects() {
    return nbObjects;
  }

  public final Collection<VertexArrayObject> getVaos() {
    return Collections.singleton(vao);
  }

  public final Collection<ShaderProgram> getShaders() {
    return Collections.singleton(shader);
  }

  public abstract void loadAdditionalUniforms(Window window, Scene scene);

  public void cleanUp() {
    vao.cleanUp();
    shader.cleanUp();
  }
}
