/*
 * Copyright (c) 2022, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering.data;

import static org.lwjgl.opengl.GL15.*;

import java.nio.IntBuffer;
import org.lwjgl.system.MemoryUtil;

public class VboInt extends Vbo<Integer> {

  private final IntBuffer buffer;

  public VboInt(int type, int dataDim, int maxCapacity) {
    super(type, dataDim, maxCapacity, GL_INT);
    buffer = MemoryUtil.memAllocInt(maxCapacity * dataDim);
  }

  @Override
  public void load() {
    buffer.flip();
    bind();
    glBufferSubData(type, 0, buffer);
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
