/*
 * Copyright (c) 2022, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package simulation.renderer;

import rendering.Window;
import rendering.renderers.Renderer;
import rendering.scene.Camera;
import rendering.scene.Scene;
import rendering.shaders.ShaderAttribute;
import rendering.shaders.ShaderProgram;
import rendering.shaders.uniform.Uniform;
import simulation.entities.LightSource;

public class LightRenderer extends Renderer<LightSource> {

  public LightRenderer() {
    super(
        new ShaderProgram(
            "src/main/resources/shaders/light/light.vert",
            "src/main/resources/shaders/light/light.geom",
            "src/main/resources/shaders/light/light.frag",
            new ShaderAttribute[] {ShaderAttributes.COLOR_ATTRIBUTE},
            new Uniform[0]));
  }

  @Override
  public void loadUniforms(Window window, Camera camera, Scene scene) {}

  @Override
  public void cleanUp() {}
}
