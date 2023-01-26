/*
 * Copyright (c) 2022-2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package org.alban098.engine2j.core.debug.component;

import imgui.ImGui;
import org.alban098.engine2j.core.debug.DebugUtils;
import org.alban098.engine2j.core.objects.entities.component.Component;
import org.alban098.engine2j.core.objects.entities.component.TransformComponent;
import org.alban098.engine2j.core.objects.entities.component.TransformUtils;
import org.joml.Math;
import org.joml.Matrix4f;
import org.joml.Vector3f;

/**
 * A concrete implementation of {@link ComponentDebugInterface} in charge of displaying {@link
 * TransformComponent}s
 */
public final class TransformComponentDebugInterface
    implements ComponentDebugInterface<TransformComponent> {

  /**
   * Returns the class type of the {@link Component}
   *
   * @return TransformComponent.class
   */
  @Override
  public Class<TransformComponent> getComponentClass() {
    return TransformComponent.class;
  }

  /**
   * Returns the name of the {@link Component}
   *
   * @return "Transform"
   */
  @Override
  public String getDisplayName() {
    return "Transform";
  }

  /**
   * Draws the interface into its container
   *
   * @param component the {@link Component} to display in the interface
   * @return true the component is a {@link TransformComponent} false otherwise
   */
  @Override
  public boolean draw(Component component) {
    if (component instanceof TransformComponent) {
      TransformComponent transformComponent = (TransformComponent) component;
      ImGui.beginChild("##" + component.hashCode(), 250, 150);
      Matrix4f aMatrix = transformComponent.getAbsoluteMatrix();
      Vector3f aPos = aMatrix.getTranslation(new Vector3f());
      Matrix4f rMatrix = transformComponent.getRelativeMatrix();
      Vector3f rPos = rMatrix.getTranslation(new Vector3f());
      ImGui.textColored(255, 0, 0, 255, "Absolute");
      drawMatrix(aMatrix, aPos);
      ImGui.separator();
      ImGui.textColored(255, 0, 0, 255, "Relative");
      drawMatrix(rMatrix, rPos);
      ImGui.endChild();
      return true;
    }
    return false;
  }

  /**
   * Draws a transformation matrix and displacement attributes as
   *
   * <ul>
   *   <li>position
   *   <li>scale
   *   <li>rotation around Z axis
   * </ul>
   *
   * @param aMatrix the matrix to display
   * @param aPos the override position to display
   */
  private void drawMatrix(Matrix4f aMatrix, Vector3f aPos) {
    ImGui.newLine();
    ImGui.sameLine(10);
    ImGui.textColored(255, 0, 255, 255, "Position");
    ImGui.sameLine(100);
    ImGui.textColored(255, 255, 0, 255, String.format("%.2f", aPos.x));
    ImGui.sameLine(160);
    ImGui.textColored(255, 255, 0, 255, String.format("%.2f", aPos.y));

    Vector3f scale = new Vector3f();
    aMatrix.getScale(scale);
    ImGui.newLine();
    ImGui.sameLine(10);
    ImGui.textColored(255, 0, 255, 255, "Position");
    ImGui.sameLine(100);
    ImGui.textColored(255, 255, 0, 255, String.format("%.2f", scale.x));
    ImGui.sameLine(160);
    ImGui.textColored(255, 255, 0, 255, String.format("%.2f", scale.y));

    DebugUtils.drawAttrib(
        "Rotation",
        String.format("%.2f", Math.toDegrees(TransformUtils.getRotationZ(aMatrix))),
        10,
        100);
  }
}
