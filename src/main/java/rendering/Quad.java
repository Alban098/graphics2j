/*
 * Copyright (c) 2022, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glDeleteVertexArrays;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import org.lwjgl.system.MemoryUtil;

/** This class represent a 3D Mesh that can be rendered by the Engine */
public class Quad {

  private static final float[] POSITIONS = {
    -.5f, .5f, .5f, .5f, .5f, -.5f, -.5f, -.5f,
  };

  private static final float[] TEXTURE_COORDS = {
    0f, 0f,
    1f, 0f,
    1f, 1f,
    0f, 1f
  };

  private static final int[] INDICES = {
    0, 1, 2,
    2, 3, 0
  };

  private final int vaoId;
  private final List<Integer> vboIdList;
  private final int vertexCount;

  /** Create a new Mesh from RAW data */
  public Quad() {
    FloatBuffer posBuffer = null;
    FloatBuffer textCoordsBuffer = null;
    IntBuffer indicesBuffer = null;
    try {
      vertexCount = INDICES.length;
      vboIdList = new ArrayList<>();

      vaoId = glGenVertexArrays();
      glBindVertexArray(vaoId);

      // Position VBO
      int vboId = glGenBuffers();
      vboIdList.add(vboId);
      posBuffer = MemoryUtil.memAllocFloat(POSITIONS.length);
      posBuffer.put(POSITIONS).flip();
      glBindBuffer(GL_ARRAY_BUFFER, vboId);
      glBufferData(GL_ARRAY_BUFFER, posBuffer, GL_STATIC_DRAW);
      glEnableVertexAttribArray(0);
      glVertexAttribPointer(0, 2, GL_FLOAT, false, 0, 0);

      // Texture coordinates VBO
      vboId = glGenBuffers();
      vboIdList.add(vboId);
      textCoordsBuffer = MemoryUtil.memAllocFloat(TEXTURE_COORDS.length);
      textCoordsBuffer.put(TEXTURE_COORDS).flip();
      glBindBuffer(GL_ARRAY_BUFFER, vboId);
      glBufferData(GL_ARRAY_BUFFER, textCoordsBuffer, GL_STATIC_DRAW);
      glEnableVertexAttribArray(1);
      glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);

      // Index VBO
      vboId = glGenBuffers();
      vboIdList.add(vboId);
      indicesBuffer = MemoryUtil.memAllocInt(INDICES.length);
      indicesBuffer.put(INDICES).flip();
      glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vboId);
      glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL_STATIC_DRAW);

      glBindBuffer(GL_ARRAY_BUFFER, 0);
      glBindVertexArray(0);
    } finally {
      if (posBuffer != null) {
        MemoryUtil.memFree(posBuffer);
      }
      if (textCoordsBuffer != null) {
        MemoryUtil.memFree(textCoordsBuffer);
      }
      if (indicesBuffer != null) {
        MemoryUtil.memFree(indicesBuffer);
      }
    }
  }

  /**
   * Return the Mesh VAO id
   *
   * @return the Mesh VAO id
   */
  public int getVaoId() {
    return vaoId;
  }

  /**
   * Return the number of Vertices of the Mesh
   *
   * @return the number of Vertices of the Mesh
   */
  public int getVertexCount() {
    return vertexCount;
  }

  /** Initialize the renderer to render the Mesh */
  public void initRender() {
    glBindVertexArray(getVaoId());
  }

  /** End the renderer for this Mesh by unbinding VAO */
  public void endRender() {
    // Restore state
    glBindVertexArray(0);
  }

  /** Render the Mesh */
  public void render() {
    glDrawElements(GL_TRIANGLES, getVertexCount(), GL_UNSIGNED_INT, 0);
  }

  /** Clean up the Mesh and its Textures/VAO/VBOs */
  public void cleanUp() {
    glDisableVertexAttribArray(0);

    // Delete the VBOs
    glBindBuffer(GL_ARRAY_BUFFER, 0);
    for (int vboId : vboIdList) {
      glDeleteBuffers(vboId);
    }

    // Delete the VAO
    glBindVertexArray(0);
    glDeleteVertexArrays(vaoId);
  }
}
