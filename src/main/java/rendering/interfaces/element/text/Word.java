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
import rendering.fonts.AtlasCharacter;
import rendering.fonts.Font;
import rendering.shaders.ShaderAttributes;

public class Word implements Iterable<Character> {

  private final Vector2f position = new Vector2f();

  private final Collection<Character> characters = new ArrayList<>();

  public Word(String text, Font font, float fontSize) {
    position.set(0, 0);
    for (char c : text.toCharArray()) {
      AtlasCharacter ac = font.get(c);
      Character character = new Character();
      character
          .getRenderable()
          .setAttributeValue(ShaderAttributes.TEXT_TEXTURE_POS, ac.getPosition());
      character.getRenderable().setAttributeValue(ShaderAttributes.TEXT_TEXTURE_SIZE, ac.getSize());

      character.setPosition(position);
      character.setSize(new Vector2f(ac.getSize()).mul(fontSize * font.getFontFactor()));
      character.setOffset(new Vector2f(ac.getOffset()).mul(fontSize * font.getFontFactor()));

      position
          .add(ac.getAdvance() * fontSize * font.getFontFactor(), 0)
          .sub(font.getPadding()[0] * 2 * fontSize, 0);
      characters.add(character);
    }
    position.add(0, fontSize);
  }

  public void cleanup() {
    characters.forEach(Character::cleanup);
  }

  public Vector2f getSize() {
    return position;
  }

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
}
