/*
 * Copyright (c) 2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package org.alban098.graphics2j.interfaces.components;

import org.alban098.graphics2j.common.shaders.ShaderAttributes;
import org.alban098.graphics2j.interfaces.components.property.Properties;
import org.joml.Math;
import org.joml.Vector2f;

/**
 * An implementation of {@link UIElement} representing a Line between 2 points, Hoverable but not
 * clickable
 */
public final class Line extends UIElement implements Hoverable {

  /** The starting point of the line, coordinates in pixels */
  private final Vector2f start = new Vector2f();
  /** The ending point of the line, coordinates in pixels */
  private final Vector2f end = new Vector2f();

  /**
   * Creates a new Line between 2 specified points
   *
   * @param start the starting point of the line, coordinates in pixels
   * @param end the ending point of the line, coordinates in pixels
   */
  public Line(Vector2f start, Vector2f end) {
    setStart(start);
    setEnd(end);
  }

  /**
   * Sets the starting point of the Line
   *
   * @param start the new starting point of the line
   */
  public void setStart(Vector2f start) {
    this.start.set(start);
    getRenderable().setAttributeValue(ShaderAttributes.LINE_START, this.start);
  }

  /**
   * Sets the ending point of the Line
   *
   * @param end the new ending point of the line
   */
  public void setEnd(Vector2f end) {
    this.end.set(end);
    getRenderable().setAttributeValue(ShaderAttributes.LINE_END, this.end);
  }

  /**
   * Called every time a {@link Properties} of the Line is changed, noting to do in this
   * implementation
   *
   * @param property the changed {@link Properties}
   * @param value the new value
   */
  protected void onPropertyChange(Properties property, Object value) {}

  /**
   * Updates the Line, this method is called once every update, noting to do in this implementation
   *
   * @param elapsedTime the elapsed time since last update in seconds
   */
  @Override
  public void update(double elapsedTime) {}

  /**
   * Return whether a point is inside of this UIElement or not, being on the edge is considered
   * being inside
   *
   * <p>/!\ Being on the edge must be considered being inside
   *
   * @param pos the position of the point to check
   * @return true if the point is inside the UIElement, false otherwise
   */
  @Override
  protected boolean isInside(Vector2f pos) {
    // retrieve the 2 points in the Window's reference frame
    Vector2f offset = getPositionInWindow();
    Vector2f absoluteStart = new Vector2f().add(offset).add(start);
    Vector2f absoluteEnd = new Vector2f().add(offset).add(end);

    // computes the equation of the line as : ax + by + c = 0
    Vector2f dir = new Vector2f().add(absoluteEnd).sub(absoluteStart);
    double a = dir.y;
    double b = -dir.x;
    double c = -(a * absoluteStart.x + b * absoluteStart.y);

    // computes the distance to the line, not considering the start and end points
    double dist = Math.abs(a * pos.x + b * pos.y + c) / (Math.sqrt(a * a + b * b));

    // correcting from the line to a segment
    Vector2f startToPoint = new Vector2f().add(pos).sub(absoluteStart);
    Vector2f endToPoint = new Vector2f().add(pos).sub(absoluteEnd);
    if (dir.dot(startToPoint) < 0 || dir.dot(endToPoint) > 0) {
      dist = Math.sqrt(Math.min(startToPoint.lengthSquared(), endToPoint.lengthSquared()));
    }

    return dist < getProperties().get(Properties.LINE_WIDTH, Float.class);
  }
}
