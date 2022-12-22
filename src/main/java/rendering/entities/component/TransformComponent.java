/*
 * Copyright (c) 2022, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering.entities.component;

import java.nio.FloatBuffer;
import java.util.Stack;
import org.joml.Math;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.lwjgl.system.MemoryUtil;
import rendering.entities.Entity;

public class TransformComponent extends Component {

  /** The actual transformation matrix */
  private final Matrix4f matrix;

  private final FloatBuffer buffer = MemoryUtil.memAllocFloat(16);

  /** Transformation values, updated everytime update is called */
  private final Vector2f displacement;

  private float scale;
  private float rotation;

  /**
   * Buffers used to cache all transformation request and process them once when update is called
   */
  private final Vector2f requestedDisplacement;

  private float requestedScale;
  private float requestedRotation;

  private final Stack<Entity> hierarchyStack;

  public TransformComponent() {
    this(new Vector2f(), 1, 0);
  }

  public TransformComponent(Vector2f displacement, float scale, float rotation) {
    this.displacement = new Vector2f(displacement);
    this.requestedDisplacement = new Vector2f(displacement);
    this.scale = scale;
    this.requestedScale = scale;
    this.rotation = rotation;
    this.requestedRotation = rotation;
    this.matrix = new Matrix4f().identity();
    this.hierarchyStack = new Stack<>();
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
    this.requestedDisplacement.set(displacement);
  }

  public void moveTo(float x, float y) {
    this.requestedDisplacement.set(x, y);
  }

  public float getScale() {
    return scale;
  }

  public void setScale(float scale) {
    this.requestedScale = scale;
  }

  public float getRotation() {
    return rotation;
  }

  public void rotateTo(float rotation) {
    this.requestedRotation = rotation;
  }

  public void move(Vector2f displacement) {
    this.requestedDisplacement.add(displacement);
  }

  public void move(float x, float y) {
    this.requestedDisplacement.add(x, y);
  }

  public void rotate(float angle) {
    requestedRotation += angle;
    while (requestedRotation < 0) {
      requestedRotation += 2 * Math.PI;
    }
    while (requestedRotation > 2 * Math.PI) {
      requestedRotation -= 2 * Math.PI;
    }
  }

  public Matrix4f getMatrix() {
    return matrix;
  }

  public Matrix4f getMatrixRecursive(Entity parent) {
    hierarchyStack.clear();
    Matrix4f m = new Matrix4f().identity();

    Entity current = parent;
    // Create a stack of the parenting hierarchy
    while (current != null) {
      hierarchyStack.push(current);
      current = current.getParent();
    }

    // Iterate through the stack from the top, and multiply each transformation matrix
    while (!hierarchyStack.empty()) {
      current = hierarchyStack.pop();
      if (current.hasComponent(TransformComponent.class)) {
        m.mul(new Matrix4f(current.getComponent(TransformComponent.class).getMatrix()));
      }
    }

    // Multiply by the current matrix, also account for the case of a null parent, therefor only
    // returning the current matrix
    return m.mul(matrix);
  }

  public FloatBuffer toFloatBuffer(Entity parent) {
    buffer.clear();
    return buffer.put(getMatrixRecursive(parent).get(new float[16])).flip();
  }

  @Override
  public void cleanUp() {
    MemoryUtil.memFree(buffer);
  }

  @Override
  public void update(Entity entity) {
    displacement.set(requestedDisplacement);
    scale = requestedScale;
    rotation = requestedRotation;
    updateMatrix();
  }
}
