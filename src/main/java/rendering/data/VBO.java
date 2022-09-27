/*
 * Copyright (c) 2022, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering.data;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;

import java.nio.FloatBuffer;
import org.lwjgl.system.MemoryUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VBO {

  private static final Logger LOGGER = LoggerFactory.getLogger(VBO.class);

  private final FloatBuffer buffer;

  private final int id;
  private final int dataDim;
  private final int location;
  private final int size;

  private int filled;

  public VBO(int location, int dataDim, int maxCapacity) {
    if (dataDim > 4) {
      LOGGER.error("Max vbo data dimension is 4, actual dimension is {}", dataDim);
      System.exit(-1);
    }
    this.id = glGenBuffers();
    this.location = location;
    this.dataDim = dataDim;
    this.size = maxCapacity * dataDim;
    this.buffer = MemoryUtil.memAllocFloat(size);
    bind();
    glBufferData(GL_ARRAY_BUFFER, (long) maxCapacity * dataDim, GL_DYNAMIC_DRAW);
    LOGGER.debug(
        "Created VBO with id {} at location {} with a size of {} bytes",
        id,
        location,
        this.buffer.capacity());
  }

  public void buffer(FloatBuffer data) {
    buffer.put(data);
  }

  public void buffer(float data) {
    buffer.put(data);
  }

  public void load() {
    buffer.flip();
    bind();
    glBufferSubData(GL_ARRAY_BUFFER, 0, buffer);
    glEnableVertexAttribArray(location);
    glVertexAttribPointer(location, dataDim, GL_FLOAT, false, 0, 0);
    LOGGER.trace("Filled VBO {} with {} bytes", id, buffer.limit());
    filled = buffer.limit();
    buffer.clear();
  }

  public void bind() {
    glBindBuffer(GL_ARRAY_BUFFER, id);
    LOGGER.trace("Bound VBO {}", id);
  }

  public void cleanUp() {
    glDeleteBuffers(id);
    MemoryUtil.memFree(buffer);
    LOGGER.debug("VBO {} cleaned up", id);
  }

  public static void unbind() {
    glBindBuffer(GL_ARRAY_BUFFER, 0);
  }

  public int getId() {
    return id;
  }

  public int getDataDim() {
    return dataDim;
  }

  public int getLocation() {
    return location;
  }

  public int getSize() {
    return size;
  }

  public int getFilled() {
    return filled;
  }
}
