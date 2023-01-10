/*
 * Copyright (c) 2022-2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering.debug.tab;

import imgui.ImGui;
import java.util.Collection;
import java.util.List;
import rendering.debug.Debugger;
import rendering.debug.entity.EntityDebugInterfaceProvider;
import rendering.scene.Scene;
import rendering.scene.entities.Entity;

public final class SceneTab extends DebugTab implements EntityContainer {

  private Class<? extends Entity> sceneSelectedType;
  private Entity sceneSelectedEntity;

  public SceneTab(Debugger parent) {
    super("Scene", parent);
  }

  @Override
  public void draw() {
    Scene scene = parent.getEngine().getLogic().getScene();
    ImGui.setWindowSize(680, 462);
    Collection<Class<? extends Entity>> types = scene.getTypes();
    if (ImGui.beginListBox("##types", 170, Math.min(400, types.size() * 19f))) {
      for (Class<? extends Entity> type : types) {
        List<? extends Entity> objects = scene.getObjects(type);
        if (ImGui.selectable(
            type.getSimpleName() + " (" + objects.size() + ")", (type.equals(sceneSelectedType)))) {
          sceneSelectedType = type;
          if (sceneSelectedEntity != null && !sceneSelectedEntity.getClass().equals(type)) {
            sceneSelectedEntity = null;
          }
        }
      }
      ImGui.endListBox();
    }
    ImGui.sameLine();
    if (sceneSelectedType != null) {
      Collection<? extends Entity> entities = scene.getObjects(sceneSelectedType);
      ImGui.beginChild("##entitiesSummary", 120, Math.min(400, entities.size() * 19f));
      if (ImGui.beginListBox("##entities", 120, Math.min(400, entities.size() * 19f))) {
        for (Entity e : entities) {
          if (ImGui.selectable(e.getName(), e.equals(sceneSelectedEntity))) {
            sceneSelectedEntity = e;
          }
        }
        ImGui.endListBox();
      }
      ImGui.endChild();
      ImGui.sameLine();
      if (sceneSelectedEntity != null) {
        EntityDebugInterfaceProvider.provide(sceneSelectedType).render(parent, sceneSelectedEntity);
      }
    }
  }

  @Override
  public void setSelectedEntity(Entity entity) {
    this.sceneSelectedType = entity.getClass();
    this.sceneSelectedEntity = entity;
  }
}
