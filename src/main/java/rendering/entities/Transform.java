/*
 * Copyright (c) 2022, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering.entities;

import org.joml.Vector2f;

public class Transform {

  public static final Transform DEFAULT = new Transform();

  private Vector2f displacement;
  private float scale;
  private float rotation;

  public Transform() {
    this.displacement = new Vector2f();
    this.scale = 1;
    this.rotation = 0;
  }

  public Transform(Vector2f displacement, float scale, float rotation) {
    this.displacement = displacement;
    this.scale = scale;
    this.rotation = rotation;
  }

  public Vector2f getDisplacement() {
    return displacement;
  }

  public void moveTo(Vector2f displacement) {
    this.displacement = displacement;
  }

  public float getScale() {
    return scale;
  }

  public void setScale(float scale) {
    this.scale = scale;
  }

  public float getRotation() {
    return rotation;
  }

  public void rotateTo(float rotation) {
    this.rotation = rotation;
  }
}
