/*
 * Copyright (c) 2022, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package simulation.renderer;

import org.joml.Vector4f;
import rendering.Window;
import rendering.entities.Entity;
import rendering.renderers.Renderer;
import rendering.scene.Camera;
import rendering.scene.Scene;
import rendering.shaders.ShaderAttribute;
import rendering.shaders.ShaderProgram;
import rendering.shaders.uniform.Uniform;

public class EntityRenderer extends Renderer<Entity> {

  public EntityRenderer() {
    super(
        new ShaderProgram(
            "src/main/resources/shaders/entity/entity.vert",
            "src/main/resources/shaders/entity/entity.geom",
            "src/main/resources/shaders/entity/entity.frag",
            new ShaderAttribute[0],
            new Uniform[0]),
        new Vector4f(1, 0, 0, 1));
  }

  @Override
  public void loadUniforms(Window window, Camera camera, Scene scene) {}

  @Override
  public void cleanUp() {}
}
