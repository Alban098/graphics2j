/*
 * Copyright (c) 2022-2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package org.alban098.engine2j.core.renderers.entities;

import org.alban098.engine2j.core.shaders.data.uniform.Uniform;
import org.alban098.engine2j.core.shaders.data.uniform.UniformMat4;
import org.alban098.engine2j.core.shaders.data.uniform.Uniforms;
import org.alban098.engine2j.core.Scene;
import org.alban098.engine2j.core.Window;
import org.alban098.engine2j.core.internal.InternalResources;
import org.alban098.engine2j.core.objects.entities.Entity;
import org.alban098.engine2j.core.shaders.ShaderAttribute;
import org.alban098.engine2j.core.shaders.ShaderProgram;
import org.joml.Matrix4f;

/**
 * A Concrete implementation of {@link EntityRenderer} used as the default one when none are
 * provided
 */
public final class DefaultEntityRenderer extends EntityRenderer<Entity> {

  /**
   * Creates a new DefaultEntityRenderer with the default {@link ShaderProgram}
   */
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
   * Loads all additional {@link Uniform}s if necessary
   * for derived classes
   *
   * @param window the {@link Window} to render into
   * @param scene the {@link Scene} to render
   */
  @Override
  protected void loadAdditionalUniforms(Window window, Scene scene) {}
}
