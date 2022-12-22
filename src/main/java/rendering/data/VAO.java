/*
 * Copyright (c) 2022, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering.data;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rendering.entities.Entity;
import rendering.entities.component.RenderableComponent;
import rendering.entities.component.TransformComponent;
import rendering.entities.component.TransformUtils;
import rendering.shaders.ShaderAttribute;
import rendering.shaders.ShaderAttributes;

public class VAO {

  private static final Logger LOGGER = LoggerFactory.getLogger(VAO.class);
  private static final int TRANSFORM_SIZE = 16;

  private final int id;
  private final Map<ShaderAttribute, VBO> vbos;

  private final SSBO ssbo;

  private final int maxQuadCapacity;
  private int batchSize = 0;

  public VAO(int maxQuadCapacity) {
    id = glGenVertexArrays();
    vbos = new HashMap<>();
    ssbo = new SSBO(0, TRANSFORM_SIZE, maxQuadCapacity);
    this.maxQuadCapacity = maxQuadCapacity;
    LOGGER.debug("Created VAO with id {} and with a size of {} primitives", id, maxQuadCapacity);
  }

  public void linkVbo(ShaderAttribute attribute) {
    vbos.put(
        attribute, new VBO(attribute.getLocation(), attribute.getDimension(), maxQuadCapacity));
  }

  public boolean batch(Entity entity) {
    RenderableComponent renderableComponent = entity.getComponent(RenderableComponent.class);
    TransformComponent transformComponent = entity.getComponent(TransformComponent.class);

    if (batchSize >= maxQuadCapacity - 1) {
      return false;
    }

    if (transformComponent != null) {
      ssbo.buffer(transformComponent.toFloatBuffer(entity.getParent()));
    } else {
      ssbo.buffer(TransformUtils.getNullTransformBuffer());
    }

    for (Map.Entry<ShaderAttribute, VBO> entry : vbos.entrySet()) {
      ShaderAttribute attribute = entry.getKey();
      VBO vbo = entry.getValue();
      if (attribute.equals(ShaderAttributes.INDEX)) {
        vbo.buffer(batchSize);
      } else {
        FloatBuffer data = renderableComponent.get(attribute);
        vbo.buffer(data);
      }
    }
    batchSize++;
    LOGGER.trace("Batched a primitive to VAO {}", id);
    return true;
  }

  public void draw() {
    prepareFrame();
    glDrawArrays(GL_POINTS, 0, batchSize);
    finalizeFrame();
  }

  private void prepareFrame() {
    glBindVertexArray(id);
    ssbo.load();
    for (VBO vbo : vbos.values()) {
      vbo.load();
    }
  }

  private void finalizeFrame() {
    batchSize = 0;
    VBO.unbind();
    SSBO.unbind();
    glBindVertexArray(0);
  }

  public void cleanUp() {
    for (VBO vbo : vbos.values()) {
      vbo.cleanUp();
    }
    ssbo.cleanUp();
    LOGGER.debug("VAO {} cleaned up", id);
  }

  public int getId() {
    return id;
  }

  public int getMaxQuadCapacity() {
    return maxQuadCapacity;
  }

  public int getBatchSize() {
    return batchSize;
  }

  public Map<ShaderAttribute, VBO> getVbos() {
    return vbos;
  }

  public SSBO getSsbo() {
    return ssbo;
  }
}
