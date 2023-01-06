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

/**
 * This class represents a Vertex Buffer Object containing a certain type of primitives a VBO is
 * basically an array of primitives that will be loaded to the GPU to be fed to a Vertex Shader This
 * implementation can be parametrized with a maximum capacity of primitives of a specified size (in
 * bytes) and dimension.
 *
 * @param <T> the type of primitives, must be a {@link Number}
 */
public abstract class VertexBufferObject<T extends Number> {

  protected static final Logger LOGGER = LoggerFactory.getLogger(VertexBufferObject.class);

  /** The id of the VBO, as identified by OpenGL */
  protected final int id;
  /** The dimension of the stored attribute (2 for vec2, 3 for vec3 ...) */
  protected final int dataDim;
  /** The size of the stored attribute (4 for float or int, 8 for double or long ...) */
  protected final int dataSize;
  /** The binding location of the VBO as specified in the Vertex Shader */
  protected final int location;
  /** The total size of the buffer in bytes */
  protected final long size;

  /** Unbinds the currently bound Vertex Buffer Object */
  public static void unbind() {
    glBindBuffer(GL_ARRAY_BUFFER, 0);
  }

  /**
   * Creates a new Vertex Buffer Object
   *
   * @param location the binding location of the VBO
   * @param dataDimension the dimension of the attribute to link to the VBO, must be <= 4
   * @param capacity the total capacity of the VBO (in number of primitives not in bytes)
   * @param dataSizeBytes the size of the data to be stored
   */
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
    glBufferData(GL_ARRAY_BUFFER, size, GL_DYNAMIC_DRAW);
  }

  /** Binds the Vertex Buffer Object to be sent to the bound Vertex Shader */
  public void bind() {
    glBindBuffer(GL_ARRAY_BUFFER, id);
    LOGGER.trace("Bound VBO {}", id);
  }

  /** Clears the Vertex Buffer Object from VRAM */
  public void cleanUp() {
    glDeleteBuffers(id);
    LOGGER.debug("VBO {} cleaned up", id);
  }

  /**
   * Return the unique identifier of the Vertex Buffer Object as identified by OpenGL
   *
   * @return the unique OpenGL id of the Vertex Buffer Object
   */
  public int getId() {
    return id;
  }

  /**
   * Returns the dimension of the data stored in this Vertex Buffer Object
   *
   * <p>- primitives such as Byte, Short, Integer, Long, Float and Double are 1 <br>
   * - composite such as Vec2, Vec3, Vec4 are respectively 2, 3 and 4
   *
   * @return the size of the data stored in this Vertex Buffer Object
   */
  public int getDataDim() {
    return dataDim;
  }

  /**
   * Returns the binding location of this Vertex Buffer Object as identifier in a Vertex Shader
   *
   * <p><i>layout (location = 0) in *type* *attributeName*;</i>
   *
   * @return the binding location of this Vertex Buffer Object
   */
  public int getLocation() {
    return location;
  }

  /**
   * Returns the total size allocated to this Vertex Buffer Object into VRAM in bytes
   *
   * @return the total allocated size in bytes
   */
  public long getSize() {
    return size;
  }

  /**
   * Buffers a {@link Buffer} into this Vertex Buffer Object, implemented as Generic for sub classed
   * to implement a unique methods or every Buffer type
   *
   * @param data the {@link Buffer} to load
   * @param <B> the type of buffer to Load
   */
  public abstract <B extends Buffer> void buffer(B data);

  /**
   * Buffers a single primitive of data
   *
   * @param data the data to load
   */
  public abstract void buffer(T data);

  /**
   * Loads the currently buffered data into VRAM to be read by the Vertex Shader, must be called
   * after one or more calls to {@link VertexBufferObject#buffer(Buffer)} or {@link
   * VertexBufferObject#buffer(Number)}
   */
  public abstract void load();

  /**
   * Returns the type of primitives stored in this Vertex Buffer Object this method returns the type
   * of primitive and not the type of data for Vec2, Vec3 and Vec4 expect {@link Float} instead of
   * {@link org.joml.Vector2f}
   *
   * @return the type of primitives stored in this Vertex Buffer Object
   */
  public abstract Class<T> getType();

  /**
   * Returns the type of Buffer that this Vertex Buffer Object will accept in its {@link
   * VertexBufferObject#buffer(Buffer)} method
   *
   * @return the type of Buffer accepted by this Vertex Buffer Object
   */
  public abstract Class<? extends Buffer> getBufferType();
}
