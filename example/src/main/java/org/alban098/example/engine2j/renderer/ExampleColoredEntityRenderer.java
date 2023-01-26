/*
 * Copyright (c) 2022-2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package org.alban098.example.engine2j.renderer;

import java.io.File;
import org.alban098.engine2j.core.Scene;
import org.alban098.engine2j.core.Window;
import org.alban098.engine2j.core.renderers.entities.EntityRenderer;
import org.alban098.engine2j.core.shaders.ShaderAttribute;
import org.alban098.engine2j.core.shaders.ShaderAttributes;
import org.alban098.engine2j.core.shaders.ShaderProgram;
import org.alban098.engine2j.core.shaders.data.uniform.Uniform;
import org.alban098.engine2j.core.shaders.data.uniform.UniformMat4;
import org.alban098.engine2j.core.shaders.data.uniform.Uniforms;
import org.alban098.example.engine2j.entities.ExampleColoredEntity;
import org.joml.Matrix4f;

public class ExampleColoredEntityRenderer extends EntityRenderer<ExampleColoredEntity> {

  public ExampleColoredEntityRenderer() {
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
  public void loadAdditionalUniforms(Window window, Scene scene) {}

  @Override
  public void cleanUp() {}
}
