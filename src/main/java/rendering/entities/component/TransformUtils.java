/*
 * Copyright (c) 2022, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering.entities.component;

import java.nio.FloatBuffer;
import org.joml.AxisAngle4f;
import org.joml.Matrix4f;

public class TransformUtils {

  private static final TransformComponent NULL_TRANSFORM = new TransformComponent();

  private TransformUtils() {}

  public static FloatBuffer getNullTransformBuffer() {
    return NULL_TRANSFORM.toFloatBuffer(false);
  }

  public static float getRotationZ(Matrix4f matrix) {
    AxisAngle4f result = new AxisAngle4f();
    matrix.getRotation(result);
    return result.z * result.angle;
  }
}
