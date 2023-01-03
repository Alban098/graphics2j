/*
 * Copyright (c) 2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering.interfaces.element;

import org.joml.Vector2f;
import rendering.shaders.ShaderAttributes;

public class Line extends UIElement implements Hoverable {

  private final Vector2f start = new Vector2f();
  private final Vector2f end = new Vector2f();

  public Line(Vector2f start, Vector2f end) {
    setStart(start);
    setEnd(end);
    onEnter(input -> getModal().toggleVisibility(true));
    onExit(input -> getModal().toggleVisibility(false));
    onInside(
        (input -> {
          if (getModal() != null) {
            getModal().getProperties().setPosition(input.getCurrentPos());
          }
        }));
  }

  public void setStart(Vector2f start) {
    this.start.set(start);
    getRenderable().setAttributeValue(ShaderAttributes.LINE_START, this.start);
  }

  public void setEnd(Vector2f end) {
    this.end.set(end);
    getRenderable().setAttributeValue(ShaderAttributes.LINE_END, this.end);
  }

  @Override
  protected void onPropertyChange(
      Properties.Snapshot oldProperties, Properties.Snapshot newProperties) {}

  @Override
  public void update(double elapsedTime) {}

  @Override
  protected boolean isInside(Vector2f pos) {
    // Just for testing, need to refactor to be the actual inside methods of the line
    return (pos.x > start.x && pos.x < end.x) && (pos.y > start.y && pos.y < end.y);
  }
}
