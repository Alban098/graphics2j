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
  private final Matrix4f relativeMatrix;

  private final Matrix4f absoluteMatrix;

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
    this.scale = scale;
    this.rotation = rotation;

    this.requestedDisplacement = new Vector2f(displacement);
    this.requestedScale = scale;
    this.requestedRotation = rotation;

    this.relativeMatrix = new Matrix4f().identity();
    this.absoluteMatrix = new Matrix4f().identity();

    this.hierarchyStack = new Stack<>();

    updateMatrix();
    updateMatrixAbsolute(null);
  }

  private void setRequestedState() {
    displacement.set(requestedDisplacement);
    scale = requestedScale;
    rotation = requestedRotation;
  }

  private void updateMatrix() {
    relativeMatrix
        .identity()
        .translate(displacement.x, displacement.y, 0)
        .scale(scale)
        .rotateZ(rotation);
  }

  private void updateMatrixAbsolute(Entity parent) {
    hierarchyStack.clear();
    absoluteMatrix.identity();

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
        absoluteMatrix.mul(
            new Matrix4f(current.getComponent(TransformComponent.class).relativeMatrix));
      }
    }

    // Multiply by the current matrix, also account for the case of a null parent, therefor only
    // returning the current matrix
    absoluteMatrix.mul(relativeMatrix);
  }

  public Matrix4f getRelativeMatrix() {
    return relativeMatrix;
  }

  public Matrix4f getAbsoluteMatrix() {
    return absoluteMatrix;
  }

  public void setDisplacement(Vector2f displacement) {
    requestedDisplacement.set(displacement);
  }

  public void setDisplacement(float x, float y) {
    requestedDisplacement.set(x, y);
  }

  public void setScale(float scale) {
    requestedScale = scale;
  }

  public void setRotation(float rotation) {
    requestedRotation = rotation;
  }

  public void move(Vector2f displacement) {
    requestedDisplacement.add(displacement);
  }

  public void move(float x, float y) {
    requestedDisplacement.add(x, y);
  }

  public void scale(float scale) {
    this.requestedScale *= scale;
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

  public FloatBuffer toFloatBuffer(boolean absolute) {
    buffer.clear();
    if (absolute) {
      return buffer.put(absoluteMatrix.get(new float[16])).flip();
    } else {
      return buffer.put(relativeMatrix.get(new float[16])).flip();
    }
  }

  @Override
  public void cleanUp() {
    MemoryUtil.memFree(buffer);
  }

  @Override
  public void update(Entity entity) {
    setRequestedState();
    updateMatrix();
    updateMatrixAbsolute(entity.getParent());
  }
}
