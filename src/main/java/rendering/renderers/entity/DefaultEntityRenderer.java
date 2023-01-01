/*
 * Copyright (c) 2022-2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering.renderers.entity;

import org.joml.Matrix4f;
import rendering.ILogic;
import rendering.Window;
import rendering.entities.Entity;
import rendering.shaders.ShaderAttribute;
import rendering.shaders.ShaderProgram;
import rendering.shaders.uniform.Uniform;
import rendering.shaders.uniform.UniformMat4;
import rendering.shaders.uniform.Uniforms;

public class DefaultEntityRenderer extends EntityRenderer<Entity> {

  public DefaultEntityRenderer() {
    super(
        new ShaderProgram(
            "src/main/resources/shaders/entity/entity.vert",
            "src/main/resources/shaders/entity/entity.geom",
            "src/main/resources/shaders/entity/entity.frag",
            new ShaderAttribute[0],
            new Uniform[] {
              new UniformMat4(Uniforms.VIEW_MATRIX.getName(), new Matrix4f().identity()),
              new UniformMat4(Uniforms.PROJECTION_MATRIX.getName(), new Matrix4f().identity())
            }));
  }

  @Override
  public void loadAdditionalUniforms(Window window, ILogic logic) {}

  @Override
  public void cleanUp() {
    super.cleanUp();
  }
}
