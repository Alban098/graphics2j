/*
 * Copyright (c) 2022-2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering.interfaces.element;

public class Button extends UIElement implements Hoverable, Clickable {

  private static final String TEXT = "textLabel";

  public Button(String text) {
    super();
    super.addElement(TEXT, new TextLabel(text));
  }

  @Override
  public void addElement(String identifier, UIElement element) {}

  @Override
  protected void onPropertyChange(
      Properties.Snapshot oldProperties, Properties.Snapshot newProperties) {
    getElement(TEXT)
        .getProperties()
        .setFontFamily(newProperties.getFontFamily())
        .setFontSize(newProperties.getFontSize())
        .setFontColor(newProperties.getFontColor())
        .setFontBlur(newProperties.getFontBlur())
        .setFontWidth(newProperties.getFontWidth())
        .setSize(newProperties.getSize())
        .setPosition(
            newProperties.getFontSize() / 4f,
            (newProperties.getSize().y - getProperties().getFontSize()) / 1.1f);
  }

  @Override
  public void update(double elapsedTime) {}

  public String getText() {
    return ((TextLabel) getElement(TEXT)).getText();
  }
}
