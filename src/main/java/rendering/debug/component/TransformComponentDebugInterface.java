/*
 * Copyright (c) 2022, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering.debug.component;

import imgui.ImGui;
import org.joml.Math;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import rendering.debug.DebugUtils;
import rendering.entities.component.Component;
import rendering.entities.component.TransformComponent;
import rendering.entities.component.TransformUtils;

public class TransformComponentDebugInterface
    implements ComponentDebugInterface<TransformComponent> {

  @Override
  public Class<TransformComponent> getComponentClass() {
    return TransformComponent.class;
  }

  @Override
  public String getDisplayName() {
    return "Transform";
  }

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

  private void drawMatrix(Matrix4f aMatrix, Vector3f aPos) {
    ImGui.newLine();
    ImGui.sameLine(10);
    ImGui.textColored(255, 0, 255, 255, "Position");
    ImGui.sameLine(100);
    ImGui.textColored(255, 255, 0, 255, String.format("%.2f", aPos.x));
    ImGui.sameLine(160);
    ImGui.textColored(255, 255, 0, 255, String.format("%.2f", aPos.y));
    DebugUtils.drawAttrib(
        "Scale", String.format("%.2f", aMatrix.getScale(new Vector3f()).x), 10, 100);
    DebugUtils.drawAttrib(
        "Rotation",
        String.format("%.2f", Math.toDegrees(TransformUtils.getRotationZ(aMatrix))),
        10,
        100);
  }
}
