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

public class Vbo {

  private final FloatBuffer buffer;

  private final int id;
  private final int dataDim;
  private final int location;

  public Vbo(int location, int dataDim, int maxCapacity) {
    this.id = glGenBuffers();
    this.location = location;
    this.dataDim = dataDim;
    this.buffer = MemoryUtil.memAllocFloat(maxCapacity * dataDim);
    bind();
    glBufferData(GL_ARRAY_BUFFER, (long) maxCapacity * dataDim, GL_DYNAMIC_DRAW);
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
    int actualDim = dataDim;
    int actualLocation = location;
    int offset = 0;
    do {
      glEnableVertexAttribArray(actualLocation);
      glVertexAttribPointer(actualLocation++, Math.min(actualDim, 4), GL_FLOAT, false, 0, offset);
      actualDim -= 4;
      offset += 16;
    } while (actualDim > 0);
    buffer.clear();
  }

  public void bind() {
    glBindBuffer(GL_ARRAY_BUFFER, id);
  }

  public void cleanUp() {
    glDeleteBuffers(id);
    MemoryUtil.memFree(buffer);
  }

  public static void unbind() {
    glBindBuffer(GL_ARRAY_BUFFER, 0);
  }
}
