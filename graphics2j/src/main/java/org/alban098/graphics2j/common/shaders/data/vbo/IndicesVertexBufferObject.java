/*
 * Copyright (c) 2022-2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package org.alban098.graphics2j.common.shaders.data.vbo;

import org.alban098.common.Cleanable;
import org.lwjgl.system.MemoryUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.Buffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11C.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL15C.glBufferSubData;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL30C.glVertexAttribIPointer;

/**
 * This class represents a Vertex Buffer Object containing a certain type of primitives a VBO is
 * basically an array of primitives that will be loaded to the GPU to be fed to a Vertex Shader This
 * implementation can be parametrized with a maximum capacity of primitives of a specified size (in
 * bytes) and dimension.
 */
public class IndicesVertexBufferObject implements Cleanable {

  /** Just a Logger to log events */
  private static final Logger LOGGER = LoggerFactory.getLogger(IndicesVertexBufferObject.class);

  /** The id of the VBO, as identified by OpenGL */
  private final int id;

  /** The total size of the buffer in bytes */
  private final long size;
  /** A Buffer used to queue data before sending them to VRAM */
  private final IntBuffer buffer;

  /** Unbinds the currently bound Vertex Buffer Object */
  public static void unbind() {
    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
  }

  /**
   * Creates a new Vertex Buffer Object
   * @param primitiveCapacity the total capacity of the VBO (in number of primitives not in bytes)
   */
  public IndicesVertexBufferObject(long primitiveCapacity, int indicesPerPrimitive) {
    this.id = glGenBuffers();
    this.size = primitiveCapacity * 4 * indicesPerPrimitive;
    this.buffer = MemoryUtil.memAllocInt((int) size);
    LOGGER.info(
        "Successfully created a VBO {} holding {} primitive constituted of 1 element of 4 byte(s) each",
        id,
        primitiveCapacity);
    bind();
    glBufferData(GL_ELEMENT_ARRAY_BUFFER, size, GL_DYNAMIC_DRAW);
    initialize();
  }

  /** Binds the Vertex Buffer Object to be sent to the bound Vertex Shader */
  public void bind() {
    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, id);
  }

  /** Clears the Vertex Buffer Object from VRAM */
  @Override
  public void cleanUp() {
    glDeleteBuffers(id);
    MemoryUtil.memFree(buffer);
    LOGGER.info("VBO {} cleaned up", id);
  }

  /**
   * Returns the unique identifier of the Vertex Buffer Object as identified by OpenGL
   *
   * @return the unique OpenGL id of the Vertex Buffer Object
   */
  public int getId() {
    return id;
  }


  /**
   * Returns the total size allocated to this Vertex Buffer Object into VRAM in bytes
   *
   * @return the total allocated size in bytes
   */
  public long getSize() {
    return size;
  }


  /**
   * Buffers a single primitive of data
   *
   * @param data the data to load
   */
  public void buffer(Integer data) {
    buffer.put(data);
  }

  public void buffer(IntBuffer data) {
      buffer.put(data);
  }

  public void buffer(int[] data, int offset) {
    for (int index : data) {
      buffer.put(index + offset);
    }
  }


  /**
   * Loads the currently buffered data into VRAM to be read by the Vertex Shader, must be called
   * after one or more calls to {@link IndicesVertexBufferObject#buffer(IntBuffer)} or {@link
   * IndicesVertexBufferObject#buffer(Integer)}
   */
  public void load() {
    // prepare the buffer for read
    buffer.flip();
    // bind the VBO
    bind();
    // load the data to VRAM at offset 0
    glBufferSubData(GL_ELEMENT_ARRAY_BUFFER, 0, buffer);
    // clear the VBO's CPU side as it has been loaded to VRAM, ready for next CPU frame
    buffer.clear();
  }

  /**
   * Returns the type of primitives stored in this Vertex Buffer Object this method returns the type
   * of primitive and not the type of data for Vec2, Vec3 and Vec4 expect {@link Float} instead of
   * {@link org.joml.Vector2f}
   *
   * @return the type of primitives stored in this Vertex Buffer Object
   */
  public Class<Integer> getType() {
    return Integer.class;
  }
}
