/*
 * Copyright (c) 2022, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering.fonts;

import org.joml.Vector2f;

public class AtlasCharacter {

  /** ID of the character */
  private final int id;
  /** Position of the character on the texture scale from 0 to 1 */
  private final Vector2f position;
  /** Size of the character on the texture, assuming a width of 1 */
  private final Vector2f size;
  /** Offset of the character on the subtexture, assuming a total width of 1 */
  private final Vector2f offset;
  /** Ho much to advance after this character */
  private final float advance;

  public AtlasCharacter(int id, Vector2f position, Vector2f size, Vector2f offset, float advance) {
    this.id = id;
    this.position = position;
    this.size = size;
    this.offset = offset;
    this.advance = advance;
  }

  public int getId() {
    return id;
  }

  public Vector2f getPosition() {
    return position;
  }

  public Vector2f getSize() {
    return size;
  }

  public Vector2f getOffset() {
    return offset;
  }

  public float getAdvance() {
    return advance;
  }
}
