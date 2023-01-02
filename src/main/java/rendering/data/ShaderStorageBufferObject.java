/*
 * Copyright (c) 2022-2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering.data;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL30.glBindBufferBase;
import static org.lwjgl.opengl.GL43.GL_SHADER_STORAGE_BUFFER;

import java.nio.FloatBuffer;
import org.lwjgl.system.MemoryUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ShaderStorageBufferObject {

  private static final Logger LOGGER = LoggerFactory.getLogger(ShaderStorageBufferObject.class);

  private final FloatBuffer buffer;

  private final int id;
  private final int location;
  private final int size;

  public ShaderStorageBufferObject(int location, int dataDim, int maxCapacity) {
    this.id = glGenBuffers();
    this.location = location;
    this.size = maxCapacity * dataDim;
    this.buffer = MemoryUtil.memAllocFloat(size);
    LOGGER.debug(
        "Created SSBO with id {} at location {} with a size of {} bytes",
        id,
        location,
        this.buffer.capacity());
    bind();
  }

  public void buffer(FloatBuffer data) {
    buffer.put(data);
  }

  public void load() {
    buffer.flip();
    bind();
    glBufferData(GL_SHADER_STORAGE_BUFFER, buffer, GL_STATIC_DRAW);
    glBindBufferBase(GL_SHADER_STORAGE_BUFFER, location, id);
    LOGGER.trace("Filled SSBO {} with {} bytes", id, buffer.limit());
    buffer.clear();
  }

  public void bind() {
    glBindBuffer(GL_SHADER_STORAGE_BUFFER, id);
    LOGGER.trace("Bound SSBO {}", id);
  }

  public void cleanUp() {
    glDeleteBuffers(id);
    MemoryUtil.memFree(buffer);
    LOGGER.debug("SSBO {} cleaned up", id);
  }

  public static void unbind() {
    glBindBuffer(GL_SHADER_STORAGE_BUFFER, 0);
    LOGGER.trace("Unbound current SSBO");
  }

  public int getId() {
    return id;
  }

  public int getLocation() {
    return location;
  }

  public int getSize() {
    // convert from sizeof(float) to bytes
    return size * 4;
  }
}
