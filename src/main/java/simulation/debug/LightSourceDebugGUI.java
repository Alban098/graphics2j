/*
 * Copyright (c) 2022, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package simulation.debug;

import imgui.ImGui;
import rendering.debug.DebugLayer;
import rendering.debug.DebugUtils;
import rendering.debug.EntityDebugGUI;
import rendering.entities.Entity;
import simulation.entities.LightSource;

public class LightSourceDebugGUI extends EntityDebugGUI {

  @Override
  protected void renderTabs(DebugLayer caller, Entity entity) {
    if (entity instanceof LightSource lightSource) {
      if (ImGui.beginTabItem("Color")) {
        DebugUtils.drawAttrib("Color", lightSource.getColor(), 0, 50);
        ImGui.endTabItem();
      }
    }
  }

  @Override
  protected boolean showComponentTab() {
    return true;
  }

  @Override
  protected boolean showChildrenTab() {
    return true;
  }

  @Override
  protected boolean showTransformTab() {
    return true;
  }

  @Override
  protected boolean showTextureTab() {
    return false;
  }
}
