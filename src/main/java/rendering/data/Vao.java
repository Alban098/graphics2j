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

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rendering.shaders.ShaderAttribute;

public class Vao {

  private static final Logger LOGGER = LoggerFactory.getLogger(Vao.class);

  private final int id;
  private final Vbo<Integer> vboIndices;
  private final Map<ShaderAttribute, Vbo<? extends Number>> vbos;

  private final int maxQuadCapacity;

  private final Integer[] currentIndices = Arrays.copyOf(Quad.INDICES, 6);

  private int batchSize = 0;

  public Vao(int maxQuadCapacity) {
    id = glGenVertexArrays();
    vbos = new HashMap<>();

    vboIndices = new VboInt(GL_ELEMENT_ARRAY_BUFFER, 1, maxQuadCapacity * Quad.NB_INDICES);

    this.maxQuadCapacity = maxQuadCapacity;
  }

  public void linkVbo(ShaderAttribute attribute) {
    int capacity = maxQuadCapacity * Quad.NB_VERTEX;
    if (attribute.getType() == GL_FLOAT) {
      vbos.put(
          attribute,
          new VboFloat(
              GL_ARRAY_BUFFER, attribute.getLocation(), attribute.getDimension(), capacity));
    } else if (attribute.getType() == GL_UNSIGNED_INT) {
      vbos.put(
          attribute,
          new VboInt(GL_ARRAY_BUFFER, attribute.getLocation(), attribute.getDimension(), capacity));
    }
  }

  public boolean batch(Quad quad) {
    if (batchSize >= maxQuadCapacity - 1) {
      return false;
    }
    for (Map.Entry<ShaderAttribute, Vbo<? extends Number>> entry : vbos.entrySet()) {
      ShaderAttribute attribute = entry.getKey();
      Vbo<?> vbo = entry.getValue();
      Number[] data = quad.get(attribute);

      // If per element, only load it once, else load it as many times as there are vertices
      if (attribute.isPerVertex()) {
        for (int i = 0; i < Quad.NB_VERTEX; i++) {
          vbo.buffer(data);
        }
      } else {
        vbo.buffer(data);
      }
    }
    vboIndices.buffer(currentIndices);
    updateBatchInfo();
    return true;
  }

  private void updateBatchInfo() {
    for (int i = 0; i < currentIndices.length; i++) {
      currentIndices[i] += Quad.NB_VERTEX;
    }
    batchSize++;
  }

  public void draw() {
    prepareFrame();
    glDrawElements(GL_TRIANGLES, batchSize * Quad.NB_INDICES, GL_UNSIGNED_INT, 0);
    finalizeFrame();
  }

  private void prepareFrame() {
    glBindVertexArray(id);
    for (Vbo<?> vbo : vbos.values()) {
      vbo.load();
    }
    vboIndices.load();
  }

  private void finalizeFrame() {
    batchSize = 0;
    System.arraycopy(Quad.INDICES, 0, currentIndices, 0, Quad.NB_INDICES);
    Vbo.unbind(GL_ARRAY_BUFFER);
    Vbo.unbind(GL_ELEMENT_ARRAY_BUFFER);
    glBindVertexArray(0);
  }

  public void cleanUp() {
    for (Vbo<?> vbo : vbos.values()) {
      vbo.cleanUp();
    }
    vboIndices.cleanUp();
  }
}
