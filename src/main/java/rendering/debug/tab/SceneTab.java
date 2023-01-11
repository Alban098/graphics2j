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
import rendering.debug.renderable.RenderableDebugInterfaceProvider;
import rendering.scene.Scene;
import rendering.scene.entities.Entity;

/**
 * A concrete implementation of {@link DebugTab} responsible to display information about the {@link
 * Scene}
 */
public final class SceneTab extends DebugTab implements EntityContainer {

  /** The type of the selected {@link Entity} */
  private Class<? extends Entity> sceneSelectedEntityType;
  /** The selected {@link Entity} */
  private Entity sceneSelectedEntity;

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
    ImGui.setWindowSize(680, 462);
    Collection<Class<? extends Entity>> types = scene.getTypes();
    if (ImGui.beginListBox("##types", 170, Math.min(400, types.size() * 19f))) {
      for (Class<? extends Entity> type : types) {
        List<? extends Entity> objects = scene.getObjects(type);
        if (ImGui.selectable(
            type.getSimpleName() + " (" + objects.size() + ")",
            (type.equals(sceneSelectedEntityType)))) {
          sceneSelectedEntityType = type;
          if (sceneSelectedEntity != null && !sceneSelectedEntity.getClass().equals(type)) {
            sceneSelectedEntity = null;
          }
        }
      }
      ImGui.endListBox();
    }
    ImGui.sameLine();
    if (sceneSelectedEntityType != null) {
      Collection<? extends Entity> entities = scene.getObjects(sceneSelectedEntityType);
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
        RenderableDebugInterfaceProvider.provide(sceneSelectedEntityType)
            .render(parent, sceneSelectedEntity);
      }
    }
  }

  /**
   * Sets the currently selected {@link Entity} held by the Tab
   *
   * @param entity the new selected {@link Entity}
   */
  @Override
  public void setSelectedEntity(Entity entity) {
    this.sceneSelectedEntityType = entity.getClass();
    this.sceneSelectedEntity = entity;
  }
}
