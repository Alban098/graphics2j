/*
 * Copyright (c) 2022, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering.entities.component;

import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;
import org.joml.*;
import org.lwjgl.system.MemoryUtil;
import rendering.Texture;
import rendering.shaders.ShaderAttribute;
import rendering.shaders.ShaderAttributes;

public class Renderable {

  private final Texture texture;
  private final Map<ShaderAttribute, FloatBuffer> attributes;
  private final Transform transform;
  private final FloatBuffer transformBuffer = MemoryUtil.memAllocFloat(16);

  public Renderable(Transform transform) {
    this(transform, null);
  }

  public Renderable(Transform transform, Texture texture) {
    this.texture = texture;
    this.attributes = new HashMap<>();
    this.transform = transform;
  }

  public Texture getTexture() {
    return texture;
  }

  public void setAttributes(ShaderAttribute attribute, float data) {
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
  }

  public void setAttributes(ShaderAttribute attribute, Vector2f data) {
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
  }

  public void setAttributes(ShaderAttribute attribute, Vector3f data) {
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
  }

  public void setAttributes(ShaderAttribute attribute, Vector4f data) {
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
  }

  public void setAttributes(ShaderAttribute attribute, Matrix2f data) {
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
  }

  public void cleanUp() {
    if (texture != null) {
      texture.cleanup();
    }

    for (FloatBuffer buffer : attributes.values()) {
      MemoryUtil.memFree(buffer);
    }

    MemoryUtil.memFree(transformBuffer);
  }

  public FloatBuffer get(ShaderAttribute attribute) {
    if (attribute.equals(ShaderAttributes.TRANSFORMS)) {
      transformBuffer.clear();
      return transformBuffer.put(transform.getMatrix().get(new float[16])).flip();
    }
    return attributes.get(attribute).flip();
  }
}
