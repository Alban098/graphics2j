/*
 * Copyright (c) 2022, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import org.lwjgl.system.MemoryUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Vao {

  private static final Logger LOGGER = LoggerFactory.getLogger(Vao.class);

  private final int id;
  private final int positionVbo;
  private final int textureCoordsVbo;
  private final int indicesVbo;

  private final int maxQuadCapacity;

  private final FloatBuffer positionBuffer;
  private final FloatBuffer textureCoordsBuffer;
  private final IntBuffer indicesBuffer;

  private int currentIndex = 0;

  public Vao(int maxQuadCapacity) {
    id = glGenVertexArrays();

    positionVbo = glGenBuffers();
    glBindBuffer(GL_ARRAY_BUFFER, positionVbo);
    glBufferData(
        GL_ARRAY_BUFFER,
        new float[maxQuadCapacity * Quad.VERTICES_DIM * Quad.NB_VERTEX],
        GL_DYNAMIC_DRAW);

    textureCoordsVbo = glGenBuffers();
    glBindBuffer(GL_ARRAY_BUFFER, textureCoordsVbo);
    glBufferData(
        GL_ARRAY_BUFFER,
        new float[maxQuadCapacity * Quad.VERTICES_DIM * Quad.NB_VERTEX],
        GL_DYNAMIC_DRAW);

    indicesVbo = glGenBuffers();
    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indicesVbo);
    glBufferData(
        GL_ELEMENT_ARRAY_BUFFER, new int[maxQuadCapacity * Quad.NB_INDICES], GL_DYNAMIC_DRAW);

    this.maxQuadCapacity = maxQuadCapacity;

    positionBuffer = MemoryUtil.memAllocFloat(maxQuadCapacity * Quad.VERTICES_DIM * Quad.NB_VERTEX);
    textureCoordsBuffer =
        MemoryUtil.memAllocFloat(maxQuadCapacity * Quad.VERTICES_DIM * Quad.NB_VERTEX);
    indicesBuffer = MemoryUtil.memAllocInt(maxQuadCapacity * Quad.NB_INDICES);
  }

  public boolean batch(Quad quad) {
    if (currentIndex >= maxQuadCapacity - 1) {
      return false;
    }
    positionBuffer.put(quad.getPosition());
    textureCoordsBuffer.put(quad.getTexCoords());
    quad.setIndices(currentIndex++);
    indicesBuffer.put(quad.getIndices());
    return true;
  }

  public void prepare() {
    bind();

    positionBuffer.flip();
    textureCoordsBuffer.flip();
    indicesBuffer.flip();

    // Position VBO
    glBindBuffer(GL_ARRAY_BUFFER, positionVbo);
    glBufferSubData(GL_ARRAY_BUFFER, 0, positionBuffer);
    glEnableVertexAttribArray(0);
    glVertexAttribPointer(0, 2, GL_FLOAT, false, 0, 0);

    // Texture coordinates VBO
    glBindBuffer(GL_ARRAY_BUFFER, textureCoordsVbo);
    glBufferSubData(GL_ARRAY_BUFFER, 0, textureCoordsBuffer);
    glEnableVertexAttribArray(1);
    glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);

    // Index VBO
    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indicesVbo);
    glBufferSubData(GL_ELEMENT_ARRAY_BUFFER, 0, indicesBuffer);

    glBindBuffer(GL_ARRAY_BUFFER, 0);

    positionBuffer.clear();
    textureCoordsBuffer.clear();
    indicesBuffer.clear();
  }

  public void prepareAndRender() {
    prepare();
    glDrawElements(GL_TRIANGLES, currentIndex * Quad.NB_INDICES, GL_UNSIGNED_INT, 0);
    unbind();
    currentIndex = 0;
  }

  public void bind() {
    glBindVertexArray(id);
  }

  public void unbind() {
    glBindVertexArray(0);
  }

  public void cleanUp() {
    glDeleteBuffers(positionVbo);
    glDeleteBuffers(textureCoordsVbo);
    glDeleteBuffers(indicesVbo);
    MemoryUtil.memFree(positionBuffer);
    MemoryUtil.memFree(textureCoordsBuffer);
    MemoryUtil.memFree(indicesBuffer);
  }
}
