/*
 * Copyright (c) 2022, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering.data;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rendering.entities.component.Renderable;
import rendering.shaders.ShaderAttribute;
import rendering.shaders.ShaderAttributes;

public class Vao {

  private static final Logger LOGGER = LoggerFactory.getLogger(Vao.class);

  private final int id;
  private final Map<ShaderAttribute, Vbo> vbos;

  private final Ssbo ssbo;

  private final int maxQuadCapacity;
  private int batchSize = 0;

  public Vao(int maxQuadCapacity) {
    id = glGenVertexArrays();
    vbos = new HashMap<>();
    ssbo = new Ssbo(0, 16, maxQuadCapacity);
    this.maxQuadCapacity = maxQuadCapacity;
  }

  public void linkVbo(ShaderAttribute attribute) {
    vbos.put(
        attribute, new Vbo(attribute.getLocation(), attribute.getDimension(), maxQuadCapacity));
  }

  public boolean batch(Renderable renderable) {
    if (batchSize >= maxQuadCapacity - 1) {
      return false;
    }

    ssbo.buffer(renderable.get(ShaderAttributes.TRANSFORMS));

    for (Map.Entry<ShaderAttribute, Vbo> entry : vbos.entrySet()) {
      ShaderAttribute attribute = entry.getKey();
      Vbo vbo = entry.getValue();
      if (attribute.equals(ShaderAttributes.INDEX)) {
        vbo.buffer(batchSize);
      } else {
        FloatBuffer data = renderable.get(attribute);
        vbo.buffer(data);
      }
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
    ssbo.load();
    for (Vbo vbo : vbos.values()) {
      vbo.load();
    }
  }

  private void finalizeFrame() {
    batchSize = 0;
    Vbo.unbind();
    glBindVertexArray(0);
  }

  public void cleanUp() {
    for (Vbo vbo : vbos.values()) {
      vbo.cleanUp();
    }
  }
}
