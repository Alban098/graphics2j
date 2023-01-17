/*
 * Copyright (c) 2022-2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package org.alban098.engine2j.objects.entities.component;

import java.nio.FloatBuffer;
import org.joml.AxisAngle4f;
import org.joml.Matrix4f;

/** Just a utility class to help with transformation matrix manipulation */
public final class TransformUtils {

  private static final TransformComponent NULL_TRANSFORM = new TransformComponent();

  /** An empty private constructor to block instantiation */
  private TransformUtils() {}

  /**
   * Returns a {@link FloatBuffer} containing a transformation matrix that does not change an Entity
   *
   * @return a {@link FloatBuffer} containing a transformation matrix that does not change an Entity
   */
  public static FloatBuffer getNullTransformBuffer() {
    return NULL_TRANSFORM.toFloatBuffer(false);
  }

  /**
   * Returns the angle of rotation around the Z axis of a transformation matrix in radians
   *
   * @param matrix the matrix to extract from
   * @return the angle of rotation around the Z axis of a transformation matrix in radians
   */
  public static float getRotationZ(Matrix4f matrix) {
    AxisAngle4f result = new AxisAngle4f();
    matrix.getRotation(result);
    return result.z * result.angle;
  }
}
