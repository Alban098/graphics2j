/*
 * Copyright (c) 2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering.data.vbo;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15C.glBufferSubData;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;

import java.nio.Buffer;
import java.nio.FloatBuffer;
import org.lwjgl.system.MemoryUtil;

public class FloatVertexBufferObject extends VertexBufferObject<Float> {

  private final FloatBuffer buffer;

  public FloatVertexBufferObject(int location, int dataDim, int maxCapacity) {
    super(location, dataDim, maxCapacity, 4);
    this.buffer = MemoryUtil.memAllocFloat(size / dataSize);
    LOGGER.debug(
        "Created VBO with id {} at location {} with a size of {} bytes",
        id,
        location,
        this.buffer.capacity());
  }

  @Override
  public <B extends Buffer> void buffer(B data) {
    if (data instanceof FloatBuffer) {
      buffer.put((FloatBuffer) data);
    }
  }

  public void buffer(Float data) {
    buffer.put(data);
  }

  public void load() {
    buffer.flip();
    bind();
    glBufferSubData(GL_ARRAY_BUFFER, 0, buffer);
    glEnableVertexAttribArray(location);
    glVertexAttribPointer(location, dataDim, GL_FLOAT, false, 0, 0);
    LOGGER.trace("Filled VBO {} with {} bytes", id, buffer.limit());
    filled = buffer.limit() * dataSize;
    buffer.clear();
  }

  @Override
  public void cleanUp() {
    super.cleanUp();
    MemoryUtil.memFree(buffer);
  }

  @Override
  public Class<Float> getType() {
    return Float.class;
  }
}
