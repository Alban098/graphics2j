/*
 * Copyright (c) 2022-2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package org.alban098.engine2j.fonts;

import java.util.HashMap;
import java.util.Map;
import org.alban098.engine2j.utils.ResourceLoader;

/** In charge of registering and managing all fonts of the Engine */
public final class FontManager {

  /** A Map of all {@link Font} currently registered, indexed by font family name */
  private static final Map<String, Font> FONTS = new HashMap<>();

  private FontManager() {}

  /**
   * Register a new {@link Font} to the Manager
   *
   * @param fontFamily the
   */
  public static void registerFont(String fontFamily, String path) {
    FONTS.put(fontFamily, ResourceLoader.loadFont(fontFamily, path + fontFamily.toLowerCase()));
  }

  /**
   * Retrieves a {@link Font} by its font family name
   *
   * @param fontFamily the font family name of the {@link Font} to retrieve
   * @return the retrieves {@link Font}, null if not found
   */
  public static Font getFont(String fontFamily) {
    return FONTS.get(fontFamily);
  }
}
