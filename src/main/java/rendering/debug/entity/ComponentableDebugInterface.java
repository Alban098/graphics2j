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
import rendering.entities.component.Component;
import rendering.entities.component.RenderableComponent;
import rendering.renderers.AbstractRenderer;
import rendering.renderers.Componentable;

public abstract class ComponentableDebugInterface<T extends Componentable> {

  public abstract Class<T> getEntityClass();

  protected abstract void renderTabs(Debugger caller, Componentable entity);

  protected abstract boolean showComponentTab();

  protected abstract boolean showChildrenTab();

  protected abstract boolean showRenderingTab();

  public final void render(Debugger caller, Componentable componentable) {
    ImGui.beginChild("Element");
    ImGui.beginTabBar("entity_tab");
    drawComponentTab(caller, componentable);
    drawRenderingTab(caller, componentable);
    this.renderTabs(caller, componentable);
    ImGui.endTabBar();
    ImGui.endChild();
  }

  private void drawRenderingTab(Debugger caller, Componentable componentable) {
    if (componentable.hasComponent(RenderableComponent.class)
        && showRenderingTab()
        && ImGui.beginTabItem("Rendering")) {
      AbstractRenderer<?> renderer = caller.getEngine().getRenderer(componentable.getClass());
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

  private void drawComponentTab(Debugger caller, Componentable componentable) {
    if (showComponentTab() && ImGui.beginTabItem("Components")) {
      ImGui.separator();
      for (Component component : componentable.getComponents()) {
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
