/*
 * Copyright (c) 2022, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering.debug.entity;

import imgui.ImGui;
import rendering.debug.DebugUtils;
import rendering.debug.Debugger;
import rendering.debug.component.ComponentDebugInterface;
import rendering.debug.component.ComponentDebugInterfaceProvider;
import rendering.entities.Entity;
import rendering.entities.component.Component;
import rendering.entities.component.RenderableComponent;
import rendering.renderers.Renderer;

public abstract class EntityDebugInterface<T extends Entity> {

  public abstract Class<T> getEntityClass();

  protected abstract void renderTabs(Debugger caller, Entity entity);

  protected abstract boolean showComponentTab();

  protected abstract boolean showChildrenTab();

  protected abstract boolean showRenderingTab();

  public final void render(Debugger caller, Entity entity) {
    ImGui.beginChild("entity");
    ImGui.beginTabBar("entity_tab");
    drawHierarchyTab(caller, entity);
    drawComponentTab(caller, entity);
    drawRenderingTab(caller, entity);
    this.renderTabs(caller, entity);
    ImGui.endTabBar();
    ImGui.endChild();
  }

  private void drawRenderingTab(Debugger caller, Entity entity) {
    if (entity.hasComponent(RenderableComponent.class)
        && showRenderingTab()
        && ImGui.beginTabItem("Rendering")) {
      Renderer<?> renderer = caller.getEngine().getRenderer(entity.getClass());
      ImGui.beginChild("renderer", 300, 130);
      ImGui.separator();
      if (renderer != null) {
        ImGui.textColored(255, 0, 0, 255, "Renderer");
        DebugUtils.drawAttrib("Type of Renderer", renderer.getClass().getSimpleName(), 10, 160);
        DebugUtils.drawAttrib("Registered Objects", renderer.getNbObjects(), 10, 160);
        DebugUtils.drawAttrib("Registered Textures", renderer.getTextures().size(), 10, 160);
        DebugUtils.drawAttrib("Draw Calls / frame", renderer.getDrawCalls(), 10, 160);
        ImGui.text("More info on \"Renderers\" tab ...");
      } else {
        ImGui.text("No \"Renderers\" attached ...");
      }
      ImGui.separator();
      ImGui.endChild();
      ImGui.endTabItem();
    }
  }

  private void drawComponentTab(Debugger caller, Entity entity) {
    if (showComponentTab() && ImGui.beginTabItem("Components")) {
      ImGui.separator();
      for (Component component : entity.getComponents()) {
        ComponentDebugInterface<?> gui =
            ComponentDebugInterfaceProvider.provide(component.getClass());
        if (ImGui.treeNode(gui.getDisplayName() + "##" + component.hashCode())) {
          gui.draw(component);
          ImGui.treePop();
          ImGui.separator();
        }
      }
      ImGui.endTabItem();
    }
  }

  private void drawHierarchyTab(Debugger caller, Entity entity) {
    if (showChildrenTab() && ImGui.beginTabItem("Hierarchy")) {
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
