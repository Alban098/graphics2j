/*
 * Copyright (c) 2022, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering.data;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rendering.entities.component.Renderable;
import rendering.shaders.ShaderAttribute;

public class Vao {

  private static final Logger LOGGER = LoggerFactory.getLogger(Vao.class);

  private final int id;
  private final Map<ShaderAttribute, Vbo<? extends Number>> vbos;

  private final int maxQuadCapacity;
  private int batchSize = 0;

  public Vao(int maxQuadCapacity) {
    id = glGenVertexArrays();
    vbos = new HashMap<>();
    this.maxQuadCapacity = maxQuadCapacity;
  }

  public void linkVbo(ShaderAttribute attribute) {
    if (attribute.getType() == GL_FLOAT) {
      vbos.put(
          attribute,
          new VboFloat(
              GL_ARRAY_BUFFER, attribute.getLocation(), attribute.getDimension(), maxQuadCapacity));
    } else if (attribute.getType() == GL_UNSIGNED_INT) {
      vbos.put(
          attribute,
          new VboInt(
              GL_ARRAY_BUFFER, attribute.getLocation(), attribute.getDimension(), maxQuadCapacity));
    }
  }

  public boolean batch(Renderable renderable) {
    if (batchSize >= maxQuadCapacity - 1) {
      return false;
    }
    for (Map.Entry<ShaderAttribute, Vbo<? extends Number>> entry : vbos.entrySet()) {
      ShaderAttribute attribute = entry.getKey();
      Vbo<?> vbo = entry.getValue();
      Number[] data = renderable.get(attribute);
      vbo.buffer(data);
    }
    batchSize++;
    return true;
  }

  public void draw() {
    prepareFrame();
    glDrawArrays(GL_POINTS, 0, batchSize);
    finalizeFrame();
  }

  private void prepareFrame() {
    glBindVertexArray(id);
    for (Vbo<?> vbo : vbos.values()) {
      vbo.load();
    }
  }

  private void finalizeFrame() {
    batchSize = 0;
    Vbo.unbind(GL_ARRAY_BUFFER);
    glBindVertexArray(0);
  }

  public void cleanUp() {
    for (Vbo<?> vbo : vbos.values()) {
      vbo.cleanUp();
    }
  }
}
