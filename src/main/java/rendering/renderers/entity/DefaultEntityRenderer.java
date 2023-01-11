/*
 * Copyright (c) 2022-2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering.renderers.entity;

import org.joml.Matrix4f;
import rendering.Window;
import rendering.scene.Scene;
import rendering.scene.entities.Entity;
import rendering.shaders.ShaderAttribute;
import rendering.shaders.ShaderProgram;
import rendering.shaders.data.uniform.Uniform;
import rendering.shaders.data.uniform.UniformMat4;
import rendering.shaders.data.uniform.Uniforms;

/**
 * A Concrete implementation of {@link EntityRenderer} used as the default one when none are
 * provided
 */
public final class DefaultEntityRenderer extends EntityRenderer<Entity> {

  /**
   * Creates a new DefaultEntityRenderer with the default {@link ShaderProgram}
   *
   * <ul>
   *   <li>src/main/resources/shaders/entity/entity.vert
   *   <li>src/main/resources/shaders/entity/entity.geom
   *   <li>src/main/resources/shaders/entity/entity.frag
   * </ul>
   */
  public DefaultEntityRenderer() {
    super(
        new ShaderProgram(
            "src/main/resources/shaders/entity/entity.vert",
            "src/main/resources/shaders/entity/entity.geom",
            "src/main/resources/shaders/entity/entity.frag",
            new ShaderAttribute[0],
            new Uniform[] {
              new UniformMat4(Uniforms.VIEW_MATRIX, new Matrix4f().identity()),
              new UniformMat4(Uniforms.PROJECTION_MATRIX, new Matrix4f().identity())
            }));
  }

  /**
   * Loads all additional {@link rendering.shaders.data.uniform.Uniform}s if necessary for derived
   * classes
   *
   * @param window the {@link Window} to render into
   * @param scene the {@link Scene} to render
   */
  @Override
  protected void loadAdditionalUniforms(Window window, Scene scene) {}
}
