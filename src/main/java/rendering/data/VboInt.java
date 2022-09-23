/*
 * Copyright (c) 2022, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering.data;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;

import java.nio.IntBuffer;
import org.lwjgl.system.MemoryUtil;

public class VboInt extends Vbo<Integer> {

  private final IntBuffer buffer;

  public VboInt(int type, int dimData, int maxCapacity) {
    this(type, NO_LOCATION, dimData, maxCapacity);
  }

  public VboInt(int type, int location, int dataDim, int maxCapacity) {
    super(type, location, dataDim, maxCapacity, GL_INT);
    buffer = MemoryUtil.memAllocInt(maxCapacity * dataDim);
  }

  @Override
  public void load() {
    buffer.flip();
    bind();
    glBufferSubData(type, 0, buffer);
    if (location != NO_LOCATION) {
      glEnableVertexAttribArray(location);
      glVertexAttribPointer(location, dataDim, dataType, false, 0, 0);
    }
    buffer.clear();
  }

  public void buffer(Number[] data) {
    for (Number val : data) {
      buffer.put(val.intValue());
    }
  }

  @Override
  public void cleanUp() {
    super.cleanUp();
    MemoryUtil.memFree(buffer);
  }
}
