/*
 * Copyright (c) 2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package org.alban098.physics2j.debug;

import java.io.File;
import org.alban098.graphics2j.common.Window;
import org.alban098.graphics2j.common.components.Camera;
import org.alban098.graphics2j.common.shaders.ShaderAttribute;
import org.alban098.graphics2j.common.shaders.ShaderAttributes;
import org.alban098.graphics2j.common.shaders.ShaderProgram;
import org.alban098.graphics2j.common.shaders.data.uniform.Uniform;
import org.alban098.graphics2j.common.shaders.data.uniform.UniformMat4;
import org.alban098.graphics2j.common.shaders.data.uniform.Uniforms;
import org.alban098.graphics2j.objects.renderers.AbstractRenderer;
import org.alban098.physics2j.QuadTree;
import org.joml.Matrix4f;

public class QuadTreeRenderer extends AbstractRenderer<QuadTree.Node<?>> {

  public QuadTreeRenderer() {
    super(
        new ShaderProgram(
            "QuadTree Shader",
            new File("assets/shaders/example.vert"),
            new File("assets/shaders/example.frag"),
            new ShaderAttribute[] {ShaderAttributes.COLOR_ATTRIBUTE},
            new Uniform[] {
              new UniformMat4(Uniforms.VIEW_MATRIX, new Matrix4f().identity()),
              new UniformMat4(Uniforms.PROJECTION_MATRIX, new Matrix4f().identity())
            }));
  }

  @Override
  protected void loadAdditionalUniforms(Window window, Camera camera) {}
}
