/*
 * Copyright (c) 2022, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package simulation.renderer;

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
            "src/main/resources/shaders/entity/vertex.glsl",
            "src/main/resources/shaders/entity/fragment.glsl",
            new ShaderAttribute[0],
            new Uniform[0]));
  }

  @Override
  public void loadUniforms(Window window, Camera camera, Scene scene) {}

  @Override
  public void cleanUp() {}
}
