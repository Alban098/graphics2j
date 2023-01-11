/*
 * Copyright (c) 2022-2023, @Author Alban098
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
import rendering.fonts.Font;
import rendering.fonts.FontManager;
import rendering.interfaces.element.UIElement;
import rendering.interfaces.element.property.Properties;

/**
 * An implementation of {@link UIElement} representing a Text that can be drawn on screen Word can
 * be iterated over
 */
public final class TextLabel extends UIElement implements Iterable<Word>, Textable {

  /** A Collection of {@link Word} composing this TextLabel */
  private final Collection<Word> words = new ArrayList<>();

  /** The raw text of the TextLabel */
  private String text;

  /**
   * Creates a new TextLabel from a text
   *
   * @param text the text of the Label
   */
  public TextLabel(String text) {
    super();
    text = text.replace("\n", " \n ");
    setText(text);
  }

  /**
   * Returns the text of this TextLabel
   *
   * @return the text of this TextLabel
   */
  public String getText() {
    return text;
  }

  /**
   * Sets the text of this TextLabel and recompute its model
   *
   * @param text the new text to set
   */
  public void setText(String text) {
    this.text = text;
    precomputeModels();
  }

  /**
   * Computes the models used to render the text on the screen, will automatically wrap the text and
   * apply line breaks (\n), text wrapping is computed using the size property
   */
  public void precomputeModels() {
    if (getContainer() != null || getParent() != null) {
      // clears the current words
      words.forEach(Word::cleanup);
      words.clear();

      // determine the viewport
      Vector2f viewport;
      if (getParent() != null) {
        viewport = getParent().getProperties().get(Properties.SIZE, Vector2f.class);
      } else {
        viewport = getContainer().getProperties().get(Properties.SIZE, Vector2f.class);
      }

      // retrieving the font and properties
      Font font = FontManager.getFont(getProperties().get(Properties.FONT_FAMILY, String.class));
      Vector2f position = new Vector2f(getProperties().get(Properties.POSITION, Vector2f.class));
      float fontSize = getProperties().get(Properties.FONT_SIZE, Float.class);
      float spaceWidth = font.get(' ').getAdvance() * fontSize * font.getFontFactor();
      float lineWidth = 0;

      for (String pseudoWord : text.split(" ")) {
        // create a new Word
        Word word = new Word(pseudoWord, font, fontSize);
        float wordWidth = word.getSize().x;
        // if text wrapping or linebreak is encountered
        if (pseudoWord.equals("\n")
            || lineWidth + wordWidth > getProperties().get(Properties.SIZE, Vector2f.class).x) {
          position.set(
              getProperties().get(Properties.POSITION, Vector2f.class).x, position.y + fontSize);
          lineWidth = 0;
          continue;
        }
        // move the Word to the correct position
        word.setPosition(position, viewport);
        position.add(wordWidth + spaceWidth, 0);
        lineWidth += wordWidth + spaceWidth;
        // register the Word
        words.add(word);
      }
    }
  }

  /**
   * Update the text label, nothing to do in this implementation
   *
   * @param elapsedTime time elasped since last update in seconds
   */
  @Override
  public void update(double elapsedTime) {}

  /**
   * Called every time a {@link Properties} of the TextLabel is changed
   *
   * @param property the changed {@link Properties}
   * @param value the new value
   */
  @Override
  protected void onPropertyChange(Properties property, Object value) {
    if (property == Properties.SIZE
        || property == Properties.FONT_SIZE
        || property == Properties.FONT_FAMILY
        || property == Properties.POSITION) {
      precomputeModels();
    }
  }

  @Override
  public Iterator<Word> iterator() {
    return words.iterator();
  }

  @Override
  public void forEach(Consumer<? super Word> action) {
    words.forEach(action);
  }

  @Override
  public Spliterator<Word> spliterator() {
    return words.spliterator();
  }
}
