/*
 * Copyright (c) 2022, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering.fonts;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import rendering.Texture;

public class Font {

  private final String name;
  private final Map<Integer, AtlasCharacter> characterMap = new HashMap<>();
  private final Texture atlas;
  private final Float[] padding;

  private final float fontFactor;

  public Font(
      String name,
      Collection<AtlasCharacter> characters,
      Texture atlas,
      Float[] padding,
      float fontFactor) {
    this.name = name;
    this.atlas = atlas;
    this.padding = padding;
    this.fontFactor = fontFactor;
    characters.forEach(c -> characterMap.put(c.getId(), c));
  }

  public AtlasCharacter get(int id) {
    return characterMap.get(id);
  }

  public String getName() {
    return name;
  }

  public Texture getAtlas() {
    return atlas;
  }

  public Float[] getPadding() {
    return padding;
  }

  public float getFontFactor() {
    return fontFactor;
  }
}
