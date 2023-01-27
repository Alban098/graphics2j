/*
 * Copyright (c) 2022-2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package org.alban098.engine2j.core.debug.renderable;

import org.alban098.engine2j.core.debug.component.ComponentDebugInterface;
import org.alban098.engine2j.core.debug.component.ComponentDebugInterfaceProvider;
import org.alban098.engine2j.core.objects.entities.Entity;
import org.alban098.engine2j.core.objects.entities.component.Component;
import org.alban098.engine2j.core.objects.interfaces.UserInterface;
import imgui.ImGui;
import org.alban098.engine2j.core.debug.DebugUtils;
import org.alban098.engine2j.core.debug.Debugger;
import org.alban098.engine2j.core.objects.Renderable;
import org.alban098.engine2j.core.renderers.RegisterableRenderer;

/**
 * Represents an abstraction of a Debug Interface that can display information about an {@link
 * Renderable}
 *
 * @param <T> the type of {@link Renderable} to display
 */
public abstract class RenderableDebugInterface<T extends Renderable> {

  /**
   * Returns the type of {@link Renderable} to display
   *
   * @return the type of {@link Renderable} to display
   */
  public abstract Class<T> getRenderableType();

  /**
   * Renders all additional tabs of the Interface
   *
   * @param caller the main Debugger
   * @param renderable the {@link Renderable} to display
   */
  protected abstract void renderTabs(Debugger caller, T renderable);

  /**
   * Should the component tab be displayed
   *
   * @return whether the component tab should be displayed
   */
  protected abstract boolean showComponentTab();

  /**
   * Should the component tab be displayed
   *
   * @return whether the component tab should be displayed
   */
  protected abstract boolean showRenderingTab();

  /**
   * Renders default tabs and call {@link RenderableDebugInterface#renderTabs(Debugger, Renderable)}
   *
   * @param caller the main Debugger
   * @param renderable the {@link Renderable} to display
   */
  public final void render(Debugger caller, Renderable renderable) {
    ImGui.beginChild("Element");
    ImGui.beginTabBar("entity_tab");
    drawComponentTab(caller, renderable);
    drawRenderingTab(caller, renderable);
    this.renderTabs(caller, (T) renderable);
    ImGui.endTabBar();
    ImGui.endChild();
  }

  /**
   * Renders the tab displaying rendering information about the {@link Renderable}
   *
   * @param caller the main Debugger
   * @param renderable the {@link Renderable} to display
   */
  private void drawRenderingTab(Debugger caller, Renderable renderable) {
    if (renderable.getRenderable() != null
        && showRenderingTab()
        && ImGui.beginTabItem("Rendering")) {
      RegisterableRenderer<?> renderer;
      if (renderable instanceof UserInterface) {
        renderer = caller.getEngine().getInterfaceRenderer();
      } else {
        renderer = caller.getEngine().getRenderer(((Entity) renderable).getClass());
      }
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

  /**
   * Renders the tab displaying component information about the {@link Renderable}
   *
   * @param caller the main Debugger
   * @param renderable the {@link Renderable} to display
   */
  private void drawComponentTab(Debugger caller, Renderable renderable) {
    if (showComponentTab() && ImGui.beginTabItem("Components")) {
      ImGui.separator();
      for (Component component : renderable.getComponents()) {
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
