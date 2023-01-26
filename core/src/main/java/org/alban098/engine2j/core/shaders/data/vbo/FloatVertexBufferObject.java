/*
 * Copyright (c) 2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package org.alban098.engine2j.core.shaders.data.vbo;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15C.glBufferSubData;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;

import java.nio.Buffer;
import java.nio.FloatBuffer;
import org.lwjgl.system.MemoryUtil;

/**
 * Concrete implementation of {@link VertexBufferObject} that can store data composed of {@link
 * Float} primitives. Such as Float, Vec2, Vec3 or Vec4
 */
public final class FloatVertexBufferObject extends VertexBufferObject<Float> {

  /** A Buffer used to queue data before sending them to VRAM */
  private final FloatBuffer buffer;

  /**
   * Creates a new Vertex Buffer Object
   *
   * @param location the binding location of the VBO
   * @param dataDimension the dimension of the attribute to link to the VBO, must be <= 4
   * @param capacity the total capacity of the VBO (in number of primitives not in bytes)
   */
  public FloatVertexBufferObject(int location, int dataDimension, int capacity) {
    // a float is exactly 4 bytes
    super(location, dataDimension, capacity, 4);
    this.buffer = MemoryUtil.memAllocFloat((int) (size / dataSize));
  }

  /**
   * Buffers a {@link FloatBuffer} into this Vertex Buffer Object, do nothing if the buffer is of
   * another type
   *
   * @param data the {@link FloatBuffer} to load
   * @param <B> the type of buffer to Load, must be {@link FloatBuffer}
   */
  @Override
  public <B extends Buffer> void buffer(B data) {
    if (data instanceof FloatBuffer) {
      buffer.put((FloatBuffer) data);
    }
  }

  /**
   * Buffers a single float of data
   *
   * @param data the data to load
   */
  public void buffer(Float data) {
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
    glVertexAttribPointer(location, dataDim, GL_FLOAT, false, 0, 0);
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
   * @return the type of primitives stored in this Vertex Buffer Object, {@link Float} for this
   *     implementation
   */
  @Override
  public Class<Float> getType() {
    return Float.class;
  }

  /**
   * Returns the type of Buffer that this Vertex Buffer Object will accept in its {@link
   * VertexBufferObject#buffer(Buffer)} method
   *
   * @return the type of Buffer accepted by this Vertex Buffer Object, {@link FloatBuffer} for this
   *     implementation
   */
  public Class<FloatBuffer> getBufferType() {
    return FloatBuffer.class;
  }
}
