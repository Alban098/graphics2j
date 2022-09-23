/*
 * Copyright (c) 2022, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering.scene;

import org.joml.Math;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class Camera {

  private static final float MAX_ZOOM = 1000f;
  private static final float MIN_ZOOM = .1f;

  private final Matrix4f projectionMatrix;
  private final Matrix4f viewMatrix;

  private final Vector2f position;
  private float zoom = 10;

  public Camera(Vector2f position) {
    this.position = position;
    this.projectionMatrix = new Matrix4f();
    this.viewMatrix = new Matrix4f();
  }

  public void move(Vector2f offset) {
    position.add(offset);
  }

  public void moveTo(Vector2f dest) {
    position.set(dest);
  }

  public void move(int x, int y) {
    position.add(x, y);
  }

  public void moveTo(int x, int y) {
    position.set(x, y);
  }

  public void zoomIn() {
    zoom = Math.clamp(MIN_ZOOM, MAX_ZOOM, zoom * 1.5f);
  }

  public void zoomOut() {
    zoom = Math.clamp(MIN_ZOOM, MAX_ZOOM, zoom / 1.5f);
  }

  public void adjustProjection(float aspectRatio) {
    this.projectionMatrix.identity();
    this.projectionMatrix.ortho(0, zoom * aspectRatio, 0, zoom, 0f, 100f);
  }

  public Matrix4f getViewMatrix() {
    Vector3f front = new Vector3f(0f, 0f, 0f);
    Vector3f up = new Vector3f(0f, 1f, 0f);
    this.viewMatrix.identity();
    this.viewMatrix.lookAt(
        new Vector3f(position.x, position.y, 3f), front.add(position.x, position.y, 0f), up);
    return this.viewMatrix;
  }

  public Matrix4f getProjectionMatrix() {
    return projectionMatrix;
  }
}
