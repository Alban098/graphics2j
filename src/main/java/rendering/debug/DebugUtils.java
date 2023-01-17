/*
 * Copyright (c) 2022-2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering.debug;

import imgui.ImGui;
import java.util.Locale;
import rendering.Texture;

/** Just a utility class regrouping methods for Debugging Layers */
public final class DebugUtils {

  /** Just a private constructor to disable instantiation */
  private DebugUtils() {}

  /**
   * Draws an attribute into an Debugging Layer
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
   * Draws a {@link Texture} and its info into a Debugging Layer
   *
   * @param texture the {@link Texture}
   */
  public static void drawTextureInfo(Texture texture) {
    ImGui.beginChild("textureInfo##" + texture.getId(), 160, 130);
    ImGui.textColored(255, 0, 0, 255, "Metadata");
    DebugUtils.drawAttrib("Id", texture.getId(), 10, 60);
    DebugUtils.drawAttrib("Size", DebugUtils.formatSize(texture.getSize()), 10, 60);
    DebugUtils.drawAttrib("Type", texture.getTypeDescriptor(), 10, 60);
    ImGui.separator();
    ImGui.textColored(255, 0, 0, 255, "Dimension");
    DebugUtils.drawAttrib("Width", texture.getWidth() + " px", 10, 70);
    DebugUtils.drawAttrib("Height", texture.getHeight() + " px", 10, 70);
    ImGui.endChild();
    ImGui.sameLine();
    ImGui.image(texture.getId(), texture.getAspectRatio() * 130, 130);
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
