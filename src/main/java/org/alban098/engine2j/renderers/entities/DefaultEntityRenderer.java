/*
 * Copyright (c) 2022-2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package org.alban098.engine2j.renderers.entities;

import org.alban098.engine2j.core.Scene;
import org.alban098.engine2j.core.Window;
import org.alban098.engine2j.objects.entities.Entity;
import org.alban098.engine2j.shaders.ShaderAttribute;
import org.alban098.engine2j.shaders.ShaderProgram;
import org.alban098.engine2j.shaders.data.uniform.Uniform;
import org.alban098.engine2j.shaders.data.uniform.UniformMat4;
import org.alban098.engine2j.shaders.data.uniform.Uniforms;
import org.joml.Matrix4f;

/**
 * A Concrete implementation of {@link EntityRenderer} used as the default one when none are
 * provided
 */
public final class DefaultEntityRenderer extends EntityRenderer<Entity> {

  /**
   * Creates a new DefaultEntityRenderer with the default {@link ShaderProgram}
   *
   * <ul>
   *   <li>resources/shaders/entity/entity.vert
   *   <li>resources/shaders/entity/entity.geom
   *   <li>resources/shaders/entity/entity.frag
   * </ul>
   */
  public DefaultEntityRenderer() {
    super(
        new ShaderProgram(
            "resources/shaders/entity/entity.vert",
            "resources/shaders/entity/entity.geom",
            "resources/shaders/entity/entity.frag",
            new ShaderAttribute[0],
            new Uniform[] {
              new UniformMat4(Uniforms.VIEW_MATRIX, new Matrix4f().identity()),
              new UniformMat4(Uniforms.PROJECTION_MATRIX, new Matrix4f().identity())
            }));
  }

  /**
   * Loads all additional {@link org.alban098.engine2j.shaders.data.uniform.Uniform}s if necessary
   * for derived classes
   *
   * @param window the {@link Window} to render into
   * @param scene the {@link Scene} to render
   */
  @Override
  protected void loadAdditionalUniforms(Window window, Scene scene) {}
}
