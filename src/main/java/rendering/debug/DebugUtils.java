/*
 * Copyright (c) 2022, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering.debug;

import imgui.ImGui;

public class DebugUtils {
  public static void drawAttrib(String name, Object value, int nameOffset, int valueOffset) {
    ImGui.newLine();
    ImGui.sameLine(nameOffset);
    ImGui.textColored(255, 0, 255, 255, name);
    ImGui.sameLine(valueOffset);
    ImGui.textColored(255, 255, 0, 255, value.toString());
  }

  public static String formatSize(int size) {
    if (size < 1_000) {
      return String.format("%d b", size);
    } else if (size < 1_000_000) {
      return String.format("%.3f Kb", size / 1_000f);
    }
    return String.format("%.3f Mb", size / 1_000_000f);
  }
}
