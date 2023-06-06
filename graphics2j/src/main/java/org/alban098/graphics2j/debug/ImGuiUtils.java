/*
 * Copyright (c) 2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package org.alban098.graphics2j.debug;

import imgui.ImGui;
import java.util.Locale;

/** Just a utility class regrouping methods for Debugging Layers */
public final class ImGuiUtils {

  /** Just a private constructor to disable instantiation */
  private ImGuiUtils() {}

  /**
   * Draws an attribute into a Debugging Layer
   *
   * @param name the name to draw (drawn in magenta)
   * @param value the value to draw (drawn in yellow)
   * @param nameOffset the absolute left offset to display the name
   * @param valueOffset the absolute left offset to display the value
   */
  public static void drawAttrib(String name, Object value, int nameOffset, int valueOffset) {
    ImGui.newLine();
    ImGui.sameLine(nameOffset);
    ImGui.textColored(255, 0, 255, 255, name);
    ImGui.sameLine(valueOffset);
    ImGui.textColored(255, 255, 0, 255, value == null ? "null" : value.toString());
  }

  /**
   * Draws an attribute into a Debugging Layer on 2 lines
   *
   * @param name the name to draw (drawn in magenta)
   * @param value the value to draw (drawn in yellow)
   * @param nameOffset the absolute left offset to display the name
   * @param valueOffset the absolute left offset to display the value
   */
  public static void drawAttrib2(String name, Object value, int nameOffset, int valueOffset) {
    ImGui.newLine();
    ImGui.sameLine(nameOffset);
    ImGui.textColored(255, 0, 255, 255, name);
    ImGui.newLine();
    ImGui.sameLine(valueOffset);
    ImGui.textColored(255, 255, 0, 255, value == null ? "null" : value.toString());
  }

  /**
   * Draws an attribute into a Debugging Layer on 2 lines
   *
   * @param name the name to draw (drawn in magenta)
   * @param value the value to draw (drawn in yellow)
   * @param nameOffset the absolute left offset to display the name
   * @param valueOffset the absolute left offset to display the value
   * @param lineWidth the maximum width of a line in characters
   */
  public static void drawAttrib2(
      String name, Object value, int nameOffset, int valueOffset, int lineWidth) {
    ImGui.newLine();
    ImGui.sameLine(nameOffset);
    ImGui.textColored(255, 0, 255, 255, name);
    String val = value == null ? "null" : value.toString();
    String[] splitted = new String[val.length() / lineWidth + 1];
    for (int i = 0; i < splitted.length; i++) {
      splitted[i] = val.substring(i * lineWidth, Math.min(val.length(), (i + 1) * lineWidth));
    }
    for (String str : splitted) {
      ImGui.newLine();
      ImGui.sameLine(valueOffset);
      ImGui.textColored(255, 255, 0, 255, str);
    }
  }

  /**
   * Formats a size in bytes into a readable String featuring the right prefix
   *
   * <ul>
   *   <li><b>X bytes</b> if less than 1.000
   *   <li><b>X KB</b> if between 1.000 and 1.000.000
   *   <li><b>X MB</b> if greater than 1.000.000
   * </ul>
   *
   * @param size the size to format (in bytes)
   * @return a formatted String representing the size
   */
  public static String formatSize(int size) {
    if (size < 1_000) {
      return String.format(Locale.ENGLISH, "%d bytes", size);
    } else if (size < 1_000_000) {
      return String.format(Locale.ENGLISH, "%.3f KB", size / 1_000f);
    }
    return String.format(Locale.ENGLISH, "%.3f MB", size / 1_000_000f);
  }
}
