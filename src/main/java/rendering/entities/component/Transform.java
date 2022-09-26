/*
 * Copyright (c) 2022, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering.entities.component;

import org.joml.Vector2f;

public class Transform {

  private final Vector2f displacement;
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
    return new Vector2f(displacement);
  }

  public void moveTo(Vector2f displacement) {
    this.displacement.set(displacement);
  }

  public void moveTo(float x, float y) {
    this.displacement.set(x, y);
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

  public void move(Vector2f displacement) {
    this.displacement.add(displacement);
  }

  public void move(float x, float y) {
    this.displacement.add(x, y);
  }

  public void set(Transform transform) {
    displacement.set(transform.displacement);
    scale = transform.scale;
    rotation = transform.getRotation();
  }

  public void rotate(float angle) {
    rotation += angle;
  }
}
