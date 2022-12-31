/*
 * Copyright (c) 2022, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering.interfaces.element;

import rendering.MouseInput;
import rendering.interfaces.UIElement;

public class Button extends UIElement implements Hoverable, Clickable {

  private static final String TEXT = "textLabel";
  private Runnable callback;
  private boolean hovered = false;
  private boolean clicked = false;

  public Button(String text) {
    super();
    super.addElement(TEXT, new TextLabel(text));
  }

  @Override
  public void addElement(String identifier, UIElement element) {}

  public void onClick(Runnable callback) {
    this.callback = callback;
  }

  @Override
  public boolean isHovered() {
    return hovered;
  }

  @Override
  public void setHovered(boolean hovered) {
    this.hovered = hovered;
  }

  @Override
  public boolean isClicked() {
    return clicked;
  }

  @Override
  public void setClicked(boolean clicked) {
    this.clicked = clicked;
  }

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
        .setPosition(
            newProperties.getFontSize() / 3f,
            (newProperties.getSize().y - getProperties().getFontSize()) / 2f);
  }

  @Override
  public void update(double elapsedTime) {}

  @Override
  public boolean input(MouseInput input) {
    boolean inside = isInside(input.getCurrentPos());

    setHovered(inside && input.canTakeControl(this));

    if (isClicked()) {
      // If the element is clicked and the left mouse button is released, this means the click has
      // ended, therefor executing the callback
      if (!input.isLeftButtonPressed()) {
        callback.run();
        input.release();
        setClicked(false);
        // Prevent other UIElement further down the stack to interpret the input
        return true;
      }
    } else {
      if (input.canTakeControl(this)) {
        if (inside) {
          // If the element isn't clicked, but the mouse input is free and we are inside, juste take
          // control of the input to prevent camera panning/movement
          input.halt(this);
          if (input.isLeftButtonPressed()) {
            // If we are clicked here, juste
            setClicked(true);
            // Prevent other UIElement further down the stack to interpret the input
            return true;
          }
        } else {
          // Otherwise juste release the input to allow camera panning/movement
          input.release();
        }
      }
    }
    return false;
  }

  public String getText() {
    return ((TextLabel) getElement(TEXT)).getText();
  }
}
