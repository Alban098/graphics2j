/*
 * Copyright (c) 2022-2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering.debug.tab;

import imgui.ImGui;
import imgui.ImVec2;
import imgui.extension.implot.ImPlot;
import imgui.extension.implot.flag.ImPlotFlags;
import rendering.Engine;
import rendering.debug.DebugUtils;
import rendering.debug.Debugger;

public final class TimingTab extends DebugTab {

  private final Double[][] frameTimes = new Double[2][256];

  public TimingTab(Debugger parent) {
    super("Timing", parent);
    for (int i = 0; i < frameTimes[0].length; i++) {
      frameTimes[0][i] = (double) i;
      frameTimes[1][i] = 0d;
    }
  }

  @Override
  public void draw() {
    Engine engine = parent.getEngine();
    ImGui.setWindowSize(640, 228);
    ImGui.beginChild("timing", 150, 170);
    ImGui.separator();
    ImGui.textColored(255, 0, 0, 255, "Target");
    DebugUtils.drawAttrib("FPS", engine.getOptions().getTargetFps(), 10, 90);
    DebugUtils.drawAttrib("Ticks/s", engine.getOptions().getTargetTps(), 10, 90);

    ImGui.separator();
    ImGui.textColored(255, 0, 0, 255, "Actual");
    DebugUtils.drawAttrib("FPS", (int) (1.0 / engine.getFrameTime()), 10, 90);
    DebugUtils.drawAttrib("Ticks/s", (int) (engine.getTPS()), 10, 90);

    ImGui.separator();
    ImGui.textColored(255, 0, 0, 255, "Times");
    DebugUtils.drawAttrib("Frame", (int) (engine.getFrameTime() * 10000) / 10f + " ms", 10, 90);
    DebugUtils.drawAttrib("Tick", (int) (10000 / engine.getTPS()) / 10f + " ms", 10, 90);

    ImGui.endChild();
    ImGui.sameLine();

    if (frameTimes[1].length - 1 >= 0) {
      System.arraycopy(frameTimes[1], 1, frameTimes[1], 0, frameTimes[1].length - 1);
    }
    frameTimes[1][255] = engine.getFrameTime() * 1000;

    ImPlot.setNextPlotLimits(0, 256, 0, 1.0 / engine.getOptions().getTargetFps() * 8000, 1);
    if (ImPlot.beginPlot(
        "Frametime plot",
        "Frames",
        "Time in ms",
        new ImVec2(466, 170),
        ImPlotFlags.CanvasOnly,
        0,
        0)) {
      ImPlot.plotLine("time", frameTimes[0], frameTimes[1]);
      ImPlot.endPlot();
    }
  }
}
