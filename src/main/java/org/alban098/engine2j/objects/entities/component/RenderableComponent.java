/*
 * Copyright (c) 2022-2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package org.alban098.engine2j.objects.entities.component;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;
import org.alban098.engine2j.core.Engine;
import org.alban098.engine2j.objects.entities.Entity;
import org.alban098.engine2j.shaders.ShaderAttribute;
import org.alban098.engine2j.shaders.ShaderAttributes;
import org.alban098.engine2j.shaders.data.Texture;
import org.joml.Matrix2f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.system.MemoryUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Concrete implementation of a Component allowing an Entity ot be rendered by the {@link Engine}
 */
public final class RenderableComponent extends Component {

  private static final Logger LOGGER = LoggerFactory.getLogger(RenderableComponent.class);

  /**
   * The {@link Texture} of the Component, can be null if plain color mode is enabled for the
   * Component
   */
  private Texture texture;
  /** A Map of all {@link ShaderAttribute}s and their buffers needed for rendering */
  private final Map<ShaderAttribute, java.nio.Buffer> attributes;

  /** Creates a new RenderableComponent, with no texture and no color */
  public RenderableComponent() {
    this.texture = null;
    this.attributes = new HashMap<>();
  }

  /**
   * Creates a new RenderableComponent with plain color mode enabled
   *
   * @param color the background color of the Component
   */
  public RenderableComponent(Vector4f color) {
    this.texture = null;
    this.attributes = new HashMap<>();
    setAttributeValue(ShaderAttributes.COLOR_ATTRIBUTE, color);
  }

  /**
   * Creates a new textured RenderableComponent
   *
   * @param texture the {@link Texture} of the Component
   */
  public RenderableComponent(Texture texture) {
    this.texture = texture;
    this.attributes = new HashMap<>();
  }

  /**
   * Returns the {@link Texture} of the Component
   *
   * @return the {@link Texture} of the Component, null if plain color mode
   */
  public Texture getTexture() {
    return texture;
  }

  /**
   * Sets a {@link Texture} for the Component, disabling plain color mode or enabling it if null
   *
   * @param texture the {@link Texture} to set
   */
  public void setTexture(Texture texture) {
    this.texture = texture;
  }

  /**
   * Sets the integer value of a {@link ShaderAttribute} of this Component
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
    LOGGER.debug("Set Attribute {} to value {}", attribute.getName(), data);
  }

  /**
   * Sets the float value of a {@link ShaderAttribute} of this Component
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
    LOGGER.debug("Set Attribute {} to value {}", attribute.getName(), data);
  }

  /**
   * Sets the 2D float vector value of a {@link ShaderAttribute} of this Component
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
    LOGGER.debug("Set Attribute {} to value {}", attribute.getName(), data);
  }

  /**
   * Sets the 3D float vector value of a {@link ShaderAttribute} of this Component
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
    LOGGER.debug("Set attribute {} to value {}", attribute.getName(), data);
  }

  /**
   * Sets the 4D float vector value of a {@link ShaderAttribute} of this Component
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
    LOGGER.debug("Set Attribute {} to value {}", attribute.getName(), data);
  }

  /**
   * Sets the 2x2 float matrix value of a {@link ShaderAttribute} of this Component
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
    LOGGER.debug("Set Attribute {} to value {}", attribute.getName(), data);
  }

  /**
   * Returns the buffer of a {@link ShaderAttribute} of this Component
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

  /** Clears the Component by clearing its {@link Texture} and {@link ShaderAttribute}s */
  @Override
  public void cleanUp() {
    if (texture != null) {
      texture.cleanup();
    }
    attributes.values().forEach(MemoryUtil::memFree);
  }

  /**
   * Updates the Component, nothing to do here
   *
   * @param entity the parent {@link Entity} of the Component
   * @param elapsedTime the elapsed time since last update in seconds
   */
  @Override
  public void update(Entity entity, double elapsedTime) {}
}
