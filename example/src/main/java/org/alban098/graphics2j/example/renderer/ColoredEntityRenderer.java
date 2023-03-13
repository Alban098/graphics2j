/*
 * Copyright (c) 2022-2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package org.alban098.graphics2j.example.renderer;

import java.io.File;
import org.alban098.graphics2j.common.Window;
import org.alban098.graphics2j.common.components.Camera;
import org.alban098.graphics2j.common.shaders.ShaderAttribute;
import org.alban098.graphics2j.common.shaders.ShaderAttributes;
import org.alban098.graphics2j.common.shaders.ShaderProgram;
import org.alban098.graphics2j.common.shaders.data.uniform.Uniform;
import org.alban098.graphics2j.common.shaders.data.uniform.UniformMat4;
import org.alban098.graphics2j.common.shaders.data.uniform.Uniforms;
import org.alban098.graphics2j.entities.renderers.EntityRenderer;
import org.alban098.graphics2j.example.entities.ColoredEntity;
import org.joml.Matrix4f;

public class ColoredEntityRenderer extends EntityRenderer<ColoredEntity> {

  public ColoredEntityRenderer() {
    super(
        new ShaderProgram(
            new File("assets/shaders/example.vert"),
            new File("assets/shaders/example.geom"),
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
