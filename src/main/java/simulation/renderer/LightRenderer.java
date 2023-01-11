/*
 * Copyright (c) 2022-2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package simulation.renderer;

import org.joml.Matrix4f;
import rendering.Window;
import rendering.renderers.entity.EntityRenderer;
import rendering.scene.Scene;
import rendering.shaders.ShaderAttribute;
import rendering.shaders.ShaderAttributes;
import rendering.shaders.ShaderProgram;
import rendering.shaders.data.uniform.Uniform;
import rendering.shaders.data.uniform.UniformMat4;
import rendering.shaders.data.uniform.Uniforms;
import simulation.entities.LightSource;

public class LightRenderer extends EntityRenderer<LightSource> {

  public LightRenderer() {
    super(
        new ShaderProgram(
            "src/main/resources/shaders/light/light.vert",
            "src/main/resources/shaders/light/light.geom",
            "src/main/resources/shaders/light/light.frag",
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
