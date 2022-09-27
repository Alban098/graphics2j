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
import rendering.Engine;
import rendering.Tracker;
import rendering.renderers.Renderer;

public class ExampleLayer extends ImGuiLayer {

  private final Double[][] frametimes = new Double[2][256];

  public ExampleLayer(Tracker tracker) {
    super(tracker);
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
      ImGui.setWindowSize(517, 376);

      ImGui.textColored(255, 0, 255, 255, "Target FPS :");
      ImGui.sameLine();
      ImGui.textColored(255, 255, 0, 255, String.valueOf(Engine.TARGET_FPS));
      ImGui.sameLine(150);
      ImGui.textColored(255, 0, 255, 255, "Actual FPS :");
      ImGui.sameLine();
      ImGui.textColored(
          255, 255, 0, 255, String.valueOf((int) (1.0 / tracker.getEngine().getFrameTime())));
      ImGui.sameLine(300);
      ImGui.textColored(255, 0, 255, 255, "Frametime :");
      ImGui.sameLine();
      ImGui.textColored(
          255,
          255,
          0,
          255,
          String.valueOf((int) (tracker.getEngine().getFrameTime() * 10000) / 10f));
      ImGui.sameLine();
      ImGui.textColored(255, 255, 0, 255, "ms");

      if (frametimes[1].length - 1 >= 0) {
        System.arraycopy(frametimes[1], 1, frametimes[1], 0, frametimes[1].length - 1);
      }
      frametimes[1][255] = tracker.getEngine().getFrameTime() * 1000;

      ImPlot.setNextPlotLimits(0, 256, 0, 1.0 / Engine.TARGET_FPS * 8000, 1);
      if (ImPlot.beginPlot(
          "Frametime plot",
          "Frames",
          "Time in ms",
          new ImVec2(500, 300),
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
      ImGui.textColored(255, 255, 0, 255, tracker.getScene().getTotalObjects() + " objects");
      for (Renderer<?> renderer : tracker.getScene().getRenderers()) {
        if (ImGui.treeNode(
            renderer.getClass().getSimpleName()
                + " @"
                + Integer.toHexString(renderer.hashCode()))) {
          ImGui.textColored(255, 255, 0, 255, renderer.getNbObjects() + " objects");
          ImGui.textColored(255, 255, 0, 255, renderer.getDrawCalls() + " draw calls/frame");
          ImGui.treePop();
        }
      }
      ImGui.endTabItem();
    }
  }
}
