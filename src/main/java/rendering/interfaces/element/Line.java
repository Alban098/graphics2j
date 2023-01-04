/*
 * Copyright (c) 2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering.interfaces.element;

import org.joml.Math;
import org.joml.Vector2f;
import rendering.shaders.ShaderAttributes;

public class Line extends UIElement implements Hoverable {

  private final Vector2f start = new Vector2f();
  private final Vector2f end = new Vector2f();

  public Line(Vector2f start, Vector2f end) {
    setStart(start);
    setEnd(end);
    onEnter(
        input -> {
          if (getModal() != null) {
            getModal().toggleVisibility(true);
            getModal().getProperties().setPosition(input.getCurrentPos());
          }
        });
    onExit(
        input -> {
          if (getModal() != null) {
            getModal().toggleVisibility(false);
          }
        });
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
    Vector2f offset = getPositionInWindow();
    Vector2f absoluteStart = new Vector2f().add(offset).add(start);
    Vector2f absoluteEnd = new Vector2f().add(offset).add(end);
    Vector2f dir = new Vector2f().add(absoluteEnd).sub(absoluteStart);
    double a = dir.y;
    double b = -dir.x;
    double c = -(a * absoluteStart.x + b * absoluteStart.y);
    double dist = Math.abs(a * pos.x + b * pos.y + c) / (Math.sqrt(a * a + b * b));

    Vector2f startToPoint = new Vector2f().add(pos).sub(absoluteStart);
    Vector2f endToPoint = new Vector2f().add(pos).sub(absoluteEnd);
    if (dir.dot(startToPoint) < 0 || dir.dot(endToPoint) > 0) {
      dist = Math.sqrt(Math.min(startToPoint.lengthSquared(), endToPoint.lengthSquared()));
    }

    return dist < getProperties().getLineWidth();
  }
}
