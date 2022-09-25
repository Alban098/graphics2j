/*
 * Copyright (c) 2022, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering;

import org.joml.Matrix4f;
import rendering.entities.Entity;
import rendering.entities.EntityRenderer;
import rendering.scene.Camera;
import rendering.shaders.ShaderAttribute;
import rendering.shaders.ShaderProgram;
import rendering.shaders.uniform.Uniform;
import rendering.shaders.uniform.UniformMat4;

public class Renderer {

  private EntityRenderer entityRenderer;

  public void init() {
    ShaderProgram entityShader =
        new ShaderProgram(
            "src/main/resources/shaders/vertex.glsl",
            "src/main/resources/shaders/fragment.glsl",
            new ShaderAttribute[0],
            new Uniform[] {
              new UniformMat4("viewMatrix", new Matrix4f().identity()),
              new UniformMat4("projectionMatrix", new Matrix4f().identity())
            });
    entityRenderer = new EntityRenderer(entityShader);
  }

  public void render(Window window, Camera camera) {
    entityRenderer.render(camera);
  }

  public void cleanUp() {
    entityRenderer.cleanUp();
  }

  public void register(Entity entity) {
    entityRenderer.register(entity);
  }

  public void unregister(Entity entity) {
    entityRenderer.unregister(entity);
  }
}
