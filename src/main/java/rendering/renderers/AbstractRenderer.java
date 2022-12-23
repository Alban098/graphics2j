/*
 * Copyright (c) 2022, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering.renderers;

import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;

import java.util.*;
import org.joml.Vector4f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rendering.Texture;
import rendering.Window;
import rendering.data.VAO;
import rendering.entities.component.RenderableComponent;
import rendering.scene.Camera;
import rendering.scene.Scene;
import rendering.shaders.ShaderProgram;
import rendering.shaders.uniform.UniformBoolean;
import rendering.shaders.uniform.UniformVec4;
import rendering.shaders.uniform.Uniforms;

public abstract class AbstractRenderer<T extends Componentable> {
  private static final Logger LOGGER = LoggerFactory.getLogger(AbstractRenderer.class);
  protected final VAO vao;
  protected final ShaderProgram shader;
  // Work with untextured object because of Hashmap null key
  protected final Map<Texture, List<T>> registered = new HashMap<>();
  protected final Vector4f wireframeColor;
  protected int drawCalls = 0;
  protected int nbObjects = 0;

  protected AbstractRenderer(ShaderProgram shader, Vector4f wireframeColor, int maxQuadPerVAO) {
    this.shader = shader;
    this.wireframeColor = wireframeColor;
    this.vao = shader.createCompatibleVao(maxQuadPerVAO);
  }

  protected final int drawVao() {
    vao.draw();
    return 1;
  }

  protected void loadUniformsNative(Window window, Camera camera, Scene scene, RenderingMode mode) {
    ((UniformBoolean) shader.getUniform(Uniforms.WIREFRAME))
        .loadBoolean(mode == RenderingMode.WIREFRAME);
    ((UniformVec4) shader.getUniform(Uniforms.WIREFRAME_COLOR)).loadVec4(wireframeColor);
    loadUniforms(window, camera, scene);
  }

  public final void render() {
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

  private void preRender() {
    shader.bind();
    glActiveTexture(GL_TEXTURE0);
  }

  private void postRender() {
    shader.unbind();
  }

  final void renderNative(Window window, Camera camera, Scene scene, RenderingMode mode) {
    preRender();
    loadUniformsNative(window, camera, scene, mode);
    render();
    postRender();
  }

  final void cleanUpNative() {
    vao.cleanUp();
    shader.cleanUp();
    cleanUp();
  }

  public final void unregister(T object) {
    RenderableComponent renderable = object.getComponent(RenderableComponent.class);
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

  public final void register(T object) {
    RenderableComponent renderable = object.getComponent(RenderableComponent.class);
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

  public abstract void loadUniforms(Window window, Camera camera, Scene scene);

  public abstract void cleanUp();
}
