/*
 * Copyright (c) 2022-2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package org.alban098.graphics2j.common.components;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;
import org.alban098.common.Cleanable;
import org.alban098.graphics2j.common.shaders.ShaderAttribute;
import org.alban098.graphics2j.common.shaders.ShaderAttributes;
import org.alban098.graphics2j.common.shaders.data.Texture;
import org.alban098.graphics2j.common.shaders.data.model.Model;
import org.joml.Matrix2f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.system.MemoryUtil;

// TODO Introduce models, that holds a number of primitives, that can be rendered by a
// VertexArrayObject

/** an Element allowing something to be rendered */
public final class RenderElement implements Cleanable {

  /** The {@link Texture} of the Element, can be null if plain color mode is enabled */
  private Texture texture;
  /** A Map of all {@link ShaderAttribute}s and their buffers needed for rendering */
  private final Map<ShaderAttribute, java.nio.Buffer> attributes;

  private final Model model;

  /** Creates a new RenderElement, with no texture and no color */
  public RenderElement(Model model) {
    this.texture = null;
    this.attributes = new HashMap<>();
    this.model = model;
    initialize();
  }

  /**
   * Creates a new RenderElement with plain color mode enabled
   *
   * @param color the background color of the Element
   */
  public RenderElement(Vector4f color, Model model) {
    this.texture = null;
    this.attributes = new HashMap<>();
    this.model = model;
    setAttributeValue(ShaderAttributes.COLOR_ATTRIBUTE, color);
    initialize();
  }

  /**
   * Creates a new textured RenderElement
   *
   * @param texture the {@link Texture} of the Element
   */
  public RenderElement(Texture texture, Model model) {
    this.texture = texture;
    this.attributes = new HashMap<>();
    this.model = model;
    initialize();
  }

  /**
   * Returns the {@link Texture} of the Element
   *
   * @return the {@link Texture} of the Element, null if plain color mode
   */
  public Texture getTexture() {
    return texture;
  }

  /**
   * Sets a {@link Texture} for the Element, disabling plain color mode or enabling it if null
   *
   * @param texture the {@link Texture} to set
   */
  public void setTexture(Texture texture) {
    this.texture = texture;
  }

  /**
   * Sets the integer value of a {@link ShaderAttribute} of this Element
   *
   * @param attribute the {@link ShaderAttribute} to set the value of
   * @param data the new value
   */
  public void setAttributeValue(ShaderAttribute attribute, int data) {
    if (this.attributes.containsKey(attribute)) {
      IntBuffer buffer = (IntBuffer) this.attributes.get(attribute);
      if (buffer.capacity() != 1) {
        MemoryUtil.memFree(buffer);
        buffer = MemoryUtil.memAllocInt(1);
        buffer.put(data);
        this.attributes.put(attribute, buffer);
      } else {
        buffer.clear();
        buffer.put(data);
      }
    } else {
      IntBuffer buffer = MemoryUtil.memAllocInt(1);
      buffer.put(data);
      this.attributes.put(attribute, buffer);
    }
  }

  /**
   * Sets the float value of a {@link ShaderAttribute} of this Element
   *
   * @param attribute the {@link ShaderAttribute} to set the value of
   * @param data the new value
   */
  public void setAttributeValue(ShaderAttribute attribute, float data) {
    if (this.attributes.containsKey(attribute)) {
      FloatBuffer buffer = (FloatBuffer) this.attributes.get(attribute);
      if (buffer.capacity() != 1) {
        MemoryUtil.memFree(buffer);
        buffer = MemoryUtil.memAllocFloat(1);
        buffer.put(data);
        this.attributes.put(attribute, buffer);
      } else {
        buffer.clear();
        buffer.put(data);
      }
    } else {
      FloatBuffer buffer = MemoryUtil.memAllocFloat(1);
      buffer.put(data);
      this.attributes.put(attribute, buffer);
    }
  }

  /**
   * Sets the 2D float vector value of a {@link ShaderAttribute} of this Element
   *
   * @param attribute the {@link ShaderAttribute} to set the value of
   * @param data the new value
   */
  public void setAttributeValue(ShaderAttribute attribute, Vector2f data) {
    if (this.attributes.containsKey(attribute)) {
      FloatBuffer buffer = (FloatBuffer) this.attributes.get(attribute);
      if (buffer.capacity() < 2) {
        MemoryUtil.memFree(buffer);
        buffer = MemoryUtil.memAllocFloat(2);
        buffer.put(new float[] {data.x, data.y});
        this.attributes.put(attribute, buffer);
      } else {
        buffer.clear();
        buffer.put(new float[] {data.x, data.y});
      }
    } else {
      FloatBuffer buffer = MemoryUtil.memAllocFloat(2);
      buffer.put(new float[] {data.x, data.y});
      this.attributes.put(attribute, buffer);
    }
  }

  /**
   * Sets the 3D float vector value of a {@link ShaderAttribute} of this Element
   *
   * @param attribute the {@link ShaderAttribute} to set the value of
   * @param data the new value
   */
  public void setAttributeValue(ShaderAttribute attribute, Vector3f data) {
    if (this.attributes.containsKey(attribute)) {
      FloatBuffer buffer = (FloatBuffer) this.attributes.get(attribute);
      if (buffer.capacity() < 3) {
        MemoryUtil.memFree(buffer);
        buffer = MemoryUtil.memAllocFloat(3);
        buffer.put(new float[] {data.x, data.y, data.z});
        this.attributes.put(attribute, buffer);
      } else {
        buffer.clear();
        buffer.put(new float[] {data.x, data.y, data.z});
      }
    } else {
      FloatBuffer buffer = MemoryUtil.memAllocFloat(3);
      buffer.put(new float[] {data.x, data.y, data.z});
      this.attributes.put(attribute, buffer);
    }
  }

  /**
   * Sets the 4D float vector value of a {@link ShaderAttribute} of this Element
   *
   * @param attribute the {@link ShaderAttribute} to set the value of
   * @param data the new value
   */
  public void setAttributeValue(ShaderAttribute attribute, Vector4f data) {
    if (this.attributes.containsKey(attribute)) {
      FloatBuffer buffer = (FloatBuffer) this.attributes.get(attribute);
      if (buffer.capacity() < 4) {
        MemoryUtil.memFree(buffer);
        buffer = MemoryUtil.memAllocFloat(4);
        buffer.put(new float[] {data.x, data.y, data.z, data.w});
        this.attributes.put(attribute, buffer);
      } else {
        buffer.clear();
        buffer.put(new float[] {data.x, data.y, data.z, data.w});
      }
    } else {
      FloatBuffer buffer = MemoryUtil.memAllocFloat(4);
      buffer.put(new float[] {data.x, data.y, data.z, data.w});
      this.attributes.put(attribute, buffer);
    }
  }

  /**
   * Sets the 2x2 float matrix value of a {@link ShaderAttribute} of this Element
   *
   * @param attribute the {@link ShaderAttribute} to set the value of
   * @param data the new value
   */
  public void setAttributeValue(ShaderAttribute attribute, Matrix2f data) {
    if (this.attributes.containsKey(attribute)) {
      FloatBuffer buffer = (FloatBuffer) this.attributes.get(attribute);
      if (buffer.capacity() < 4) {
        MemoryUtil.memFree(buffer);
        buffer = MemoryUtil.memAllocFloat(4);
        buffer.put(data.get(new float[4]));
        this.attributes.put(attribute, buffer);
      } else {
        buffer.clear();
        buffer.put(data.get(new float[4]));
      }
    } else {
      FloatBuffer buffer = MemoryUtil.memAllocFloat(4);
      buffer.put(data.get(new float[4]));
      this.attributes.put(attribute, buffer);
    }
  }

  /**
   * Returns the buffer of a {@link ShaderAttribute} of this Element
   *
   * @param attribute the {@link ShaderAttribute} to retrieve the buffer of
   * @param type the class type of {@link java.nio.Buffer} to retrieve as
   * @return the {@link java.nio.Buffer} of the attribute, flip for rendering, null if the {@link
   *     ShaderAttribute} isn't present
   * @param <T> the type of {@link java.nio.Buffer} to retrieve as
   */
  public <T extends java.nio.Buffer> T get(ShaderAttribute attribute, Class<T> type) {
    return (T) attributes.get(attribute).flip();
  }

  /** Clears the Element by clearing its {@link Texture} and {@link ShaderAttribute}s */
  @Override
  public void cleanUp() {
    attributes.values().forEach(MemoryUtil::memFree);
  }

  public Model getModel() {
    return model;
  }
}
