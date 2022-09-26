/*
 * Copyright (c) 2022, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering.data;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL30.glBindBufferBase;
import static org.lwjgl.opengl.GL43.GL_SHADER_STORAGE_BUFFER;

import java.nio.FloatBuffer;
import org.lwjgl.system.MemoryUtil;

public class Ssbo {

  private final FloatBuffer buffer;

  private final int id;
  private final int location;

  public Ssbo(int location, int dataDim, int maxCapacity) {
    this.id = glGenBuffers();
    this.location = location;
    this.buffer = MemoryUtil.memAllocFloat(maxCapacity * dataDim);
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
    buffer.clear();
  }

  public void bind() {
    glBindBuffer(GL_SHADER_STORAGE_BUFFER, id);
  }

  public void cleanUp() {
    glDeleteBuffers(id);
    MemoryUtil.memFree(buffer);
  }

  public static void unbind() {
    glBindBuffer(GL_SHADER_STORAGE_BUFFER, 0);
  }
}
