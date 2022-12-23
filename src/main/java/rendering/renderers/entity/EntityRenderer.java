/*
 * Copyright (c) 2022, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering.renderers.entity;

import java.util.*;
import org.joml.Vector4f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rendering.Window;
import rendering.entities.Entity;
import rendering.renderers.AbstractRenderer;
import rendering.renderers.RenderingMode;
import rendering.scene.Camera;
import rendering.scene.Scene;
import rendering.shaders.ShaderProgram;
import rendering.shaders.uniform.UniformMat4;
import rendering.shaders.uniform.Uniforms;

public abstract class EntityRenderer<T extends Entity> extends AbstractRenderer<T> {

  private static final Logger LOGGER = LoggerFactory.getLogger(EntityRenderer.class);

  // Work with untextured object because of Hashmap null key
  protected EntityRenderer(ShaderProgram shader, Vector4f wireframeColor) {
    super(shader, wireframeColor, 8096);
  }

  protected final void loadUniformsNative(
      Window window, Camera camera, Scene scene, RenderingMode mode) {
    ((UniformMat4) shader.getUniform(Uniforms.VIEW_MATRIX)).loadMatrix(camera.getViewMatrix());
    ((UniformMat4) shader.getUniform(Uniforms.PROJECTION_MATRIX))
        .loadMatrix(camera.getProjectionMatrix());
    super.loadUniformsNative(window, camera, scene, mode);
  }
}
