/*
 * Copyright (c) 2022-2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering.debug.renderable.entity;

import imgui.ImGui;
import rendering.debug.DebugUtils;
import rendering.debug.Debugger;
import rendering.debug.renderable.RenderableDebugInterface;
import rendering.renderers.Renderable;
import rendering.scene.entities.Entity;

/** A concrete implementation of {@link RenderableDebugInterface} for {@link Entity} */
public class EntityDebugInterface<T extends Entity> extends RenderableDebugInterface<T> {

  /**
   * Returns the type of {@link Renderable} to display
   *
   * @return Entity.class
   */
  @Override
  public Class<T> getRenderableType() {
    return (Class<T>) Entity.class;
  }

  /**
   * Renders all additional tabs of the Interface (Hierarchy Tab)
   *
   * @param caller the main Debugger
   * @param entity the {@link Entity} to display
   */
  @Override
  protected void renderTabs(Debugger caller, Entity entity) {
    drawHierarchyTab(caller, entity);
  }

  /**
   * Should the component tab be displayed
   *
   * @return true if not override
   */
  @Override
  protected boolean showComponentTab() {
    return true;
  }

  /**
   * Should the hierarchy tab be displayed
   *
   * @return true if not override
   */
  protected boolean showHierarchyTab() {
    return true;
  }

  /**
   * Should the rendering tab be displayed
   *
   * @return true
   */
  @Override
  protected boolean showRenderingTab() {
    return true;
  }

  /**
   * Renders the tab displaying hierarchy information about the {@link Entity}
   *
   * @param caller the main Debugger
   * @param entity the {@link Entity} to display
   */
  protected void drawHierarchyTab(Debugger caller, Entity entity) {
    if (showHierarchyTab() && ImGui.beginTabItem("Hierarchy")) {
      if (entity.getParent() != null) {
        ImGui.separator();
        ImGui.textColored(255, 0, 0, 255, "Parent");
        ImGui.newLine();
        ImGui.sameLine(20);
        ImGui.beginChild("entity_parent", 250, 63);
        ImGui.separator();
        ImGui.textColored(0, 0, 255, 255, entity.getParent().getName());
        DebugUtils.drawAttrib("Type", entity.getParent().getClass().getSimpleName(), 20, 90);
        DebugUtils.drawAttrib("Children", entity.getParent().getChildren().size(), 20, 90);
        ImGui.endChild();
        if (ImGui.isItemClicked()) {
          caller.setSelectedEntity(entity.getParent());
        }
      }
      ImGui.separator();
      if (!entity.getChildren().isEmpty()) {
        ImGui.textColored(
            255, 0, 0, 255, String.format("Children (%d)", entity.getChildren().size()));
        for (Entity child : entity.getChildren()) {
          ImGui.newLine();
          ImGui.sameLine(20);
          ImGui.beginChild("entity_children_" + child.hashCode(), 280, 55);
          ImGui.separator();
          ImGui.textColored(0, 0, 255, 255, child.getName());
          DebugUtils.drawAttrib("Type", child.getClass().getSimpleName(), 20, 90);
          DebugUtils.drawAttrib("Children", child.getChildren().size(), 20, 90);
          ImGui.endChild();
          if (ImGui.isItemClicked()) {
            caller.setSelectedEntity(child);
          }
        }
      }
      ImGui.endTabItem();
    }
  }
}
