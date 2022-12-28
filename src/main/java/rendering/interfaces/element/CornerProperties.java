/*
 * Copyright (c) 2022, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering.interfaces.element;

import org.joml.Vector4f;

public class CornerProperties {

  private final Vector4f data = new Vector4f();

  public CornerProperties() {}

  public CornerProperties(
      float topLeftRadius, float topRightRadius, float bottomLeftRadius, float bottomRightRadius) {
    this.data.x = topLeftRadius;
    this.data.w = topRightRadius;
    this.data.y = bottomLeftRadius;
    this.data.z = bottomRightRadius;
  }

  public void set(
      float topLeftRadius, float topRightRadius, float bottomLeftRadius, float bottomRightRadius) {
    this.data.x = topLeftRadius;
    this.data.w = topRightRadius;
    this.data.y = bottomLeftRadius;
    this.data.z = bottomRightRadius;
  }

  public float getTopLeftRadius() {
    return data.x;
  }

  public void setTopLeftRadius(float topLeftRadius) {
    this.data.x = topLeftRadius;
  }

  public float getTopRightRadius() {
    return data.w;
  }

  public void setTopRightRadius(float topRightRadius) {
    this.data.w = topRightRadius;
  }

  public float getBottomLeftRadius() {
    return data.y;
  }

  public void setBottomLeftRadius(float bottomLeftRadius) {
    this.data.y = bottomLeftRadius;
  }

  public float getBottomRightRadius() {
    return data.z;
  }

  public void setBottomRightRadius(float bottomRightRadius) {
    this.data.z = bottomRightRadius;
  }

  public Vector4f toVec4() {
    return data;
  }
}
