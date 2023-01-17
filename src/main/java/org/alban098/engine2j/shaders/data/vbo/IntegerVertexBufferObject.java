/*
 * Copyright (c) 2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package org.alban098.engine2j.shaders.data.vbo;

import static org.lwjgl.opengl.GL11C.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15C.glBufferSubData;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL30C.glVertexAttribIPointer;

import java.nio.Buffer;
import java.nio.IntBuffer;
import org.lwjgl.system.MemoryUtil;

/**
 * Concrete implementation of {@link VertexBufferObject} that can store data composed of {@link
 * Integer} primitives. Such as Integer
 */
public final class IntegerVertexBufferObject extends VertexBufferObject<Integer> {

  /** A Buffer used to queue data before sending them to VRAM */
  private final IntBuffer buffer;

  /**
   * Creates a new Vertex Buffer Object
   *
   * @param location the binding location of the VBO
   * @param dataDimension the dimension of the attribute to link to the VBO, must be <= 4
   * @param capacity the total capacity of the VBO (in number of primitives not in bytes)
   */
  public IntegerVertexBufferObject(int location, int dataDimension, int capacity) {
    // an integer is exactly 4 bytes
    super(location, dataDimension, capacity, 4);
    this.buffer = MemoryUtil.memAllocInt((int) (size / dataSize));
    LOGGER.debug(
        "Created VBO<int> with id {} at location {} with a size of {} bytes",
        id,
        location,
        this.buffer.capacity());
  }

  /**
   * Buffers a {@link IntBuffer} into this Vertex Buffer Object, do nothing if the buffer is of
   * another type
   *
   * @param data the {@link IntBuffer} to load
   * @param <B> the type of buffer to Load, must be {@link IntBuffer}
   */
  @Override
  public <B extends Buffer> void buffer(B data) {
    if (data instanceof IntBuffer) {
      buffer.put((IntBuffer) data);
    }
  }

  /**
   * Buffers a single float of data
   *
   * @param data the data to load
   */
  public void buffer(Integer data) {
    buffer.put(data);
  }

  /**
   * Loads the currently buffered data into VRAM to be read by the Vertex Shader, must be called
   * after one or more calls to {@link VertexBufferObject#buffer(Buffer)} or {@link
   * VertexBufferObject#buffer(Number)}
   */
  public void load() {
    // prepare the buffer for read
    buffer.flip();
    // bind the VBO
    bind();
    // load the data to VRAM at offset 0
    glBufferSubData(GL_ARRAY_BUFFER, 0, buffer);
    // enable the VBO
    glEnableVertexAttribArray(location);
    // glVertexAttribIPointer() instead of glVertexAttribPointer() to force the type inside the
    // shader to be an integer instead of a float
    glVertexAttribIPointer(location, dataDim, GL_UNSIGNED_INT, 0, 0);
    LOGGER.trace("Filled VBO<int> {} with {} bytes", id, buffer.limit());
    // clear the VBO's CPU side as it has been loaded to VRAM, ready for next CPU frame
    buffer.clear();
  }

  /** Clears the Vertex Buffer Object from VRAM by deallocating the CPU Buffer */
  @Override
  public void cleanUp() {
    super.cleanUp();
    MemoryUtil.memFree(buffer);
  }

  /**
   * Returns the type of primitives stored in this Vertex Buffer Object this method returns the type
   * of primitive and not the type of data
   *
   * @return the type of primitives stored in this Vertex Buffer Object, {@link Integer} for this
   *     implementation
   */
  @Override
  public Class<Integer> getType() {
    return Integer.class;
  }

  /**
   * Returns the type of Buffer that this Vertex Buffer Object will accept in its {@link
   * VertexBufferObject#buffer(Buffer)} method
   *
   * @return the type of Buffer accepted by this Vertex Buffer Object, {@link IntBuffer} for this
   *     implementation
   */
  public Class<IntBuffer> getBufferType() {
    return IntBuffer.class;
  }
}
