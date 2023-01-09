/*
 * Copyright (c) 2022-2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering.data;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

import java.nio.Buffer;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rendering.data.vbo.FloatVertexBufferObject;
import rendering.data.vbo.IntegerVertexBufferObject;
import rendering.data.vbo.VertexBufferObject;
import rendering.entities.component.RenderableComponent;
import rendering.entities.component.TransformComponent;
import rendering.entities.component.TransformUtils;
import rendering.renderers.Renderable;
import rendering.shaders.ShaderAttribute;
import rendering.shaders.ShaderAttributes;

/**
 * This class represents a Vertex Array Object, a VAO is conceptually a collection of VBO and
 * potentially SSBOs that describe an Object to be rendered by a {@link
 * rendering.shaders.ShaderProgram}. It will map each {@link ShaderAttribute} to a {@link
 * VertexBufferObject}, and a {@link ShaderStorageBufferObject} for passing Transforms. It can be
 * size to store up to a defined number of objects. It wraps all information needed to rendering and
 * abstract the logic of {@link VertexBufferObject} and {@link ShaderStorageBufferObject}.
 */
public class VertexArrayObject {

  private static final Logger LOGGER = LoggerFactory.getLogger(VertexArrayObject.class);
  private static final int TRANSFORM_SIZE = 16;

  /**
   * A Map of all {@link ShaderAttribute}s the VAO needs to bound to a {@link
   * rendering.shaders.ShaderProgram} via a {@link VertexBufferObject}
   */
  private final Map<ShaderAttribute, VertexBufferObject<?>> vbos;
  /** The {@link ShaderStorageBufferObject} holding the transforms of each quad */
  private final ShaderStorageBufferObject ssbo;
  /** The id of the VAO, as identified by OpenGL */
  private final int id;
  /** The maximum number of Quad this VAO can batch */
  private final int maxQuadCapacity;
  /** The size of the current batch in number of quads */
  private int batchSize = 0;

  /**
   * Creates a new Vertex Array Object of a specified size
   *
   * @param maxQuadCapacity the maximum number of quads this VAO can store
   * @param transformSSBO does the VAO needs a Transform SSBO
   */
  public VertexArrayObject(int maxQuadCapacity, boolean transformSSBO) {
    id = glGenVertexArrays();
    vbos = new HashMap<>();
    if (transformSSBO) {
      ssbo = new ShaderStorageBufferObject(0, TRANSFORM_SIZE, maxQuadCapacity);
    } else {
      ssbo = null;
    }
    this.maxQuadCapacity = maxQuadCapacity;
    LOGGER.debug("Created VAO with id {} and with a size of {} primitives", id, maxQuadCapacity);
  }

  /**
   * Creates a {@link VertexBufferObject} for a specified {@link ShaderAttribute}
   *
   * @param attribute the {@link ShaderAttribute} to link to the VAO
   */
  public void createVBO(ShaderAttribute attribute) {
    Class<?> dataClass = attribute.getDataType();
    if (dataClass.equals(Float.class)) {
      vbos.put(
          attribute,
          new FloatVertexBufferObject(
              attribute.getLocation(), attribute.getDimension(), maxQuadCapacity));
    } else if (dataClass.equals(Integer.class)) {
      vbos.put(
          attribute,
          new IntegerVertexBufferObject(
              attribute.getLocation(), attribute.getDimension(), maxQuadCapacity));
    }
  }

  /**
   * Batches a {@link Renderable} into the VAO if it still has space left
   *
   * @param renderable the {@link Renderable} to batch
   * @return true if the item has been successfully batched, false otherwise
   */
  public boolean batch(Renderable renderable) {
    // skip if no space left
    if (batchSize >= maxQuadCapacity) {
      return false;
    }
    RenderableComponent renderableComponent = renderable.getRenderable();
    TransformComponent transformComponent = renderable.getTransform();
    if (renderableComponent != null) {

      // if transform is needed, buffer it to the SSBO
      if (ssbo != null) {
        if (transformComponent != null) {
          ssbo.buffer(transformComponent.toFloatBuffer(true));
        } else {
          ssbo.buffer(TransformUtils.getNullTransformBuffer());
        }
      }

      // for each attribute, buffer it to the right VBO
      for (Map.Entry<ShaderAttribute, VertexBufferObject<?>> entry : vbos.entrySet()) {
        ShaderAttribute attribute = entry.getKey();
        if (attribute.equals(ShaderAttributes.INDEX)
            && attribute.getDataType().equals(Integer.class)) {
          VertexBufferObject<Integer> vbo = (VertexBufferObject<Integer>) entry.getValue();
          vbo.buffer(batchSize);
        } else {
          VertexBufferObject<?> vbo = entry.getValue();
          Buffer data = renderableComponent.get(attribute, vbo.getBufferType());
          vbo.buffer(data);
        }
      }
      batchSize++;
      LOGGER.trace("Batched a primitive to VAO {}", id);
    }
    return true;
  }

  /**
   * Batches and draws a {@link Renderable} immediately, if {@link Renderable} are already batched,
   * they will be drawn to
   *
   * @param renderable the {@link Renderable} to draw
   */
  public void immediateDraw(Renderable renderable) {
    if (!batch(renderable)) {
      drawBatched();
      return;
    }
    batch(renderable);
    drawBatched();
  }

  /** Draws all currently batched data to the bound rendering target */
  public void drawBatched() {
    prepareFrame();
    glDrawArrays(GL_POINTS, 0, batchSize);
    LOGGER.debug("Drawn a batch of {} elements", batchSize);
    finalizeFrame();
  }

  /** Clears the VAO by clearing the VBOs and SSBO */
  public void cleanUp() {
    for (VertexBufferObject<?> vbo : vbos.values()) {
      vbo.cleanUp();
    }
    if (ssbo != null) {
      ssbo.cleanUp();
    }
    LOGGER.debug("VAO {} cleaned up", id);
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
  public int getMaxQuadCapacity() {
    return maxQuadCapacity;
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
  private void finalizeFrame() {
    batchSize = 0;
    VertexBufferObject.unbind();
    ShaderStorageBufferObject.unbind();
    glBindVertexArray(0);
  }
}
