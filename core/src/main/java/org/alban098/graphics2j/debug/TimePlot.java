/*
 * Copyright (c) 2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package org.alban098.graphics2j.debug;

import imgui.ImVec2;
import imgui.extension.implot.ImPlot;
import imgui.extension.implot.flag.ImPlotAxisFlags;
import imgui.extension.implot.flag.ImPlotFlags;
import imgui.flag.ImGuiCond;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import org.alban098.graphics2j.debug.structures.ScrollingBuffer;

/**
 * A Small simplicity of life class for rendering time plots
 *
 * @param <T> the Type of Objects for plot legend
 */
public class TimePlot<T> {

  /** A Map of all samples indexed by legends element */
  private final Map<T, ScrollingBuffer<Double>> times = new HashMap<>();
  /** A Supplier in charge of supplying a Map of Sample for one time step */
  private Supplier<Map<T, Double>> sampleSupplier;
  /** The maximum number of samples on the plot before scrolling */
  private final int sampleSize;
  /** The name of the Plot */
  private final String name;
  /** The dimension of the plot in pixels */
  private final ImVec2 plotSize;
  /** The number of samples already plotted, used for scrolling */
  private long sampleCount = 0;

  /**
   * Creates a new {@link TimePlot}
   *
   * @param name the name of the Plot
   * @param sampleCapacity the max number of samples on the Plot
   * @param plotSize the size of the Plot in pixels
   * @param sampleSupplier the supplier in charge of supplying a Map of Sample for one time step
   */
  public TimePlot(
      String name, int sampleCapacity, ImVec2 plotSize, Supplier<Map<T, Double>> sampleSupplier) {
    this.sampleSize = sampleCapacity;
    this.name = name;
    this.plotSize = plotSize;
    this.sampleSupplier = sampleSupplier;
    populateTimeKeys();
  }

  /** Populates the time map keys */
  private void populateTimeKeys() {
    for (T t : sampleSupplier.get().keySet()) {
      times.put(t, new ScrollingBuffer<>(sampleSize));
    }
  }

  /**
   * Sets the sample Supplier methods, resets the plot and recompute the time Map keys
   *
   * @param sampleSupplier the new samples Supplier
   */
  public void setSampleSupplier(Supplier<Map<T, Double>> sampleSupplier) {
    this.sampleSupplier = sampleSupplier;
    clear();
    populateTimeKeys();
  }

  /** Clears the Plot */
  public void clear() {
    times.clear();
    sampleCount = 0;
  }

  /** Updates the plot by appending the next sample */
  public void update() {
    for (Map.Entry<T, Double> entry : sampleSupplier.get().entrySet()) {
      times.get(entry.getKey()).push(sampleCount, entry.getValue() * 1_000);
    }
    sampleCount++;
  }

  /**
   * Renders the Plot to the screen
   *
   * @param yMin the min Y value of the Plot Y axis viewport
   * @param yMax the max Y value of the Plot Y axis viewport
   * @param flags additional flag to be passed to {@link ImPlot#beginPlot(String)}
   */
  public void render(double yMin, double yMax, int flags) {
    ImPlot.setNextPlotLimitsX(sampleCount - sampleSize, sampleCount, ImGuiCond.Always);
    ImPlot.setNextPlotLimitsY(yMin, yMax, 0);
    if (ImPlot.beginPlot(
        name,
        "",
        "Time in ms",
        plotSize,
        ImPlotFlags.NoMousePos | flags,
        ImPlotAxisFlags.NoGridLines | ImPlotAxisFlags.NoDecorations,
        ImPlotAxisFlags.LockMin)) {
      for (T t : times.keySet()) {
        ScrollingBuffer<Double> buffer = times.get(t);
        ImPlot.plotLine(t.toString(), buffer.getIndices(), buffer.getValues(), buffer.getOffset());
      }
      ImPlot.endPlot();
    }
  }
}
