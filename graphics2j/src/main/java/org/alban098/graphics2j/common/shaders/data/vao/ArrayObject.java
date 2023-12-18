/*
 * Copyright (c) 2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package org.alban098.graphics2j.common.shaders.data.vao;

import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;
import static org.lwjgl.opengl.GL30C.glDeleteVertexArrays;

import java.util.HashMap;
import java.util.Map;
import org.alban098.common.Cleanable;
import org.alban098.common.Transform;
import org.alban098.graphics2j.common.components.RenderElement;
import org.alban098.graphics2j.common.shaders.ShaderAttribute;
import org.alban098.graphics2j.common.shaders.ShaderProgram;
import org.alban098.graphics2j.common.shaders.data.ShaderStorageBufferObject;
import org.alban098.graphics2j.common.shaders.data.vbo.VertexBufferObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ArrayObject implements Cleanable {

  protected static final Logger LOGGER = LoggerFactory.getLogger(ArrayObject.class);

  /** The size of a transform in bytes */
  protected static final int TRANSFORM_SIZE = 16;

  /**
   * A Map of all {@link ShaderAttribute}s the VAO needs to bound to a {@link ShaderProgram} via a
   * {@link VertexBufferObject}
   */
  protected final Map<ShaderAttribute, VertexBufferObject<?>> vbos;

  /** The {@link ShaderStorageBufferObject} holding the transforms of each quad */
  protected final ShaderStorageBufferObject ssbo;
  /** The id of the VAO, as identified by OpenGL */
  protected final int id;
  /** The maximum number of Quad this VAO can batch */
  protected final int maxPrimitiveCapacity;
  /** The size of the current batch in number of quads */
  protected int batchedVertices = 0;

  public ArrayObject(int maxPrimitiveCapacity, boolean transformSSBO) {
    id = glGenVertexArrays();
    vbos = new HashMap<>();
    if (transformSSBO) {
      ssbo = new ShaderStorageBufferObject(0, TRANSFORM_SIZE, maxPrimitiveCapacity);
    } else {
      ssbo = null;
    }
    this.maxPrimitiveCapacity = maxPrimitiveCapacity;
    LOGGER.info(
        "Created VAO with id {} and with a size of {} primitives", id, maxPrimitiveCapacity);
    initialize();
  }

  public abstract boolean batch(RenderElement renderElement, Transform transform);

  public abstract void drawCall();

  public abstract void createVBO(ShaderAttribute attribute);

  /**
   * Batches and draws a {@link RenderElement} immediately, applying a {@link Transform} to it, if
   * {@link RenderElement}s are already batched, they will be drawn to
   *
   * @param renderElement the {@link RenderElement} to draw
   * @param transform how to transform the renderElement
   */
  public void immediateDraw(RenderElement renderElement, Transform transform) {
    if (!batch(renderElement, transform)) {
      drawBatched();
      return;
    }
    batch(renderElement, transform);
    drawBatched();
  }

  /** Draws all currently batched data to the bound rendering target */
  public void drawBatched() {
    prepareFrame();
    drawCall();
    end();
  }

  /** Clears the VAO by clearing the VBOs and SSBO */
  @Override
  public void cleanUp() {
    glDeleteVertexArrays(id);
    LOGGER.info("VAO {} cleaned up", id);
  }

  /**
   * Returns the unique identifier of the Vertex Array Object as identified by OpenGL
   *
   * @return the unique OpenGL id of the Vertex Array Object
   */
  public int getId() {
    return id;
  }

  /**
   * Returns the capacity of the Vertex Array Object, in number of quads
   *
   * @return the capacity of the Vertex Array Object
   */
  public int getMaxPrimitiveCapacity() {
    return maxPrimitiveCapacity;
  }

  /**
   * Returns all {@link VertexBufferObject} currently linked to the VAO
   *
   * @return all linked {@link VertexBufferObject}
   */
  public Map<ShaderAttribute, VertexBufferObject<?>> getVbos() {
    return vbos;
  }

  /**
   * Returns the currently linked Transform {@link ShaderStorageBufferObject}
   *
   * @return the currently linked Transform {@link ShaderStorageBufferObject}
   */
  public ShaderStorageBufferObject getSsbo() {
    return ssbo;
  }

  /** Prepare the frame for render the VAO by binding the VAO and loading all VBOs and SSBO */
  private void prepareFrame() {
    glBindVertexArray(id);
    if (ssbo != null) {
      ssbo.load();
    }
    for (VertexBufferObject<?> vbo : vbos.values()) {
      vbo.load();
    }
  }

  /** Finalize the rendering of the VAO and unbind VBOs, SSBO and VAO */
  private void end() {
    batchedVertices = 0;
    VertexBufferObject.unbind();
    ShaderStorageBufferObject.unbind();
    glBindVertexArray(0);
  }
}
