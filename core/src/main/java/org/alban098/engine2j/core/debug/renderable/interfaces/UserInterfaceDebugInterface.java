/*
 * Copyright (c) 2022-2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package org.alban098.engine2j.core.debug.renderable.interfaces;

import imgui.ImGui;
import org.alban098.engine2j.core.debug.DebugUtils;
import org.alban098.engine2j.core.debug.Debugger;
import org.alban098.engine2j.core.debug.renderable.RenderableDebugInterface;
import org.alban098.engine2j.core.objects.Renderable;
import org.alban098.engine2j.core.objects.entities.Entity;
import org.alban098.engine2j.core.objects.interfaces.UserInterface;
import org.alban098.engine2j.core.objects.interfaces.element.UIElement;
import org.alban098.engine2j.core.objects.interfaces.element.property.Properties;
import org.alban098.engine2j.core.objects.interfaces.element.property.RenderingProperties;

/** A concrete implementation of {@link RenderableDebugInterface} for {@link UserInterface} */
public class UserInterfaceDebugInterface<T extends UserInterface>
    extends RenderableDebugInterface<T> {

  /**
   * Returns the type of {@link Renderable} to display
   *
   * @return UserInterface.class
   */
  @Override
  public Class<T> getRenderableType() {
    return (Class<T>) UserInterface.class;
  }

  /**
   * Renders all additional tabs of the Interface (Hierarchy Tab)
   *
   * @param caller the main Debugger
   * @param ui the {@link UserInterface} to display
   */
  @Override
  protected void renderTabs(Debugger caller, UserInterface ui) {
    drawHierarchyTab(caller, ui);
    drawInfoTab(caller, ui);
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
   * Should the info tab be displayed
   *
   * @return true if not override
   */
  protected boolean showInfoTab() {
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
   * @param ui the {@link UserInterface} to display
   */
  protected void drawHierarchyTab(Debugger caller, UserInterface ui) {
    if (showHierarchyTab() && ImGui.beginTabItem("Hierarchy")) {
      if (ui.getNbElements() > 0) {
        for (UIElement element : ui.getElements()) {
          drawElement(element, 10);
        }
      }
      ImGui.endTabItem();
    }
  }

  /**
   * Renders the tab displaying hierarchy information about the {@link Entity}
   *
   * @param caller the main Debugger
   * @param ui the {@link UserInterface} to display
   */
  protected void drawInfoTab(Debugger caller, UserInterface ui) {
    if (showInfoTab() && ImGui.beginTabItem("Info")) {
      DebugUtils.drawAttrib("Visible", ui.isVisible(), 10, 180);
      DebugUtils.drawAttrib("Number of UIElements", ui.getNbElements(), 10, 180);
      ImGui.endTabItem();
    }
  }

  /**
   * Draws an {@link UIElement} with its {@link RenderingProperties} and children recursively
   *
   * @param element the {@link UIElement} to draw
   * @param offset the left offset in pixels to draw from
   */
  private void drawElement(UIElement element, int offset) {
    if (ImGui.treeNode(element.getClass().getSimpleName() + "##" + element.hashCode())) {
      DebugUtils.drawAttrib("Type", element.getClass().getSimpleName(), offset + 20, offset + 110);
      DebugUtils.drawAttrib("Name", element.getName(), offset + 20, offset + 110);
      DebugUtils.drawAttrib("Has Modal", element.getModal() != null, offset + 20, offset + 110);
      if (ImGui.treeNode("Properties##" + element.hashCode())) {
        drawProperties(element.getProperties(), offset);
        ImGui.treePop();
      }
      if (element.getNbElements() > 0 && ImGui.treeNode("Children##" + element.hashCode())) {
        for (UIElement child : element.getElements()) {
          drawElement(child, offset + 40);
        }
        ImGui.treePop();
      }
      ImGui.separator();
      ImGui.treePop();
    }
  }

  /**
   * Draws {@link RenderingProperties} at a certain offset in pixel
   *
   * @param properties the {@link RenderingProperties} to draw
   * @param offset the left offset in pixels to draw from
   */
  private void drawProperties(RenderingProperties properties, int offset) {
    for (Properties prop : Properties.values()) {
      DebugUtils.drawAttrib(
          prop.name(), properties.get(prop, Object.class), offset + 40, offset + 180);
    }
  }
}
