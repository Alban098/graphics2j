/*
 * Copyright (c) 2022, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering.entities.component;

import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;
import org.joml.Matrix2f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.system.MemoryUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rendering.Texture;
import rendering.entities.Entity;
import rendering.shaders.ShaderAttribute;
import rendering.shaders.ShaderAttributes;

public class RenderableComponent extends Component {

  private static final Logger LOGGER = LoggerFactory.getLogger(RenderableComponent.class);
  private final Texture texture;
  // ONLY Floats are supported yet, even for indices, this is not optimal but allow the reuse of a
  // single FloatBuffer when rendering
  private final Map<ShaderAttribute, FloatBuffer> attributes;

  public RenderableComponent() {
    this.texture = null;
    this.attributes = new HashMap<>();
  }

  public RenderableComponent(Vector3f color) {
    this.texture = null;
    this.attributes = new HashMap<>();
    setAttributeValue(ShaderAttributes.COLOR_ATTRIBUTE, color);
  }

  public RenderableComponent(Texture texture) {
    this.texture = texture;
    this.attributes = new HashMap<>();
  }

  public Texture getTexture() {
    return texture;
  }

  public void setAttributeValue(ShaderAttribute attribute, float data) {
    if (this.attributes.containsKey(attribute)) {
      FloatBuffer buffer = this.attributes.get(attribute);
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

  public void setAttributeValue(ShaderAttribute attribute, Vector2f data) {
    if (this.attributes.containsKey(attribute)) {
      FloatBuffer buffer = this.attributes.get(attribute);
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

  public void setAttributeValue(ShaderAttribute attribute, Vector3f data) {
    if (this.attributes.containsKey(attribute)) {
      FloatBuffer buffer = this.attributes.get(attribute);
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

  public void setAttributeValue(ShaderAttribute attribute, Vector4f data) {
    if (this.attributes.containsKey(attribute)) {
      FloatBuffer buffer = this.attributes.get(attribute);
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

  public void setAttributeValue(ShaderAttribute attribute, Matrix2f data) {
    if (this.attributes.containsKey(attribute)) {
      FloatBuffer buffer = this.attributes.get(attribute);
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

  public FloatBuffer get(ShaderAttribute attribute) {
    return attributes.get(attribute).flip();
  }

  @Override
  public void cleanUp() {
    if (texture != null) {
      texture.cleanup();
    }
    attributes.values().forEach(MemoryUtil::memFree);
  }

  @Override
  public void update(Entity entity) {}
}
