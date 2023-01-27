/*
 * Copyright (c) 2022-2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package org.alban098.engine2j.example.debug;

import org.alban098.engine2j.example.entities.ExampleColoredEntity;
import imgui.ImGui;
import org.alban098.engine2j.core.debug.DebugUtils;
import org.alban098.engine2j.core.debug.Debugger;
import org.alban098.engine2j.core.debug.renderable.entity.EntityDebugInterface;

public class ExampleColoredEntityDebugInterface extends EntityDebugInterface<ExampleColoredEntity> {

  @Override
  public Class<ExampleColoredEntity> getRenderableType() {
    return ExampleColoredEntity.class;
  }

  @Override
  protected void renderTabs(Debugger caller, ExampleColoredEntity lightSource) {
    drawHierarchyTab(caller, lightSource);
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
  protected boolean showRenderingTab() {
    return true;
  }
}
