/*
 * Copyright (c) 2022, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering.renderers.interfaces;

import org.joml.Vector4f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rendering.Window;
import rendering.interfaces.UserInterface;
import rendering.renderers.AbstractRenderer;
import rendering.scene.Camera;
import rendering.scene.Scene;
import rendering.shaders.ShaderProgram;

public abstract class InterfaceRenderer<T extends UserInterface> extends AbstractRenderer<T> {

  private static final Logger LOGGER = LoggerFactory.getLogger(InterfaceRenderer.class);

  public InterfaceRenderer(ShaderProgram shader, Vector4f wireframeColor) {
    super(shader, wireframeColor, 64);
  }

  @Override
  public void loadUniforms(Window window, Camera camera, Scene scene) {}
}
