/*
 * Copyright (c) 2022-2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering.fonts;

import org.joml.Vector2f;

/** Represents a Character extracted from the Bitmap font files */
public final class CharacterDescriptor {

  /** ID of the character */
  private final int id;
  /** Position of the character on the texture scaled from 0 to 1 */
  private final Vector2f position;
  /** Size of the character on the texture, assuming a width of 1 */
  private final Vector2f size;
  /** Offset of the character on the subtexture, assuming a total width of 1 */
  private final Vector2f offset;
  /** How much to advance after this character, assuming a total width of 1 */
  private final float advance;

  /**
   * Creates a new CharacterDescriptor
   *
   * @param id id of the character
   * @param position position of the character on the texture scaled from 0 to 1
   * @param size size of the character on the texture, assuming a width of 1
   * @param offset offset of the character on the subtexture, assuming a total width of 1
   * @param advance how much to advance after this character
   */
  public CharacterDescriptor(
      int id, Vector2f position, Vector2f size, Vector2f offset, float advance) {
    this.id = id;
    this.position = position;
    this.size = size;
    this.offset = offset;
    this.advance = advance;
  }

  /**
   * Returns the id of the Character
   *
   * @return the id of the Character
   */
  public int getId() {
    return id;
  }

  /**
   * Returns the position of the character on the texture scaled from 0 to 1
   *
   * @return the position of the character on the texture
   */
  public Vector2f getPosition() {
    return position;
  }

  /**
   * Returns the size of the character on the texture, assuming a width of 1
   *
   * @return the size of the character on the texture
   */
  public Vector2f getSize() {
    return size;
  }

  /**
   * Returns the offset of the character on the subtexture, assuming a total width of 1
   *
   * @return the offset of the character on the subtexture
   */
  public Vector2f getOffset() {
    return offset;
  }

  /**
   * Returns how much to advance after this character, assuming a total width of 1
   *
   * @return how much to advance after this character
   */
  public float getAdvance() {
    return advance;
  }
}
