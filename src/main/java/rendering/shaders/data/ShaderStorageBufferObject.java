/*
 * Copyright (c) 2022-2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering.shaders.data;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL30.glBindBufferBase;
import static org.lwjgl.opengl.GL43.GL_SHADER_STORAGE_BUFFER;

import java.nio.Buffer;
import java.nio.FloatBuffer;
import org.lwjgl.system.MemoryUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents a Shader Storage Buffer Object that can be linked to a {@link VertexArrayObject}, it's
 * similar to as {@link rendering.shaders.data.vbo.VertexBufferObject} but can store arbitrarily
 * large objects. Can be bound, loaded and read from a standard {@link
 * rendering.shaders.ShaderProgram}'s vertex shader. It's basically an array of primitives that can
 * be cast in the shader into an arbitrary structure.
 *
 * <p>Only floats are supported yet
 */
public final class ShaderStorageBufferObject {

  private static final Logger LOGGER = LoggerFactory.getLogger(ShaderStorageBufferObject.class);
  /** A Buffer used to queue data before sending them to VRAM */
  private final FloatBuffer buffer;
  /** The id of the SSBO, as identified by OpenGL */
  private final int id;
  /** The binding location of the SSBO as specified in the Vertex Shader (binding=X) */
  private final int location;
  /** The total size of the buffer in bytes */
  private final int size;

  /** Unbinds the currently bound Shader Storage Buffer Object */
  public static void unbind() {
    glBindBuffer(GL_SHADER_STORAGE_BUFFER, 0);
    LOGGER.trace("Unbound current SSBO");
  }

  /**
   * Creates a new Shader Storage Buffer Object
   *
   * @param location the binding location of the SSBO
   * @param dataDimension the dimension of a singular object stored in the SSBO
   * @param maxCapacity the number of object the SSBO can store
   */
  public ShaderStorageBufferObject(int location, int dataDimension, int maxCapacity) {
    this.id = glGenBuffers();
    this.location = location;
    this.size = maxCapacity * dataDimension * 4;
    this.buffer = MemoryUtil.memAllocFloat(maxCapacity * dataDimension);
    LOGGER.debug(
        "Created SSBO with id {} at location {} with a size of {} bytes",
        id,
        location,
        this.buffer.capacity());
    bind();
  }

  /**
   * Buffers a {@link Buffer} into this Shader Storage Buffer Object to implement a unique methods
   * or every Buffer type
   *
   * @param data the {@link Buffer} to load
   */
  public void buffer(FloatBuffer data) {
    buffer.put(data);
  }

  /**
   * Loads the currently buffered data into VRAM to be read by the Vertex Shader, must be called
   * after one or more calls to {@link ShaderStorageBufferObject#buffer(FloatBuffer)}
   */
  public void load() {
    buffer.flip();
    bind();
    glBufferData(GL_SHADER_STORAGE_BUFFER, buffer, GL_STATIC_DRAW);
    glBindBufferBase(GL_SHADER_STORAGE_BUFFER, location, id);
    LOGGER.trace("Filled SSBO {} with {} bytes", id, buffer.limit());
    buffer.clear();
  }

  /** Binds the Shader Storage Buffer Object to be sent to the bound Vertex Shader */
  public void bind() {
    glBindBuffer(GL_SHADER_STORAGE_BUFFER, id);
    LOGGER.trace("Bound SSBO {}", id);
  }

  /** Clears the Shader Storage Buffer Object from VRAM */
  public void cleanUp() {
    glDeleteBuffers(id);
    MemoryUtil.memFree(buffer);
    LOGGER.debug("SSBO {} cleaned up", id);
  }

  /**
   * Return the unique identifier of the Shader Storage Buffer Object as identified by OpenGL
   *
   * @return the unique OpenGL id of the Shader Storage Buffer Object
   */
  public int getId() {
    return id;
  }

  /**
   * Returns the binding location of this Shader Storage Buffer Object as identifier in a Vertex
   * Shader
   *
   * <p><i>layout(std430, binding = X) buffer *attributeName*;</i>
   *
   * @return the binding location of this Shader Storage Buffer Object
   */
  public int getLocation() {
    return location;
  }

  /**
   * Returns the total size allocated to this Shader Storage Buffer Object into VRAM in bytes
   *
   * @return the total allocated size in bytes
   */
  public int getSize() {
    return size;
  }
}
