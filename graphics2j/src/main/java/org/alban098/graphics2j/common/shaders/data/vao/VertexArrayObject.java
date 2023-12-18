/*
 * Copyright (c) 2022-2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package org.alban098.graphics2j.common.shaders.data.vao;

import static org.lwjgl.opengl.GL11.glDrawArrays;

import java.util.Map;
import org.alban098.common.Transform;
import org.alban098.common.TransformUtils;
import org.alban098.graphics2j.common.components.RenderElement;
import org.alban098.graphics2j.common.shaders.ShaderAttribute;
import org.alban098.graphics2j.common.shaders.ShaderAttributes;
import org.alban098.graphics2j.common.shaders.ShaderProgram;
import org.alban098.graphics2j.common.shaders.data.ShaderStorageBufferObject;
import org.alban098.graphics2j.common.shaders.data.model.Primitive;
import org.alban098.graphics2j.common.shaders.data.vbo.FloatVertexBufferObject;
import org.alban098.graphics2j.common.shaders.data.vbo.IntegerVertexBufferObject;
import org.alban098.graphics2j.common.shaders.data.vbo.VertexBufferObject;

/**
 * This class represents a Vertex Array Object, a VAO is conceptually a collection of VBO and
 * potentially SSBOs that describe an Object to be rendered by a {@link ShaderProgram}. It will map
 * each {@link ShaderAttribute} to a {@link VertexBufferObject}, and a {@link
 * ShaderStorageBufferObject} for passing Transforms. It can be size to store up to a defined number
 * of objects. It wraps all information needed to rendering and abstract the logic of {@link
 * VertexBufferObject} and {@link ShaderStorageBufferObject}.
 */
public final class VertexArrayObject extends ArrayObject {

  private final Primitive primitive;

  /**
   * Creates a new Vertex Array Object of a specified size
   *
   * @param maxPrimitiveCapacity the maximum number of quads this VAO can store
   * @param transformSSBO does the VAO needs a Transform SSBO
   */
  public VertexArrayObject(int maxPrimitiveCapacity, boolean transformSSBO, Primitive primitive) {
    super(maxPrimitiveCapacity, transformSSBO);
    this.primitive = primitive;
  }

  /**
   * Creates a {@link VertexBufferObject} for a specified {@link ShaderAttribute}
   *
   * @param attribute the {@link ShaderAttribute} to link to the VAO
   */
  @Override
  public void createVBO(ShaderAttribute attribute) {
    Class<?> dataClass = attribute.getDataType();
    if (dataClass.equals(Float.class)) {
      vbos.put(
          attribute,
          new FloatVertexBufferObject(
              attribute.getLocation(), attribute.getDimension(), maxPrimitiveCapacity, primitive));
    } else if (dataClass.equals(Integer.class)) {
      vbos.put(
          attribute,
          new IntegerVertexBufferObject(
              attribute.getLocation(), attribute.getDimension(), maxPrimitiveCapacity, primitive));
    }
  }

  /**
   * Batches a {@link RenderElement} into the VAO if it still has space left, will be transformed by
   * a {@link Transform}
   *
   * @param renderElement the {@link RenderElement} to batch
   * @param transform how to transform the renderElement
   * @return true if the item has been successfully batched, false otherwise
   */
  @Override
  public boolean batch(RenderElement renderElement, Transform transform) {
    // skip if no space left
    if (renderElement != null) {
      if (batchedVertices + renderElement.getModel().getVerticesCount() >= maxPrimitiveCapacity) {
        return false;
      }
      // if transform is needed, buffer it to the SSBO
      if (ssbo != null) {
        if (transform != null) {
          ssbo.buffer(transform.toFloatBuffer());
        } else {
          ssbo.buffer(TransformUtils.getNullTransformBuffer());
        }
      }

      for (Map.Entry<ShaderAttribute, VertexBufferObject<?>> entry : vbos.entrySet()) {
        ShaderAttribute attribute = entry.getKey();
        if (attribute.equals(ShaderAttributes.TRANSFORM_INDEX)
            && attribute.getDataType().equals(Integer.class)) {
          VertexBufferObject<Integer> vbo = (IntegerVertexBufferObject) entry.getValue();
          vbo.buffer(batchedVertices, renderElement.getModel().getVerticesCount());
        } else if (attribute.equals(ShaderAttributes.VERTEX)) {
          renderElement.getModel().fillWithVertices((FloatVertexBufferObject) entry.getValue());
        } else if (attribute.equals(ShaderAttributes.UV)) {
          renderElement.getModel().fillWithUVs((FloatVertexBufferObject) entry.getValue());
        } else {
          VertexBufferObject<?> vbo = entry.getValue();
          vbo.buffer(
              renderElement.get(attribute, vbo.getBufferType()),
              renderElement.getModel().getVerticesCount());
        }
      }
      batchedVertices++;
    }
    return true;
  }

  @Override
  public void drawCall() {
    glDrawArrays(primitive.type, 0, batchedVertices * primitive.verticesCount);
  }
}
