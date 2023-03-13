/*
 * Copyright (c) 2022-2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package org.alban098.graphics2j.common.components;

import java.nio.FloatBuffer;
import org.alban098.graphics2j.common.Cleanable;
import org.joml.Math;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.lwjgl.system.MemoryUtil;

/**
 * a Concrete implementation a Component allowing an Entity to be moved, scaled and rotated in the
 * world
 */
public final class Transform implements Cleanable {

  /** The transformation matrix with parent transforms applied */
  private final Matrix4f matrix;
  /** A Buffer used to store the absolute transformation matrix for rendering */
  private final FloatBuffer buffer = MemoryUtil.memAllocFloat(16);

  private final float[] matrixArray = new float[16];
  /** The current displacement of the Component */
  private final Vector2f displacement;
  /** The current scale of the Component */
  private final Vector2f scale;
  /** The current rotation of the Component */
  private float rotation;
  /** The displacement to apply at the next update */
  private final Vector2f requestedDisplacement;
  /** The scale to apply at the next update */
  private final Vector2f requestedScale;
  /** The rotation to apply at the next update */
  private float requestedRotation;
  /** A flag to indicate that a change has occurred */
  private boolean change = false;
  /** Creates a new TransformComponent */
  public Transform() {
    this(new Vector2f(), new Vector2f(1, 1), 0);
  }

  /**
   * Creates a new TransformComponent with set values
   *
   * @param displacement the current displacement of the Component
   * @param scale the current scale of the Component, applied to both axis
   * @param rotation the current rotation of the Component
   */
  public Transform(Vector2f displacement, float scale, float rotation) {
    this(displacement, new Vector2f(scale, scale), rotation);
  }

  /**
   * Creates a new TransformComponent with set values
   *
   * @param displacement the current displacement of the Component
   * @param scale the current scale of the Component
   * @param rotation the current rotation of the Component
   */
  public Transform(Vector2f displacement, Vector2f scale, float rotation) {
    this.displacement = new Vector2f(displacement);
    this.scale = scale;
    this.rotation = rotation;

    this.requestedDisplacement = new Vector2f(displacement);
    this.requestedScale = scale;
    this.requestedRotation = rotation;

    this.matrix = new Matrix4f().identity();

    updateMatrix();
  }

  /** Applies the requested state to the current state */
  private void setRequestedState() {
    displacement.set(requestedDisplacement);
    scale.set(requestedScale);
    rotation = requestedRotation;
    change = false;
  }

  /** Recomputes the relative transformation matrix */
  private void updateMatrix() {
    matrix
        .identity()
        .translate(displacement.x, displacement.y, 0)
        .rotateZ(rotation)
        .scale(scale.x, scale.y, 1);
    matrix.get(matrixArray);
  }

  /**
   * Apply a transformation to the current one
   *
   * @param parent the parent transform to apply
   */
  private void applyTransform(Transform parent) {
    matrix.mul(parent.matrix);
  }

  /**
   * Returns the absolute transformation matrix, with all parent transformations applied
   *
   * @return the absolute transformation matrix, with all parent transformations applied
   */
  public Matrix4f getMatrix() {
    return matrix;
  }

  /**
   * Sets the displacement of the Component
   *
   * @param displacement the new displacement of the Component
   */
  public void setDisplacement(Vector2f displacement) {
    requestedDisplacement.set(displacement);
    change = true;
  }

  /**
   * Sets the displacement of the Component
   *
   * @param x the x component of the new displacement
   * @param y the y component of the new displacement
   */
  public void setDisplacement(float x, float y) {
    requestedDisplacement.set(x, y);
    change = true;
  }

  /**
   * Sets the scale of the Component
   *
   * @param scale the new scale of the Component
   */
  public void setScale(Vector2f scale) {
    requestedScale.set(scale);
    change = true;
  }

  /**
   * Sets the scale of the Component
   *
   * @param x the x component of the new scale
   * @param y the y component of the new scale
   */
  public void setScale(float x, float y) {
    requestedScale.set(x, y);
    change = true;
  }

  /**
   * Sets the new rotation of the Component around the Z axis
   *
   * @param rotation the new rotation of the Component around the Z axis
   */
  public void setRotation(float rotation) {
    requestedRotation = rotation;
    change = true;
  }

  /**
   * Move the Component a certain amount from its current position
   *
   * @param offset the amount to move the Component
   */
  public void move(Vector2f offset) {
    requestedDisplacement.add(offset);
    change = true;
  }

  /**
   * Move the Component a certain amount from its current position
   *
   * @param x the x component of the offset vector
   * @param y the y component of the offset vector
   */
  public void move(float x, float y) {
    requestedDisplacement.add(x, y);
    change = true;
  }

  /**
   * Scale the model by a set amount, on both axis
   *
   * @param scale the amount to scale
   */
  public void scale(float scale) {
    this.requestedScale.x *= scale;
    this.requestedScale.y *= scale;
    change = true;
  }

  /**
   * Scale the model by a set amount
   *
   * @param scale the amount to scale
   */
  public void scale(Vector2f scale) {
    this.requestedScale.x *= scale.x;
    this.requestedScale.y *= scale.y;
    change = true;
  }

  /**
   * Rotates the component by a certain amount, and then wrap the rotation between 0 and 2*PI
   *
   * @param angle the amount to rotate
   */
  public void rotate(float angle) {
    requestedRotation += angle;
    while (requestedRotation < 0) {
      requestedRotation += 2 * Math.PI;
    }
    while (requestedRotation > 2 * Math.PI) {
      requestedRotation -= 2 * Math.PI;
    }
    change = true;
  }

  /**
   * Returns a {@link java.nio.Buffer} containing a transformation matrix
   *
   * @return a {@link java.nio.Buffer} containing the transformation matrix
   */
  public FloatBuffer toFloatBuffer() {
    buffer.clear();
    return buffer.put(matrixArray).flip();
  }

  /** Clears the Component and its buffer */
  @Override
  public void cleanUp() {
    MemoryUtil.memFree(buffer);
  }

  /** Updates the Component by recomputing its matrix */
  public void commit() {
    if (change) {
      setRequestedState();
      updateMatrix();
    }
  }

  public Vector2f getDisplacement() {
    return displacement;
  }

  public Vector2f getScale() {
    return scale;
  }
}
