/*
 * Copyright (c) 2022, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering.fonts;

import java.util.HashMap;
import java.util.Map;
import rendering.ResourceLoader;

public class FontManager {

  private static final Map<String, Font> FONTS = new HashMap<>();

  private FontManager() {}

  public static void registerFont(String fontFamily) {
    FONTS.put(
        fontFamily,
        ResourceLoader.loadFont(
            fontFamily, "src/main/resources/fonts/" + fontFamily.toLowerCase()));
  }

  public static Font getFont(String fontFamily) {
    return FONTS.get(fontFamily);
  }
}
