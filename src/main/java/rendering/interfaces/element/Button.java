/*
 * Copyright (c) 2022-2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering.interfaces.element;

import org.joml.Vector2f;
import rendering.interfaces.element.property.Properties;
import rendering.interfaces.element.text.TextLabel;

public class Button extends UIElement implements Hoverable, Clickable {

  private static final String TEXT = "textLabel";

  public Button(String text) {
    super();
    super.addElement(TEXT, new TextLabel(text));
  }

  @Override
  public void addElement(String identifier, UIElement element) {}

  @Override
  protected void onPropertyChange(Properties property, Object value) {
    if (property == Properties.FONT_FAMILY
        || property == Properties.FONT_SIZE
        || property == Properties.FONT_COLOR
        || property == Properties.FONT_BLUR
        || property == Properties.FONT_WIDTH) {
      getElement(TEXT).getProperties().set(property, value);
    }
    if (property == Properties.SIZE) {
      Vector2f size = getProperties().get(Properties.SIZE, Vector2f.class);
      float fontSize = getProperties().get(Properties.FONT_SIZE, Float.class);
      getElement(TEXT)
          .getProperties()
          .set(Properties.SIZE, value)
          .set(Properties.POSITION, new Vector2f(fontSize / 4f, (size.y - fontSize) / 1.1f));
    }
    if (property == Properties.FONT_SIZE) {
      Vector2f size = getProperties().get(Properties.SIZE, Vector2f.class);
      float fontSize = getProperties().get(Properties.FONT_SIZE, Float.class);
      getElement(TEXT)
          .getProperties()
          .set(Properties.POSITION, new Vector2f(fontSize / 4f, (size.y - fontSize) / 1.1f));
    }
  }

  @Override
  public void update(double elapsedTime) {}

  public String getText() {
    return ((TextLabel) getElement(TEXT)).getText();
  }
}
