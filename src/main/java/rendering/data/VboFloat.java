/*
 * Copyright (c) 2022, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering.data;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL15.glBufferSubData;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;

import java.nio.FloatBuffer;
import org.lwjgl.system.MemoryUtil;

public class VboFloat extends Vbo<Float> {

  private final FloatBuffer buffer;

  public VboFloat(int type, int dimData, int maxCapacity) {
    this(type, NO_LOCATION, dimData, maxCapacity);
  }

  public VboFloat(int type, int location, int dataDim, int maxCapacity) {
    super(type, location, dataDim, maxCapacity, GL_FLOAT);
    buffer = MemoryUtil.memAllocFloat(maxCapacity * dataDim);
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
      buffer.put(val.floatValue());
    }
  }

  @Override
  public void cleanUp() {
    super.cleanUp();
    MemoryUtil.memFree(buffer);
  }
}
