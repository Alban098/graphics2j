/*
 * Copyright (c) 2022-2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering.data;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

import java.nio.Buffer;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rendering.data.vbo.FloatVertexBufferObject;
import rendering.data.vbo.IntegerVertexBufferObject;
import rendering.data.vbo.VertexBufferObject;
import rendering.entities.component.RenderableComponent;
import rendering.entities.component.TransformComponent;
import rendering.entities.component.TransformUtils;
import rendering.renderers.Renderable;
import rendering.shaders.ShaderAttribute;
import rendering.shaders.ShaderAttributes;

public class VertexArrayObject {

  private static final Logger LOGGER = LoggerFactory.getLogger(VertexArrayObject.class);
  private static final int TRANSFORM_SIZE = 16;

  private final int id;
  private final Map<ShaderAttribute, VertexBufferObject<?>> vbos;

  private final ShaderStorageBufferObject ssbo;

  private final int maxQuadCapacity;
  private int batchSize = 0;

  public VertexArrayObject(int maxQuadCapacity) {
    id = glGenVertexArrays();
    vbos = new HashMap<>();
    ssbo = new ShaderStorageBufferObject(0, TRANSFORM_SIZE, maxQuadCapacity);
    this.maxQuadCapacity = maxQuadCapacity;
    LOGGER.debug("Created VAO with id {} and with a size of {} primitives", id, maxQuadCapacity);
  }

  public void linkVbo(ShaderAttribute attribute) {
    Class<?> dataClass = attribute.getDataType();
    if (dataClass.equals(Float.class)) {
      vbos.put(
          attribute,
          new FloatVertexBufferObject(
              attribute.getLocation(), attribute.getDimension(), maxQuadCapacity));
    } else if (dataClass.equals(Integer.class)) {
      vbos.put(
          attribute,
          new IntegerVertexBufferObject(
              attribute.getLocation(), attribute.getDimension(), maxQuadCapacity));
    }
  }

  public boolean batch(Renderable renderable) {
    if (batchSize >= maxQuadCapacity) {
      return false;
    }
    RenderableComponent renderableComponent = renderable.getRenderable();
    TransformComponent transformComponent = renderable.getTransform();
    if (renderableComponent != null) {
      if (transformComponent != null) {
        ssbo.buffer(transformComponent.toFloatBuffer(true));
      } else {
        ssbo.buffer(TransformUtils.getNullTransformBuffer());
      }

      for (Map.Entry<ShaderAttribute, VertexBufferObject<?>> entry : vbos.entrySet()) {
        ShaderAttribute attribute = entry.getKey();
        if (attribute.equals(ShaderAttributes.INDEX)
            && attribute.getDataType().equals(Integer.class)) {
          VertexBufferObject<Integer> vbo = (VertexBufferObject<Integer>) entry.getValue();
          vbo.buffer(batchSize);
        } else {
          VertexBufferObject<?> vbo = entry.getValue();
          Buffer data = renderableComponent.get(attribute);
          vbo.buffer(data);
        }
      }
      batchSize++;
      LOGGER.trace("Batched a primitive to VAO {}", id);
    }
    return true;
  }

  public void draw(Renderable renderable) {
    batch(renderable);
    draw();
  }

  public void draw() {
    prepareFrame();
    glDrawArrays(GL_POINTS, 0, batchSize);
    LOGGER.debug("Drawn a batch of {} elements", batchSize);
    finalizeFrame();
  }

  private void prepareFrame() {
    glBindVertexArray(id);
    ssbo.load();
    for (VertexBufferObject vbo : vbos.values()) {
      vbo.load();
    }
  }

  private void finalizeFrame() {
    batchSize = 0;
    VertexBufferObject.unbind();
    ShaderStorageBufferObject.unbind();
    glBindVertexArray(0);
  }

  public void cleanUp() {
    for (VertexBufferObject vbo : vbos.values()) {
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

  public Map<ShaderAttribute, VertexBufferObject<?>> getVbos() {
    return vbos;
  }

  public ShaderStorageBufferObject getSsbo() {
    return ssbo;
  }
}
