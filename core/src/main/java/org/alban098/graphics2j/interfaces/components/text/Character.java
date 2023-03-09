/*
 * Copyright (c) 2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package org.alban098.graphics2j.interfaces.components.text;

import org.alban098.graphics2j.common.Renderable;
import org.alban098.graphics2j.common.components.RenderElement;
import org.alban098.graphics2j.common.components.Transform;
import org.alban098.graphics2j.fonts.CharacterDescriptor;
import org.joml.Vector2f;

/**
 * Represents a Character of a {@link Word} this is the base {@link Renderable} piece of the font
 * rendering pipeline
 */
public final class Character implements Renderable {

  /** The {@link RenderElement} used to render the Character */
  private final RenderElement renderableComponent;
  /** The {@link Transform} used to place the Character */
  private final Transform transform;
  /** The position of the Character on its container, in pixels */
  private final Vector2f position = new Vector2f();
  /** The size of the Character in pixels */
  private final Vector2f size = new Vector2f();
  /** The offset of the Character in the texture atlas from the base position */
  private final Vector2f offset = new Vector2f();
  /** A reference to the source Character as extracted from the font files */
  private final CharacterDescriptor character;

  /**
   * Create a new Character from an {@link CharacterDescriptor}
   *
   * @param c the {@link CharacterDescriptor} this Character is derived from
   */
  public Character(CharacterDescriptor c) {
    this.character = c;
    renderableComponent = new RenderElement();
    transform = new Transform();
  }

  /**
   * Gets the {@link RenderElement} used to render the Character
   *
   * @return the {@link RenderElement} used to render the Character
   */
  @Override
  public RenderElement getRenderable() {
    return renderableComponent;
  }

  /**
   * Gets the {@link Transform} used to place the Character
   *
   * @return the {@link Transform} used to place the Character
   */
  @Override
  public Transform getTransform() {
    return transform;
  }

  /**
   * Returns a display name for the Renderable
   *
   * @return a display name for the Renderable
   */
  @Override
  public String getName() {
    return String.valueOf(character.getId());
  }

  /**
   * Update the {@link Transform} of Character to be rendered correctly on the screen
   *
   * @param viewport the size of the Viewport in pixels
   */
  public void updateTransform(Vector2f viewport) {
    position.add(offset);
    float width = 2f * size.x / viewport.x;
    float height = 2f * size.y / viewport.y;
    transform.setScale(width, height);
    transform.setDisplacement(
        2f * position.x / viewport.x - 1 + width / 2f,
        2f * -position.y / viewport.y + 1 - height / 2f);
    transform.commit();
    position.sub(offset);
  }

  /** Clears the Character by cleaning its components */
  @Override
  public void cleanUp() {
    renderableComponent.cleanUp();
    transform.cleanUp();
  }

  /**
   * Sets the position of the Character inside its container, in pixels
   *
   * @param position the new position of the Character inside its container
   */
  public void setPosition(Vector2f position) {
    this.position.set(position);
  }

  /**
   * Sets the size of the Character in pixels
   *
   * @param size the new size of the Character
   */
  public void setSize(Vector2f size) {
    this.size.set(size);
  }

  /**
   * Moves the Character from its current position, in pixels
   *
   * @param delta the amount to move the Character
   */
  public void addPosition(Vector2f delta) {
    this.position.add(delta);
  }

  /**
   * Sets the offset of the Character in pixels
   *
   * @param offset the new offset of the Character
   */
  public void setOffset(Vector2f offset) {
    this.offset.set(offset);
  }

  @Override
  public String toString() {
    return character.toString();
  }
}
