/*
 * Copyright (c) 2022-2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package example.renderer;

import example.entities.ExampleColoredEntity;
import java.io.File;
import org.alban098.engine2j.core.Scene;
import org.alban098.engine2j.core.Window;
import org.alban098.engine2j.renderers.entities.EntityRenderer;
import org.alban098.engine2j.shaders.ShaderAttribute;
import org.alban098.engine2j.shaders.ShaderAttributes;
import org.alban098.engine2j.shaders.ShaderProgram;
import org.alban098.engine2j.shaders.data.uniform.Uniform;
import org.alban098.engine2j.shaders.data.uniform.UniformMat4;
import org.alban098.engine2j.shaders.data.uniform.Uniforms;
import org.joml.Matrix4f;

public class ExampleColoredEntityRenderer extends EntityRenderer<ExampleColoredEntity> {

  public ExampleColoredEntityRenderer() {
    super(
        new ShaderProgram(
            new File("engine2j/example/shaders/example.vert"),
            new File("engine2j/example/shaders/example.geom"),
            new File("engine2j/example/shaders/example.frag"),
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
