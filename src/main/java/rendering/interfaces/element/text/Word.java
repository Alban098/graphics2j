/*
 * Copyright (c) 2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering.interfaces.element.text;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.function.Consumer;
import org.joml.Vector2f;
import rendering.fonts.CharacterDescriptor;
import rendering.fonts.Font;
import rendering.shaders.ShaderAttributes;

/** Represent a Word inside a {@link TextLabel} allow text wrapping and line breaks */
public class Word implements Iterable<Character> {

  /** The position of the Word in its container */
  private final Vector2f position = new Vector2f();
  /** The text of the Word */
  private final String text;
  /** A Collection of all the {@link Character} in this Word */
  private final Collection<Character> characters = new ArrayList<>();

  /**
   * Creates a new Word from a text, in a certain {@link Font} of a specified font size
   *
   * @param text the text of the Word
   * @param font the {@link Font} to render the Word in
   * @param fontSize the font size in pixels
   */
  public Word(String text, Font font, float fontSize) {
    this.text = text;
    position.set(0, 0);
    for (char c : text.toCharArray()) {
      // retrieve the character descriptor from the Font
      CharacterDescriptor ac = font.get(c);
      // create a Character with it
      Character character = new Character(ac);
      // specify the needed info for rendering (position and size on the font atlas)
      character
          .getRenderable()
          .setAttributeValue(ShaderAttributes.TEXT_TEXTURE_POS, ac.getPosition());
      character.getRenderable().setAttributeValue(ShaderAttributes.TEXT_TEXTURE_SIZE, ac.getSize());

      // place and scale the Character in its container
      character.setPosition(position);
      character.setSize(new Vector2f(ac.getSize()).mul(fontSize * font.getFontFactor()));
      character.setOffset(new Vector2f(ac.getOffset()).mul(fontSize * font.getFontFactor()));

      // advance to the next Character
      position
          .add(ac.getAdvance() * fontSize * font.getFontFactor(), 0)
          .sub(font.getPadding()[0] * 2 * fontSize, 0);
      characters.add(character);
    }
    // add font size because the bounding box is determined by this
    position.add(0, fontSize);
  }

  /** Clears the Word by clearing all its {@link Character} */
  public void cleanup() {
    characters.forEach(Character::cleanup);
  }

  /**
   * Gets the current size of the Word in pixels
   *
   * @return the current size of the Word in pixels
   */
  public Vector2f getSize() {
    return position;
  }

  /**
   * Sets the position of the Word in its container, in pixels
   *
   * @param position the new position of the Word in its container
   * @param viewport the viewport, representing the size of the container in pixels
   */
  public void setPosition(Vector2f position, Vector2f viewport) {
    characters.forEach(
        c -> {
          c.addPosition(position);
          c.updateTransform(viewport);
        });
  }

  @Override
  public Iterator<Character> iterator() {
    return characters.iterator();
  }

  @Override
  public void forEach(Consumer<? super Character> action) {
    characters.forEach(action);
  }

  @Override
  public Spliterator<Character> spliterator() {
    return characters.spliterator();
  }

  @Override
  public String toString() {
    return text;
  }
}
