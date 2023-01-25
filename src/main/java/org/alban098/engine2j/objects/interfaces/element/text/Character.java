/*
 * Copyright (c) 2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package org.alban098.engine2j.objects.interfaces.element.text;

import java.util.Collection;
import java.util.List;
import org.alban098.engine2j.fonts.CharacterDescriptor;
import org.alban098.engine2j.objects.Renderable;
import org.alban098.engine2j.objects.entities.component.Component;
import org.alban098.engine2j.objects.entities.component.RenderableComponent;
import org.alban098.engine2j.objects.entities.component.TransformComponent;
import org.joml.Vector2f;

/**
 * Represents a Character of a {@link Word} this is the base {@link Renderable} piece of the font
 * rendering pipeline
 */
public final class Character implements Renderable {

  /** The {@link RenderableComponent} used to render the Character */
  private final RenderableComponent renderableComponent;
  /** The {@link TransformComponent} used to place the Character */
  private final TransformComponent transformComponent;
  /** The position of the Character on its container, in pixels */
  private final Vector2f position = new Vector2f();
  /** The size of the Character in pixels */
  private final Vector2f size = new Vector2f();
  /** The offset of the Character in the texture atlas from the base position */
  private final Vector2f offset = new Vector2f();

  private final CharacterDescriptor character;

  /**
   * Create a new Character from an {@link CharacterDescriptor}
   *
   * @param c the {@link CharacterDescriptor} this Character is derived from
   */
  public Character(CharacterDescriptor c) {
    this.character = c;
    renderableComponent = new RenderableComponent();
    transformComponent = new TransformComponent();
  }

  /**
   * Gets the {@link RenderableComponent} used to render the Character
   *
   * @return the {@link RenderableComponent} used to render the Character
   */
  @Override
  public RenderableComponent getRenderable() {
    return renderableComponent;
  }

  /**
   * Gets the {@link TransformComponent} used to place the Character
   *
   * @return the {@link TransformComponent} used to place the Character
   */
  @Override
  public TransformComponent getTransform() {
    return transformComponent;
  }

  /**
   * Returns a Collection of {@link Component}, must at least return a {@link RenderableComponent}
   * and a {@link TransformComponent}
   */
  @Override
  public Collection<Component> getComponents() {
    return List.of(renderableComponent, transformComponent);
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
   * Update the {@link TransformComponent} of Character to be rendered correctly on the screen
   *
   * @param viewport the size of the Viewport in pixels
   */
  public void updateTransform(Vector2f viewport) {
    position.add(offset);
    float width = 2f * size.x / viewport.x;
    float height = 2f * size.y / viewport.y;
    transformComponent.setScale(width, height);
    transformComponent.setDisplacement(
        2f * position.x / viewport.x - 1 + width / 2f,
        2f * -position.y / viewport.y + 1 - height / 2f);
    transformComponent.update(null, 0);
    position.sub(offset);
  }

  /** Clears the Character by cleaning its components */
  public void cleanUp() {
    renderableComponent.cleanUp();
    transformComponent.cleanUp();
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
