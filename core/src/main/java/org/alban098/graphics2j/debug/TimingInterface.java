/*
 * Copyright (c) 2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package org.alban098.graphics2j.debug;

import imgui.ImGui;
import imgui.ImVec2;
import imgui.extension.implot.ImPlot;
import imgui.extension.implot.flag.ImPlotFlags;
import org.alban098.graphics2j.common.Window;

public class TimingInterface extends DebugInterface {

  private static final int LABEL_RENEW_TIME = 100;

  /** Holds the frametime graph coordinates points */
  private final Double[][] frameTimes = new Double[2][256];

  private final Window window;

  private double frametime = 0;
  private double lastFrame = 0;
  private int fps = 0;
  private long refreshLabelAt = 0;

  public TimingInterface(Window window) {
    super("Timing");
    this.window = window;
    for (int i = 0; i < frameTimes[0].length; i++) {
      frameTimes[0][i] = (double) i;
      frameTimes[1][i] = 0d;
    }
  }

  @Override
  public void render() {
    if (System.currentTimeMillis() >= refreshLabelAt) {
      fps = window.getTimeSinceLastFrame() == 0 ? 0 : (int) (1.0 / window.getTimeSinceLastFrame());
      frametime = (int) (window.getFrametime() * 10000) / 10.0;
      lastFrame = (int) (window.getTimeSinceLastFrame() * 10000) / 10.0;
      refreshLabelAt = System.currentTimeMillis() + LABEL_RENEW_TIME;
    }
    ImGui.setWindowSize(640, 228);
    ImGui.beginChild("timing", 150, 170);

    ImGui.separator();
    ImGui.textColored(255, 0, 0, 255, "Performance");
    DebugUtils.drawAttrib("FPS", fps, 10, 90);

    ImGui.separator();
    ImGui.textColored(255, 0, 0, 255, "Times");
    DebugUtils.drawAttrib("Rendering ", frametime + " ms", 10, 90);
    DebugUtils.drawAttrib("Frame ", lastFrame + " ms", 10, 90);

    ImGui.endChild();
    ImGui.sameLine();

    if (frameTimes[1].length - 1 >= 0) {
      System.arraycopy(frameTimes[1], 1, frameTimes[1], 0, frameTimes[1].length - 1);
    }
    frameTimes[1][255] = (int) (window.getFrametime() * 10000) / 10.0;

    ImPlot.setNextPlotLimits(0, 256, 0, 1.0 / 120 * 8000, 1);
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
