/*
 * Copyright (c) 2022, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering.entities;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rendering.Texture;
import rendering.data.Vao;
import rendering.scene.Camera;
import rendering.shaders.ShaderProgram;
import rendering.shaders.uniform.UniformMat4;

public class EntityRenderer {

  private static final Logger LOGGER = LoggerFactory.getLogger(EntityRenderer.class);

  private final Vao vao;

  private final ShaderProgram shader;
  private final Map<Texture, List<Entity>> registeredEntities = new HashMap<>();

  private int drawCall = 0;

  public EntityRenderer(ShaderProgram shader) {
    this.shader = shader;
    this.vao = shader.createCompatibleVao(8096);
  }

  public void unregister(List<Entity> entities) {
    entities.forEach(this::unregister);
    LOGGER.info("Unregistered {} entities", entities.size());
  }

  public void register(List<Entity> entities) {
    entities.forEach(this::register);
    LOGGER.info("Registered {} entities", entities.size());
  }

  public void unregister(Entity entity) {
    Renderable renderable = entity.getRenderable();
    List<Entity> list = registeredEntities.get(renderable.getTexture());
    if (list.remove(entity)) {
      if (list.isEmpty()) {
        registeredEntities.remove(renderable.getTexture());
      }
      LOGGER.debug("Unregistered an entity");
    }
  }

  public void register(Entity entity) {
    Renderable renderable = entity.getRenderable();
    registeredEntities.computeIfAbsent(renderable.getTexture(), t -> new ArrayList<>());
    registeredEntities.get(renderable.getTexture()).add(entity);
    LOGGER.debug("Registered an entity");
  }

  public void render(Camera camera) {
    drawCall = 0;
    glEnable(GL_TEXTURE_2D);
    shader.bind();
    glActiveTexture(GL_TEXTURE0);
    ((UniformMat4) shader.getUniform("viewMatrix")).loadMatrix(camera.getViewMatrix());
    ((UniformMat4) shader.getUniform("projectionMatrix")).loadMatrix(camera.getProjectionMatrix());
    for (Map.Entry<Texture, List<Entity>> entry : registeredEntities.entrySet()) {
      entry.getKey().bind();
      for (Entity entity : entry.getValue()) {
        if (!vao.batch(entity.getRenderable().getQuad())) {
          // If the VAO is full, draw it and start a new batch
          drawVao();
          vao.batch(entity.getRenderable().getQuad());
        }
      }
      drawVao();
    }
    shader.unbind();
  }

  public void drawVao() {
    vao.draw();
    drawCall++;
  }

  public int getFrameDrawCall() {
    return drawCall;
  }

  public void cleanUp() {
    vao.cleanUp();
    shader.cleanUp();
  }
}
