/*
 * Copyright (c) 2022, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering.renderers.interfaces;

import org.joml.Vector4f;
import rendering.Window;
import rendering.interfaces.UserInterface;
import rendering.scene.Camera;
import rendering.scene.Scene;
import rendering.shaders.ShaderAttribute;
import rendering.shaders.ShaderAttributes;
import rendering.shaders.ShaderProgram;
import rendering.shaders.uniform.Uniform;

public class DefaultInterfaceRenderer extends InterfaceRenderer<UserInterface> {

  public DefaultInterfaceRenderer() {
    super(
        new ShaderProgram(
            "src/main/resources/shaders/interface/entity.vert",
            "src/main/resources/shaders/interface/entity.geom",
            "src/main/resources/shaders/interface/entity.frag",
            new ShaderAttribute[] {ShaderAttributes.COLOR_ATTRIBUTE},
            new Uniform[0]),
        new Vector4f(1, 0, 0, 1));
  }

  @Override
  public void loadUniforms(Window window, Camera camera, Scene scene) {}

  @Override
  public void cleanUp() {}
}
