/*
 * Copyright (c) 2022-2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package org.alban098.engine2j.fonts;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.alban098.engine2j.common.shaders.data.Texture;

/** Represents a Font loaded from Bitmap font files */
public final class Font {

  /** The name of the font */
  private final String name;
  /** A map of all the Characters drawable in that font, indexed by their ASCII code */
  private final Map<Integer, CharacterDescriptor> characterMap = new HashMap<>();
  /** The OpenGL texture of the atlas image file */
  private final Texture atlas;
  /**
   * The paddings as indicated in the .fnt file, represent the padding around each character in the
   * atlas in pixels
   */
  private final Float[] padding;

  /**
   * A calculated factor to convert between the absolute size of the Font and a desired size in
   * pixels
   */
  private final float fontFactor;

  /**
   * Creates a new Font
   *
   * @param name the name of the font
   * @param characters a map of all the Characters drawable in that font, indexed by their ASCII
   *     code
   * @param atlas the OpenGL texture of the atlas image file
   * @param padding the paddings as indicated in the .fnt file, represent the padding around each
   *     character in the atlas in pixels
   * @param fontFactor a calculated factor to convert between the absolute size of the Font and a
   *     desired size in pixels
   */
  public Font(
      String name,
      Collection<CharacterDescriptor> characters,
      Texture atlas,
      Float[] padding,
      float fontFactor) {
    this.name = name;
    this.atlas = atlas;
    this.padding = padding;
    this.fontFactor = fontFactor;
    characters.forEach(c -> characterMap.put(c.getId(), c));
  }

  /**
   * Retrieves a {@link CharacterDescriptor} from its ASCII code
   *
   * @param id the ASCII code of the Character
   * @return the matching {@link CharacterDescriptor}, null il not found
   */
  public CharacterDescriptor get(int id) {
    return characterMap.get(id);
  }

  /**
   * Returns the name of the font
   *
   * @return the name of the font
   */
  public String getName() {
    return name;
  }

  /**
   * Returns the OpenGL texture of the atlas image file
   *
   * @return the OpenGL texture of the atlas image file
   */
  public Texture getAtlas() {
    return atlas;
  }

  /**
   * Returns the paddings as indicated in the .fnt file, represent the padding around each character
   * in the atlas in pixels
   *
   * @return the paddings as indicated in the .fnt file
   */
  public Float[] getPadding() {
    return padding;
  }

  /**
   * Returns the calculated factor to convert between the absolute size of the Font and a desired
   * size in pixels
   *
   * @return the calculated factor to convert between the absolute size of the Font and a desired
   *     size
   */
  public float getFontFactor() {
    return fontFactor;
  }
}
