/*
 * Copyright (c) 2022-2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package org.alban098.graphics2j.entities.renderers;

import org.alban098.graphics2j.common.Window;
import org.alban098.graphics2j.common.components.Camera;
import org.alban098.graphics2j.common.resources.InternalResources;
import org.alban098.graphics2j.common.shaders.ShaderAttribute;
import org.alban098.graphics2j.common.shaders.ShaderProgram;
import org.alban098.graphics2j.common.shaders.data.uniform.Uniform;
import org.alban098.graphics2j.common.shaders.data.uniform.UniformMat4;
import org.alban098.graphics2j.common.shaders.data.uniform.Uniforms;
import org.alban098.graphics2j.entities.Entity;
import org.joml.Matrix4f;

/**
 * A Concrete implementation of {@link EntityRenderer} used as the default one when none are
 * provided
 */
public final class DefaultEntityRenderer extends EntityRenderer<Entity> {

  /** Creates a new DefaultEntityRenderer with the default {@link ShaderProgram} */
  public DefaultEntityRenderer() {
    super(
        new ShaderProgram(
            InternalResources.ENTITY_VERTEX,
            InternalResources.ENTITY_GEOMETRY,
            InternalResources.ENTITY_FRAGMENT,
            new ShaderAttribute[0],
            new Uniform[] {
              new UniformMat4(Uniforms.VIEW_MATRIX, new Matrix4f().identity()),
              new UniformMat4(Uniforms.PROJECTION_MATRIX, new Matrix4f().identity())
            }));
  }

  /**
   * Loads all additional {@link Uniform}s if necessary for derived classes
   *
   * @param window the {@link Window} to render into
   * @param camera the {@link Camera} to render from
   */
  @Override
  protected void loadAdditionalUniforms(Window window, Camera camera) {}
}
