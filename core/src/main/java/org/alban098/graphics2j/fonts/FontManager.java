/*
 * Copyright (c) 2022-2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package org.alban098.graphics2j.fonts;

import java.util.HashMap;
import java.util.Map;

import org.alban098.graphics2j.common.Window;
import org.alban098.graphics2j.common.utils.ResourceLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** In charge of registering and managing all fonts of the Engine */
public final class FontManager {

  /** Just a Logger to log events */
  private static final Logger LOGGER = LoggerFactory.getLogger(Window.class);

  /** A Map of all {@link Font} currently registered, indexed by font family name */
  private static final Map<String, Font> FONTS = new HashMap<>();

  /** Just a default private constructor to prevent instanciation */
  private FontManager() {}

  /**
   * Register a new {@link Font} to the Manager
   *
   * @param fontFamily the font family name
   * @param path the path of where font files are stored, will append 'fontFamily' and the extension
   *     at the end
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
    if (!FONTS.containsKey(fontFamily)) {
      LOGGER.error("Font family '{}' not found, have you registered it using FontManager.registerFont() prior to this call ?", fontFamily);
      throw new IllegalArgumentException("Font " + fontFamily + " not found !");
    }
    return FONTS.get(fontFamily);
  }
}
