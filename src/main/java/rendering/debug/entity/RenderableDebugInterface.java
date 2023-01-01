/*
 * Copyright (c) 2022-2023, @Author Alban098
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
import rendering.renderers.RegisterableRenderer;

public abstract class RenderableDebugInterface<T extends Entity> {

  public abstract Class<T> getEntityClass();

  protected abstract void renderTabs(Debugger caller, T entity);

  protected abstract boolean showComponentTab();

  protected abstract boolean showChildrenTab();

  protected abstract boolean showRenderingTab();

  public final void render(Debugger caller, Entity entity) {
    ImGui.beginChild("Element");
    ImGui.beginTabBar("entity_tab");
    drawComponentTab(caller, entity);
    drawRenderingTab(caller, entity);
    this.renderTabs(caller, (T) entity);
    ImGui.endTabBar();
    ImGui.endChild();
  }

  private void drawRenderingTab(Debugger caller, Entity entity) {
    if (entity.hasComponent(RenderableComponent.class)
        && showRenderingTab()
        && ImGui.beginTabItem("Rendering")) {
      RegisterableRenderer<?> renderer = caller.getEngine().getRenderer(entity.getClass());
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
}
