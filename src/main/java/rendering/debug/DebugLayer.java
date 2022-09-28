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
import java.util.List;
import rendering.Engine;
import rendering.entities.Entity;

public class DebugLayer extends ImGuiLayer {

  private final Double[][] frametimes = new Double[2][256];

  public DebugLayer(Engine engine) {
    super(engine);
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
      ImGui.setWindowSize(600, 228);
      ImGui.beginChild("timing", 150, 170);
      ImGui.textColored(255, 0, 255, 255, "Target FPS");
      ImGui.sameLine(90);
      ImGui.textColored(255, 255, 0, 255, String.valueOf(Engine.TARGET_FPS));

      ImGui.textColored(255, 0, 255, 255, "Target TPS");
      ImGui.sameLine(90);
      ImGui.textColored(255, 255, 0, 255, String.valueOf(Engine.TARGET_TPS));

      ImGui.newLine();
      ImGui.newLine();

      ImGui.textColored(255, 0, 255, 255, "Actual FPS");
      ImGui.sameLine(90);
      ImGui.textColored(255, 255, 0, 255, String.valueOf((int) (1.0 / engine.getFrameTime())));
      ImGui.textColored(255, 0, 255, 255, "Actual TPS");
      ImGui.sameLine(90);
      ImGui.textColored(255, 255, 0, 255, String.valueOf((int) (engine.getNbUpdates())));

      ImGui.newLine();
      ImGui.newLine();

      ImGui.textColored(255, 0, 255, 255, "Frame time");
      ImGui.sameLine(90);
      ImGui.textColored(
          255, 255, 0, 255, String.valueOf((int) (engine.getFrameTime() * 10000) / 10f));
      ImGui.sameLine();
      ImGui.textColored(255, 255, 0, 255, "ms");
      ImGui.textColored(255, 0, 255, 255, "Update time");
      ImGui.sameLine(90);
      ImGui.textColored(
          255, 255, 0, 255, String.valueOf((int) (10000 / engine.getNbUpdates()) / 10f));
      ImGui.sameLine();
      ImGui.textColored(255, 255, 0, 255, "ms");
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
          new ImVec2(426, 170),
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
      ImGui.textColored(255, 255, 0, 255, engine.getTotalObjects() + " objects");
      ImGui.beginGroup();
      ImGui.beginTabBar("scene.entities");
      for (Class<? extends Entity> type : engine.getLogic().getScene().getTypes()) {
        List<? extends Entity> entities = engine.getLogic().getScene().getObjects(type);
        if (ImGui.beginTabItem(type.getSimpleName() + " (" + entities.size() + ")")) {
          for (Entity o : entities) {
            ImGui.separator();
            if (ImGui.treeNode(Integer.toHexString(o.hashCode()))) {
              ImGui.text(o.getChildren().size() + " children");
              ImGui.image(o.getRenderable().getTexture().getId(), 128, 128);
              // TODO Display more attributes
              ImGui.treePop();
            }
          }
          ImGui.endTabItem();
        }
      }
      ImGui.endTabBar();
      ImGui.endGroup();
      ImGui.endTabItem();
    }
  }
}
