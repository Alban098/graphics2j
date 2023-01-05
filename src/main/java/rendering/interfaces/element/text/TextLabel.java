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

public class TextLabel extends UIElement implements Iterable<Word> {

  private final Collection<Word> words = new ArrayList<>();

  private String text;

  public TextLabel(String text) {
    super();
    text = text.replace("\n", " \n ");
    setText(text);
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
    precomputeModels();
  }

  public void precomputeModels() {
    if (getContainer() != null || getParent() != null) {
      words.forEach(Word::cleanup);
      words.clear();

      Vector2f viewport;
      if (getParent() != null) {
        viewport = getParent().getProperties().get(Properties.SIZE, Vector2f.class);
      } else {
        viewport = getContainer().getProperties().get(Properties.SIZE, Vector2f.class);
      }

      Font font = FontManager.getFont(getProperties().get(Properties.FONT_FAMILY, String.class));
      Vector2f position = new Vector2f(getProperties().get(Properties.POSITION, Vector2f.class));
      float fontSize = getProperties().get(Properties.FONT_SIZE, Float.class);
      float spaceWidth = font.get(' ').getAdvance() * fontSize * font.getFontFactor();
      float lineWidth = 0;

      for (String pseudoWord : text.split(" ")) {
        Word word = new Word(pseudoWord, font, fontSize);
        float wordWidth = word.getSize().x;
        if (pseudoWord.equals("\n")
            || lineWidth + wordWidth > getProperties().get(Properties.SIZE, Vector2f.class).x) {
          position.set(
              getProperties().get(Properties.POSITION, Vector2f.class).x, position.y + fontSize);
          lineWidth = 0;
          continue;
        }
        word.setPosition(position, viewport);
        position.add(wordWidth + spaceWidth, 0);
        lineWidth += wordWidth + spaceWidth;
        words.add(word);
      }
    }
  }

  @Override
  public void update(double elapsedTime) {}

  public Collection<Word> getWords() {
    return words;
  }

  @Override
  protected void onPropertyChange(Properties property, Object value) {}

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
