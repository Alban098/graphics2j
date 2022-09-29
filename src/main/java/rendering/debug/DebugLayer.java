/*
 * Copyright (c) 2022, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering.debug;

import imgui.ImGui;
import imgui.ImVec2;
import imgui.extension.implot.ImPlot;
import imgui.extension.implot.flag.ImPlotFlags;
import java.util.Collection;
import java.util.List;
import rendering.Engine;
import rendering.entities.Entity;

public class DebugLayer extends ImGuiLayer {

  private final Double[][] frametimes = new Double[2][256];
  private Class<? extends Entity> sceneSelectedType;
  private Entity sceneSelectedEntity;

  public DebugLayer(Debugger debugger) {
    super(debugger);
    for (int i = 0; i < frametimes[0].length; i++) {
      frametimes[0][i] = (double) i;
      frametimes[1][i] = 0d;
    }
  }

  @Override
  public void render() {
    ImGui.begin("Debug");
    if (ImGui.beginTabBar("tab")) {
      drawTimingTab();
      drawSceneTab();
      drawSelectedEntityTab();
      ImGui.endTabBar();
    }
    ImGui.end();
  }

  private void drawSelectedEntityTab() {
    if (ImGui.beginTabItem("Selected Entity")) {
      ImGui.textColored(255, 255, 0, 255, "Nothing here yet");
      ImGui.endTabItem();
    }
  }

  private void drawTimingTab() {
    if (ImGui.beginTabItem("Timing")) {
      ImGui.setWindowSize(640, 228);
      ImGui.beginChild("timing", 150, 170);
      ImGui.separator();
      ImGui.textColored(255, 0, 0, 255, "Target");
      DebugUtils.drawAttrib("FPS", Engine.TARGET_FPS, 10, 90);
      DebugUtils.drawAttrib("Ticks/s", Engine.TARGET_TPS, 10, 90);

      ImGui.separator();
      ImGui.textColored(255, 0, 0, 255, "Actual");
      DebugUtils.drawAttrib("FPS", (int) (1.0 / engine.getFrameTime()), 10, 90);
      DebugUtils.drawAttrib("Ticks/s", (int) (engine.getNbUpdates()), 10, 90);

      ImGui.separator();
      ImGui.textColored(255, 0, 0, 255, "Times");
      DebugUtils.drawAttrib("Frame", (int) (engine.getFrameTime() * 10000) / 10f + " ms", 10, 90);
      DebugUtils.drawAttrib("Tick", (int) (10000 / engine.getNbUpdates()) / 10f + " ms", 10, 90);

      ImGui.endChild();
      ImGui.sameLine();

      if (frametimes[1].length - 1 >= 0) {
        System.arraycopy(frametimes[1], 1, frametimes[1], 0, frametimes[1].length - 1);
      }
      frametimes[1][255] = engine.getFrameTime() * 1000;

      ImPlot.setNextPlotLimits(0, 256, 0, 1.0 / Engine.TARGET_FPS * 8000, 1);
      if (ImPlot.beginPlot(
          "Frametime plot",
          "Frames",
          "Time in ms",
          new ImVec2(466, 170),
          ImPlotFlags.CanvasOnly,
          0,
          0)) {
        ImPlot.plotLine("time", frametimes[0], frametimes[1]);
        ImPlot.endPlot();
      }
      ImGui.endTabItem();
    }
  }

  public void drawSceneTab() {
    if (ImGui.beginTabItem("Scene")) {
      ImGui.setWindowSize(680, 462);
      Collection<Class<? extends Entity>> types = scene.getTypes();
      if (ImGui.beginListBox("##types", 170, Math.min(400, types.size() * 19f))) {
        for (Class<? extends Entity> type : types) {
          List<? extends Entity> entities = scene.getObjects(type);
          if (ImGui.selectable(
              type.getSimpleName() + " (" + entities.size() + ")",
              (type.equals(sceneSelectedType)))) {
            sceneSelectedType = type;
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
          debugger.getDebugGUI(sceneSelectedType).render(this, sceneSelectedEntity);
        }
      }
      ImGui.endTabItem();
    }
  }

  public void setSelectedEntity(Entity entity) {
    this.sceneSelectedType = entity.getClass();
    this.sceneSelectedEntity = entity;
  }
}
