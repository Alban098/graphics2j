/*
 * Copyright (c) 2022, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering.scene;

import org.joml.*;
import org.joml.Math;

public class Camera {

  private static final Vector3f UP = new Vector3f(0f, 1f, 0f);

  private static final float MAX_ZOOM = 1000f;
  private static final float MIN_ZOOM = .1f;

  private final Matrix4f projectionMatrix;
  private final Matrix4f viewMatrix;

  private final Vector2f position;
  private float rotation = 0;
  private float aspectRatio;
  private float zoom = 10;

  public Camera(Vector2f position) {
    this.position = position;
    this.projectionMatrix = new Matrix4f();
    this.viewMatrix = new Matrix4f();
  }

  public Matrix4f getViewMatrix() {
    this.viewMatrix.identity();
    this.viewMatrix.lookAt(
        new Vector3f(position.x, position.y, 1f),
        new Vector3f(position.x, position.y, 0f),
        new Vector3f(0f, 1f, 0f).rotateZ(rotation));
    return this.viewMatrix;
  }

  public Matrix4f getProjectionMatrix() {
    return projectionMatrix;
  }

  public float getZoom() {
    return zoom;
  }

  public void move(Vector2f offset) {
    position.add(offset);
  }

  public void moveTo(Vector2f target) {
    position.set(target);
  }

  public void rotate(float offset) {
    rotation += offset;
  }

  public void rotateTo(float target) {
    rotation = target;
  }

  public void zoom(float factor) {
    zoom = Math.clamp(MIN_ZOOM, MAX_ZOOM, zoom * factor);
    adjustProjection(aspectRatio);
  }

  public void adjustProjection(float aspectRatio) {
    this.aspectRatio = aspectRatio;
    this.projectionMatrix.identity();
    this.projectionMatrix.ortho(
        -zoom * aspectRatio / 2, zoom * aspectRatio / 2, -zoom / 2, zoom / 2, 0f, 1f);
  }
}
