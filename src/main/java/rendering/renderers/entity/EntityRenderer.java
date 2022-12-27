/*
 * Copyright (c) 2022, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering.renderers.entity;

import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;

import java.util.*;
import org.joml.Vector4f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rendering.Texture;
import rendering.Window;
import rendering.data.VAO;
import rendering.entities.Entity;
import rendering.entities.component.RenderableComponent;
import rendering.renderers.Renderer;
import rendering.renderers.RenderingMode;
import rendering.scene.Camera;
import rendering.scene.Scene;
import rendering.shaders.ShaderProgram;
import rendering.shaders.uniform.UniformBoolean;
import rendering.shaders.uniform.UniformMat4;
import rendering.shaders.uniform.UniformVec4;
import rendering.shaders.uniform.Uniforms;

public abstract class EntityRenderer<T extends Entity> implements Renderer<T> {
  private static final Logger LOGGER = LoggerFactory.getLogger(EntityRenderer.class);
  protected final VAO vao;
  protected final ShaderProgram shader;
  // Work with untextured object because of Hashmap null key
  protected final Map<Texture, List<T>> registered = new HashMap<>();
  protected final Vector4f wireframeColor;
  protected int drawCalls = 0;
  protected int nbObjects = 0;

  protected EntityRenderer(ShaderProgram shader, Vector4f wireframeColor) {
    this.shader = shader;
    this.wireframeColor = wireframeColor;
    this.vao = shader.createCompatibleVao(8096);
  }

  protected final int drawVao() {
    vao.draw();
    return 1;
  }

  protected void loadUniforms(Window window, Camera camera, Scene scene, RenderingMode mode) {
    shader.getUniform(Uniforms.VIEW_MATRIX, UniformMat4.class).loadMatrix(camera.getViewMatrix());
    shader
        .getUniform(Uniforms.PROJECTION_MATRIX, UniformMat4.class)
        .loadMatrix(camera.getProjectionMatrix());
    shader
        .getUniform(Uniforms.WIREFRAME, UniformBoolean.class)
        .loadBoolean(mode == RenderingMode.WIREFRAME);
    shader.getUniform(Uniforms.WIREFRAME_COLOR, UniformVec4.class).loadVec4(wireframeColor);
    loadAdditionalUniforms(window, camera, scene);
  }

  public final void render(Window window, Camera camera, Scene scene, RenderingMode mode) {
    shader.bind();
    glActiveTexture(GL_TEXTURE0);
    loadUniforms(window, camera, scene, mode);
    drawCalls = 0;

    for (Map.Entry<Texture, List<T>> entry : registered.entrySet()) {
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

  public void unregister(T object) {
    RenderableComponent renderable = object.getRenderable();
    if (renderable != null) {
      List<T> list = registered.get(renderable.getTexture());
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

  public void register(T object) {
    RenderableComponent renderable = object.getRenderable();
    if (renderable != null) {
      registered.computeIfAbsent(renderable.getTexture(), t -> new ArrayList<>());
      registered.get(renderable.getTexture()).add(object);
      nbObjects++;
      LOGGER.debug("Registered an object of type [{}]", object.getClass().getName());
    } else {
      LOGGER.warn(
          "Trying to register an object of type [{}] that has no RenderableComponent attached",
          object.getClass().getName());
    }
  }

  public final Collection<Texture> getTextures() {
    return registered.keySet();
  }

  public final void setWireframeColor(Vector4f wireframeColor) {
    this.wireframeColor.set(wireframeColor);
  }

  public final Vector4f getWireframeColor() {
    return wireframeColor;
  }

  public final int getDrawCalls() {
    return drawCalls;
  }

  public final int getNbObjects() {
    return nbObjects;
  }

  public final VAO getVao() {
    return vao;
  }

  public final ShaderProgram getShader() {
    return shader;
  }

  public abstract void loadAdditionalUniforms(Window window, Camera camera, Scene scene);

  public void cleanUp() {
    vao.cleanUp();
    shader.cleanUp();
  }
}
