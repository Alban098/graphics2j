/*
 * Copyright (c) 2022-2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package org.alban098.graphics2j.objects.renderers;

import org.alban098.graphics2j.common.Renderable;
import org.alban098.graphics2j.common.Window;
import org.alban098.graphics2j.common.components.Camera;
import org.alban098.graphics2j.common.resources.InternalResources;
import org.alban098.graphics2j.common.shaders.ShaderAttribute;
import org.alban098.graphics2j.common.shaders.ShaderAttributes;
import org.alban098.graphics2j.common.shaders.ShaderProgram;
import org.alban098.graphics2j.common.shaders.data.model.Primitive;
import org.alban098.graphics2j.common.shaders.data.uniform.Uniform;
import org.alban098.graphics2j.common.shaders.data.uniform.UniformMat4;
import org.alban098.graphics2j.common.shaders.data.uniform.Uniforms;
import org.joml.Matrix4f;

/**
 * A Concrete implementation of {@link AbstractRenderer} used as the default one when none are
 * provided
 */
public final class DefaultVertexRenderer extends AbstractRenderer<Renderable> {

  /** Creates a new DefaultRenderer with the default {@link ShaderProgram} */
  public DefaultVertexRenderer() {
    super(
        new ShaderProgram(
            "Default Shader",
            InternalResources.DEFAULT_VERTEX_VERTEX_MODE,
            InternalResources.DEFAULT_FRAGMENT,
            new ShaderAttribute[] {ShaderAttributes.UV},
            new Uniform[] {
              new UniformMat4(Uniforms.VIEW_MATRIX, new Matrix4f().identity()),
              new UniformMat4(Uniforms.PROJECTION_MATRIX, new Matrix4f().identity())
            }),
        Primitive.TRIANGLES);
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
