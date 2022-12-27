/*
 * Copyright (c) 2022, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package simulation.renderer;

import org.joml.Matrix4f;
import org.joml.Vector4f;
import rendering.ILogic;
import rendering.Window;
import rendering.renderers.entity.EntityRenderer;
import rendering.shaders.ShaderAttribute;
import rendering.shaders.ShaderAttributes;
import rendering.shaders.ShaderProgram;
import rendering.shaders.uniform.Uniform;
import rendering.shaders.uniform.UniformMat4;
import rendering.shaders.uniform.Uniforms;
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
              new UniformMat4(Uniforms.VIEW_MATRIX.getName(), new Matrix4f().identity()),
              new UniformMat4(Uniforms.PROJECTION_MATRIX.getName(), new Matrix4f().identity())
            }),
        new Vector4f(0, 1, 0, 1));
  }

  @Override
  public void loadAdditionalUniforms(Window window, ILogic logic) {}

  @Override
  public void cleanUp() {}
}
