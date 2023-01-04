/*
 * Copyright (c) 2022-2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering.data.vbo;

import static org.lwjgl.opengl.GL15.*;

import java.nio.Buffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class VertexBufferObject<T> {

  protected static final Logger LOGGER = LoggerFactory.getLogger(VertexBufferObject.class);

  protected final int id;
  protected final int dataDim;
  protected final int dataSize;
  protected final int location;
  protected final long size;

  public static void unbind() {
    glBindBuffer(GL_ARRAY_BUFFER, 0);
  }

  public VertexBufferObject(int location, int dataDimension, long capacity, int dataSizeBytes) {
    if (dataDimension > 4) {
      LOGGER.error("Max vbo data dimension is 4, actual dimension is {}", dataDimension);
      System.exit(-1);
    }
    this.id = glGenBuffers();
    this.location = location;
    this.dataDim = dataDimension;
    this.size = capacity * dataDimension * dataSizeBytes;
    this.dataSize = dataSizeBytes;
    bind();
    // glBufferData except a size in bytes, so we need to multiply by the size of the data type
    glBufferData(GL_ARRAY_BUFFER, size, GL_DYNAMIC_DRAW);
  }

  public void bind() {
    glBindBuffer(GL_ARRAY_BUFFER, id);
    LOGGER.trace("Bound VBO {}", id);
  }

  public void cleanUp() {
    glDeleteBuffers(id);
    LOGGER.debug("VBO {} cleaned up", id);
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

  public long getSize() {
    return size;
  }

  public abstract <B extends Buffer> void buffer(B data);

  public abstract void buffer(T data);

  public abstract void load();

  public abstract Class<T> getType();

  public abstract Class<? extends Buffer> getBufferType();
}
