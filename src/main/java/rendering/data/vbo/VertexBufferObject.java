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
  protected final int size;

  protected int filled;

  public VertexBufferObject(int location, int dataDim, int maxCapacity, int dataSize) {
    if (dataDim > 4) {
      LOGGER.error("Max vbo data dimension is 4, actual dimension is {}", dataDim);
      System.exit(-1);
    }
    this.id = glGenBuffers();
    this.location = location;
    this.dataDim = dataDim;
    this.size = maxCapacity * dataDim * dataSize;
    this.dataSize = dataSize;
    bind();
    glBufferData(GL_ARRAY_BUFFER, (long) maxCapacity * dataDim * dataSize, GL_DYNAMIC_DRAW);
  }

  public abstract <B extends Buffer> void buffer(B data);

  public abstract void buffer(T data);

  public abstract void load();

  public void bind() {
    glBindBuffer(GL_ARRAY_BUFFER, id);
    LOGGER.trace("Bound VBO {}", id);
  }

  public void cleanUp() {
    glDeleteBuffers(id);
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
    // convert from sizeof(data) to bytes
    return filled * dataSize;
  }

  public abstract Class<T> getType();
}
