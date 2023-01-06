/*
 * Copyright (c) 2022-2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package simulation.debug;

import imgui.ImGui;
import rendering.debug.DebugUtils;
import rendering.debug.Debugger;
import rendering.debug.entity.RenderableDebugInterface;
import simulation.entities.LightSource;

public class LightSourceDebugInterface extends RenderableDebugInterface<LightSource> {

  @Override
  public Class<LightSource> getEntityClass() {
    return LightSource.class;
  }

  @Override
  protected void renderTabs(Debugger caller, LightSource lightSource) {
    if (ImGui.beginTabItem("Color")) {
      DebugUtils.drawAttrib("Color", lightSource.getColor(), 0, 50);
      ImGui.endTabItem();
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
  protected boolean showRenderingTab() {
    return true;
  }
}
