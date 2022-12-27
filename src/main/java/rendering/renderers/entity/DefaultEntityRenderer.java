/*
 * Copyright (c) 2022, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering.renderers.entity;

import org.joml.Matrix4f;
import org.joml.Vector4f;
import rendering.Window;
import rendering.entities.Entity;
import rendering.scene.Camera;
import rendering.scene.Scene;
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
            }),
        new Vector4f(1, 0, 0, 1));
  }

  @Override
  public void loadAdditionalUniforms(Window window, Camera camera, Scene scene) {}

  @Override
  public void cleanUp() {
    super.cleanUp();
  }
}
