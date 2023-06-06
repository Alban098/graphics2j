/*
 * Copyright (c) 2022-2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package org.alban098.graphics2j.common.components;

import org.alban098.graphics2j.common.Window;
import org.alban098.graphics2j.input.MouseState;
import org.joml.*;
import org.joml.Math;

/** Represents the Camera from which everything will be rendered */
public final class Camera {

  /** The maximum zoom level of the Camera */
  private static final float MAX_ZOOM = 1000f;
  /** The maximum zoom level of the Camera */
  private static final float MIN_ZOOM = .1f;

  /** The projection matrix of the Camera, used to convert from view space to screen space */
  private final Matrix4f projectionMatrix;
  /** The view matrix of the Camera, used to convert from world space to view space */
  private final Matrix4f viewMatrix;
  /**
   * The inverse of the projection-view matrix, used to compute the Camera's viewport in world space
   * and prevent Entity processing while not on the viewport
   */
  private final Matrix4f inverseTransform;
  /** The current position of the Camera in world space */
  private final Vector2f position;
  /** The rotation of the Camera around the Z axis */
  private float rotation = 0;
  /** The aspect ratio of the viewport of the Camera */
  private float aspectRatio;
  /** The current zoom level of the Camera */
  private float zoom = 10;
  /** A bounding box for the Camera's viewport used for Entity clipping */
  private final Vector4f viewportBoundingBox;

  /**
   * Creates a new Camera at a specified position
   *
   * @param window the window the Camera should render to
   * @param position the position of the Camera in world space
   */
  public Camera(Window window, Vector2f position) {
    this.position = position;
    this.projectionMatrix = new Matrix4f();
    this.viewMatrix = new Matrix4f();
    this.inverseTransform = new Matrix4f();
    this.viewportBoundingBox = new Vector4f(-1, 1, 1, -1);
    adjustProjection(window.getAspectRatio());
  }

  /**
   * Returns whether a point is inside the pseudo viewport of the Camera or not, with a specified
   * tolerance. It does it by looking for an overlap between the bounding box of the viewport and a
   * square centered on the specified point with side 2 * tolerance
   *
   * <p>{@link #computeViewportBoundingBox()} for pseudo viewport computation
   *
   * @param point the point to test
   * @param tolerance the half side of the bounding box around the point
   * @return whether the point is considered inside the Camera's viewport
   */
  public boolean isInsidePseudoViewport(Vector2f point, Vector2f tolerance) {
    return point.x > (viewportBoundingBox.x - tolerance.x / 2)
        && point.x < (viewportBoundingBox.z + tolerance.x / 2)
        && point.y > (viewportBoundingBox.y - tolerance.y / 2)
        && point.y < (viewportBoundingBox.w + tolerance.y / 2);
  }

  /** Calculates the view matrix of the Camera, used to convert from world space to view space */
  public void adjustView() {
    this.viewMatrix.identity();
    this.viewMatrix.lookAt(
        new Vector3f(position.x, position.y, 1f),
        new Vector3f(position.x, position.y, 0f),
        new Vector3f(0f, 1f, 0f).rotateZ(rotation));
  }

  /**
   * Returns the view matrix of the Camera, used to convert from world space to view space
   *
   * @return the view matrix of the Camera, used to convert from world space to view space
   */
  public Matrix4f getViewMatrix() {
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

  /**
   * Updates the Camera's position, rotation ... according to the current Mouse state
   *
   * @param window the {@link Window} in which the Camera is rendering
   * @param mouseInputManager the current state of the Mouse
   */
  public void update(Window window, MouseState mouseInputManager) {
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
    adjustView();
    computeViewportBoundingBox();
  }

  /**
   * Computes the pseudo viewport by computing an orthogonal bounding box around the real viewport
   */
  private void computeViewportBoundingBox() {
    inverseTransform.identity().mul(projectionMatrix).mul(viewMatrix).invert();
    Vector4f topLeft = new Vector4f(-1, 1, 0, 1).mul(inverseTransform);
    Vector4f topRight = new Vector4f(1, 1, 0, 1).mul(inverseTransform);
    Vector4f bottomLeft = new Vector4f(-1, -1, 0, 1).mul(inverseTransform);
    Vector4f bottomRight = new Vector4f(1, -1, 0, 1).mul(inverseTransform);
    float maxX = Math.max(Math.max(Math.max(topLeft.x, topRight.x), bottomRight.x), bottomLeft.x);
    float maxY = Math.max(Math.max(Math.max(topLeft.y, topRight.y), bottomRight.y), bottomLeft.y);
    float minX = Math.min(Math.min(Math.min(topLeft.x, topRight.x), bottomRight.x), bottomLeft.x);
    float minY = Math.min(Math.min(Math.min(topLeft.y, topRight.y), bottomRight.y), bottomLeft.y);
    viewportBoundingBox.set(minX, minY, maxX, maxY);
  }
}
