/*
 * Copyright (c) 2022, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering.entities.component;

import org.joml.Matrix4f;
import org.joml.Vector2f;

public class Transform {

  private final Vector2f displacement;
  private final Matrix4f matrix;
  private float scale;
  private float rotation;

  private Transform parent;

  public Transform() {
    this(new Vector2f(), 1, 0);
  }

  public Transform(Vector2f displacement, float scale, float rotation) {
    this.displacement = displacement;
    this.scale = scale;
    this.rotation = rotation;
    this.matrix = new Matrix4f().identity();
    updateMatrix();
  }

  private void updateMatrix() {
    this.matrix
        .identity()
        .translate(displacement.x, displacement.y, 0)
        .scale(scale)
        .rotateZ(rotation);
  }

  public Vector2f getDisplacement() {
    return new Vector2f(displacement);
  }

  public void moveTo(Vector2f displacement) {
    this.displacement.set(displacement);
    updateMatrix();
  }

  public void moveTo(float x, float y) {
    this.displacement.set(x, y);
    updateMatrix();
  }

  public float getScale() {
    return scale;
  }

  public void setScale(float scale) {
    this.scale = scale;
    updateMatrix();
  }

  public float getRotation() {
    return rotation;
  }

  public void rotateTo(float rotation) {
    this.rotation = rotation;
    updateMatrix();
  }

  public void move(Vector2f displacement) {
    this.displacement.add(displacement);
    updateMatrix();
  }

  public void move(float x, float y) {
    this.displacement.add(x, y);
    updateMatrix();
  }

  public void rotate(float angle) {
    rotation += angle;
    updateMatrix();
  }

  public Matrix4f getMatrix() {
    if (parent != null) {
      return new Matrix4f(parent.getMatrix()).scale(1f / parent.scale).mul(matrix);
    }
    return matrix;
  }

  public void setParent(Transform parent) {
    this.parent = parent;
  }
}
