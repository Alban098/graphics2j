/*
 * Copyright (c) 2022-2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package org.alban098.engine2j.core.debug.tab;

import imgui.ImGui;
import java.util.ArrayList;
import java.util.Collection;
import org.alban098.engine2j.core.Scene;
import org.alban098.engine2j.core.debug.Debugger;
import org.alban098.engine2j.core.debug.renderable.RenderableDebugInterfaceProvider;
import org.alban098.engine2j.core.objects.Renderable;
import org.alban098.engine2j.core.objects.entities.Entity;
import org.alban098.engine2j.core.objects.interfaces.UserInterface;

/**
 * A concrete implementation of {@link DebugTab} responsible to display information about the {@link
 * Scene}
 */
public final class SceneTab extends DebugTab implements RenderableContainer {

  /** The type of the selected {@link Renderable} */
  private Class<? extends Renderable> selectedType;
  /** The selected {@link Renderable} */
  private Renderable selectedRenderable;

  /**
   * Creates a new Scene Tab
   *
   * @param parent the parent {@link Debugger}
   */
  public SceneTab(Debugger parent) {
    super("Scene", parent);
  }

  /** Draws the content of the Tab to the tabview */
  @Override
  public void draw() {
    Scene scene = parent.getEngine().getLogic().getScene();
    ImGui.setWindowSize(900, 462);
    Collection<Class<? extends Renderable>> types = new ArrayList<>(scene.getEntityTypes());
    types.addAll(scene.getInterfaceTypes());
    if (ImGui.beginListBox("##types", 170, Math.min(400, types.size() * 19f))) {
      for (Class<? extends Renderable> type : types) {
        if (ImGui.selectable(type.getSimpleName(), (type.equals(selectedType)))) {
          selectedType = type;
          if (selectedRenderable != null && !selectedRenderable.getClass().equals(type)) {
            selectedRenderable = null;
          }
        }
      }
      ImGui.endListBox();
    }
    ImGui.sameLine();
    if (selectedType != null) {
      Collection<? extends Renderable> renderables;
      if (Entity.class.isAssignableFrom(selectedType)) {
        renderables = scene.getEntitiesOfType((Class<? extends Entity>) selectedType);
      } else {
        renderables = scene.getInterfacesOfType((Class<? extends UserInterface>) selectedType);
      }
      ImGui.beginChild("##entitiesSummary", 120, Math.min(400, renderables.size() * 19f));
      if (ImGui.beginListBox("##renderables", 120, Math.min(400, renderables.size() * 19f))) {
        for (Renderable e : renderables) {
          if (ImGui.selectable(e.getName(), e.equals(selectedRenderable))) {
            selectedRenderable = e;
          }
        }
        ImGui.endListBox();
      }
      ImGui.endChild();
      ImGui.sameLine();
      if (selectedRenderable != null) {
        RenderableDebugInterfaceProvider.provide(selectedType).render(parent, selectedRenderable);
      }
    }
  }

  /**
   * Sets the currently selected {@link Renderable} held by the Tab
   *
   * @param renderable the new selected {@link Renderable}
   */
  @Override
  public void setSelectedRenderable(Renderable renderable) {
    this.selectedType = renderable.getClass();
    this.selectedRenderable = renderable;
  }
}
