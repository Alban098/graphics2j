/*
 * Copyright (c) 2022-2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package org.alban098.engine2j.common.components;

import org.alban098.engine2j.common.Window;
import org.alban098.engine2j.engine.Engine;
import org.alban098.engine2j.input.MouseInputManager;
import org.joml.Math;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

/** Represents the Camera from which the {@link Engine} will render */
public final class Camera {

  /** The maximum zoom level of the Camera */
  private static final float MAX_ZOOM = 1000f;
  /** The maximum zoom level of the Camera */
  private static final float MIN_ZOOM = .1f;

  /** The projection matrix of the Camera, used to convert from view space to screen space */
  private final Matrix4f projectionMatrix;
  /** The view matrix of the Camera, used to convert from world space to view space */
  private final Matrix4f viewMatrix;
  /** The current position of the Camera in world space */
  private final Vector2f position;
  /** The rotation of the Camera around the Z axis */
  private float rotation = 0;
  /** The aspect ratio of the viewport of the Camera */
  private float aspectRatio;
  /** The current zoom level of the Camera */
  private float zoom = 10;

  /**
   * Creates a new Camera at a specified position
   *
   * @param position the position of the Camera in world space
   */
  public Camera(Window window, Vector2f position) {
    this.position = position;
    this.projectionMatrix = new Matrix4f();
    this.viewMatrix = new Matrix4f();
    adjustProjection(window.getAspectRatio());
  }

  /**
   * Calculates and returns the view matrix of the Camera, used to convert from world space to view
   * space
   *
   * @return the view matrix of the Camera, used to convert from world space to view space
   */
  public Matrix4f getViewMatrix() {
    this.viewMatrix.identity();
    this.viewMatrix.lookAt(
        new Vector3f(position.x, position.y, 1f),
        new Vector3f(position.x, position.y, 0f),
        new Vector3f(0f, 1f, 0f).rotateZ(rotation));
    return this.viewMatrix;
  }

  /**
   * Returns the projection matrix of the Camera, used to convert from world space to view space
   *
   * @return the projection matrix of the Camera, used to convert from world space to view space
   */
  public Matrix4f getProjectionMatrix() {
    return projectionMatrix;
  }

  /**
   * Returns the current zoom level of the Camera
   *
   * @return the current zoom level of the Camera
   */
  public float getZoom() {
    return zoom;
  }

  /**
   * Move the Camera a certain amount from its current position
   *
   * @param offset the amount to move the Camera
   */
  public void move(Vector2f offset) {
    float sin = Math.sin(rotation);
    float cos = Math.cosFromSin(sin, rotation);
    offset.set(offset.x * cos - offset.y * sin, offset.x * sin + offset.y * cos);
    position.add(offset);
  }

  /**
   * Move the Camera to a set position
   *
   * @param target where to move the Camera
   */
  public void moveTo(Vector2f target) {
    position.set(target);
  }

  /**
   * Rotates the Camera a certain amount from its current rotation around the Z axis
   *
   * @param offset the amount to rotate the Camera around the Z axis
   */
  public void rotate(float offset) {
    rotation += offset;
  }

  /**
   * Rotates the Camera to a certain rotation around the Z axis
   *
   * @param target the new rotation of the Camera around the Z axis
   */
  public void rotateTo(float target) {
    rotation = target;
  }

  /**
   * Multiply the current zoom level by a certain amount
   *
   * @param factor how much to multiply the current zoom level by a certain amount
   */
  public void zoom(float factor) {
    zoom = Math.clamp(MIN_ZOOM, MAX_ZOOM, zoom * factor);
    adjustProjection(aspectRatio);
  }

  /**
   * Recompute the projection matrix to fit a certain aspect ratio
   *
   * @param aspectRatio the aspect ratio to fit
   */
  public void adjustProjection(float aspectRatio) {
    this.aspectRatio = aspectRatio;
    this.projectionMatrix.identity();
    this.projectionMatrix.ortho(
        -zoom * aspectRatio / 2, zoom * aspectRatio / 2, -zoom / 2, zoom / 2, 0f, 1f);
  }

  public void update(Window window, MouseInputManager mouseInputManager) {
    if (window.isResized()) {
      adjustProjection(window.getAspectRatio());
    }

    if (mouseInputManager.canTakeControl(this)) {
      if (mouseInputManager.isLeftButtonPressed()) {
        mouseInputManager.halt(this);
        Vector2f pan =
            mouseInputManager.getDisplacementVector().div(window.getHeight()).mul(getZoom());
        pan.x = -pan.x;
        move(pan);
      }

      if (mouseInputManager.isRightButtonPressed()) {
        mouseInputManager.halt(this);
        float rotation = mouseInputManager.getDisplacementVector().y;
        rotate((float) (rotation / java.lang.Math.PI / 128f));
      }

      if (mouseInputManager.getScrollOffset() != 0) {
        mouseInputManager.halt(this);
        zoom(1 - mouseInputManager.getScrollOffset() / 10);
      }
    }
    if (mouseInputManager.hasControl(this)
        && !mouseInputManager.isLeftButtonPressed()
        && !mouseInputManager.isRightButtonPressed()
        && mouseInputManager.getScrollOffset() == 0) {
      mouseInputManager.release();
    }
  }
}