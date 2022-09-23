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
import rendering.shaders.VertexAttribute;

public class Vao {

  private static final Logger LOGGER = LoggerFactory.getLogger(Vao.class);

  private final int id;
  private final Vbo<Integer> indices;
  private final Map<VertexAttribute, Vbo<? extends Number>> vbos;

  private final int maxQuadCapacity;

  private int currentVertexIndex = 0;

  public Vao(int maxQuadCapacity) {
    id = glGenVertexArrays();
    vbos = new HashMap<>();

    indices = new VboInt(GL_ELEMENT_ARRAY_BUFFER, 1, maxQuadCapacity * Quad.NB_INDICES);

    this.maxQuadCapacity = maxQuadCapacity;
  }

  public void addVbo(VertexAttribute attribute) {
    if (attribute.getType() == GL_FLOAT) {
      vbos.put(
          attribute,
          new AttributeVboFloat(
              GL_ARRAY_BUFFER,
              attribute.getLocation(),
              attribute.getDimension(),
              maxQuadCapacity * Quad.NB_VERTEX));
    } else if (attribute.getType() == GL_UNSIGNED_INT) {
      vbos.put(
          attribute,
          new AttributeVboInt(
              GL_ARRAY_BUFFER,
              attribute.getLocation(),
              attribute.getDimension(),
              maxQuadCapacity * Quad.NB_VERTEX));
    }
  }

  public boolean batch(Quad quad) {
    if (currentVertexIndex >= maxQuadCapacity - 1) {
      return false;
    }
    for (Map.Entry<VertexAttribute, Vbo<? extends Number>> entry : vbos.entrySet()) {
      entry.getValue().buffer(quad.get(entry.getKey()));
    }
    quad.setIndices(currentVertexIndex++);
    indices.buffer(quad.getIndices());
    return true;
  }

  public void draw() {
    glBindVertexArray(id);

    for (Vbo<?> vbo : vbos.values()) {
      vbo.load();
    }
    indices.load();

    glDrawElements(GL_TRIANGLES, currentVertexIndex * Quad.NB_INDICES, GL_UNSIGNED_INT, 0);

    currentVertexIndex = 0;
    Vbo.unbind(GL_ARRAY_BUFFER);
    Vbo.unbind(GL_ELEMENT_ARRAY_BUFFER);
    glBindVertexArray(0);
  }

  public void cleanUp() {
    for (Vbo<?> vbo : vbos.values()) {
      vbo.cleanUp();
    }
    indices.cleanUp();
  }
}
