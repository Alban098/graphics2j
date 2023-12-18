/*
 * Copyright (c) 2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package org.alban098.physics2j;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Arrays;
import org.joml.Vector2f;

/**
 * The {@code Polygon} class encapsulates a description of a closed, two-dimensional region within a
 * coordinate space. This region is bounded by an arbitrary number of line segments, each of which
 * is one side of the polygon. Internally, a polygon comprises of a list of {@code (x,y)} coordinate
 * pairs, where each pair defines a <i>vertex</i> of the polygon, and two successive pairs are the
 * endpoints of a line that is a side of the polygon. The first and final pairs of {@code (x,y)}
 * points are joined by a line segment that closes the polygon. This {@code Polygon} is defined with
 * an even-odd winding rule. See {@link java.awt.geom.PathIterator#WIND_EVEN_ODD WIND_EVEN_ODD} for
 * a definition of the even-odd winding rule. This class's hit-testing methods, which include the
 * {@code contains}, {@code intersects} and {@code inside} methods, use the <i>insideness</i>
 * definition described in the {@link Shape} class comments.
 *
 * @author Sami Shaio
 * @see Shape
 * @author Herb Jellinek
 * @since 1.0
 */
public class Polygon {

  public int npoints;
  public float[] xpoints;
  public float[] ypoints;
  protected Rectangle2D bounds;

  private static final int MIN_LENGTH = 4;

  public Polygon() {
    xpoints = new float[MIN_LENGTH];
    ypoints = new float[MIN_LENGTH];
  }

  public Polygon(float[] xpoints, float[] ypoints, int npoints) {
    // Fix 4489009: should throw IndexOutOfBoundsException instead
    // of OutOfMemoryError if npoints is huge and > {x,y}points.length
    if (npoints > xpoints.length || npoints > ypoints.length) {
      throw new IndexOutOfBoundsException(
          "npoints > xpoints.length || " + "npoints > ypoints.length");
    }
    // Fix 6191114: should throw NegativeArraySizeException with
    // negative npoints
    if (npoints < 0) {
      throw new NegativeArraySizeException("npoints < 0");
    }
    // Fix 6343431: Applet compatibility problems if arrays are not
    // exactly npoints in length
    this.npoints = npoints;
    this.xpoints = Arrays.copyOf(xpoints, npoints);
    this.ypoints = Arrays.copyOf(ypoints, npoints);
  }

  public void reset() {
    npoints = 0;
    bounds = null;
  }

  public void invalidate() {
    bounds = null;
  }

  public void translate(float deltaX, float deltaY) {
    for (int i = 0; i < npoints; i++) {
      xpoints[i] += deltaX;
      ypoints[i] += deltaY;
    }
    if (bounds != null) {
      bounds.add(deltaX, deltaY);
    }
  }

  void calculateBounds(float[] xpoints, float[] ypoints, int npoints) {
    float boundsMinX = Float.MAX_VALUE;
    float boundsMinY = Float.MAX_VALUE;
    float boundsMaxX = Float.MIN_VALUE;
    float boundsMaxY = Float.MIN_VALUE;

    for (int i = 0; i < npoints; i++) {
      float x = xpoints[i];
      boundsMinX = Math.min(boundsMinX, x);
      boundsMaxX = Math.max(boundsMaxX, x);
      float y = ypoints[i];
      boundsMinY = Math.min(boundsMinY, y);
      boundsMaxY = Math.max(boundsMaxY, y);
    }
    bounds =
        new Rectangle2D.Float(
            boundsMinX, boundsMinY, boundsMaxX - boundsMinX, boundsMaxY - boundsMinY);
  }

  void updateBounds(float x, float y) {
    if (x < bounds.getX()) {
      bounds.setRect(x, bounds.getY(), bounds.getWidth() + (bounds.getX() - x), bounds.getHeight());
    } else {
      bounds.setRect(
          bounds.getX(),
          bounds.getY(),
          Math.max(bounds.getWidth(), x - bounds.getX()),
          bounds.getHeight());
    }

    if (y < bounds.getY()) {
      bounds.setRect(bounds.getX(), y, bounds.getWidth(), bounds.getHeight() + (bounds.getY() - y));
    } else {
      bounds.setRect(
          bounds.getX(), y, bounds.getWidth(), Math.max(bounds.getHeight(), y - bounds.getY()));
    }
  }

  public void addPoint(float x, float y) {
    if (npoints >= xpoints.length || npoints >= ypoints.length) {
      int newLength = npoints * 2;
      // Make sure that newLength will be greater than MIN_LENGTH and
      // aligned to the power of 2
      if (newLength < MIN_LENGTH) {
        newLength = MIN_LENGTH;
      } else if ((newLength & (newLength - 1)) != 0) {
        newLength = Integer.highestOneBit(newLength);
      }

      xpoints = Arrays.copyOf(xpoints, newLength);
      ypoints = Arrays.copyOf(ypoints, newLength);
    }
    xpoints[npoints] = x;
    ypoints[npoints] = y;
    npoints++;
    if (bounds != null) {
      updateBounds(x, y);
    }
  }

  public Rectangle2D getBoundingBox() {
    if (npoints == 0) {
      return new Rectangle2D.Float();
    }
    if (bounds == null) {
      calculateBounds(xpoints, ypoints, npoints);
    }
    return bounds.getBounds();
  }

  @Deprecated
  public boolean inside(float x, float y) {
    return contains(x, y);
  }

  public boolean contains(float x, float y) {
    if (npoints <= 2 || !getBoundingBox().contains(x, y)) {
      return false;
    }
    int hits = 0;

    float lastx = xpoints[npoints - 1];
    float lasty = ypoints[npoints - 1];
    float curx, cury;

    // Walk the edges of the polygon
    for (int i = 0; i < npoints; lastx = curx, lasty = cury, i++) {
      curx = xpoints[i];
      cury = ypoints[i];

      if (cury == lasty) {
        continue;
      }

      float leftx;
      if (curx < lastx) {
        if (x >= lastx) {
          continue;
        }
        leftx = curx;
      } else {
        if (x >= curx) {
          continue;
        }
        leftx = lastx;
      }

      float test1, test2;
      if (cury < lasty) {
        if (y < cury || y >= lasty) {
          continue;
        }
        if (x < leftx) {
          hits++;
          continue;
        }
        test1 = x - curx;
        test2 = y - cury;
      } else {
        if (y < lasty || y >= cury) {
          continue;
        }
        if (x < leftx) {
          hits++;
          continue;
        }
        test1 = x - lastx;
        test2 = y - lasty;
      }

      if (test1 < (test2 / (lasty - cury) * (lastx - curx))) {
        hits++;
      }
    }

    return ((hits & 1) != 0);
  }

  public boolean contains(Point2D p) {
    return contains((float) p.getX(), (float) p.getY());
  }

  public PathIterator getPathIterator(AffineTransform at) {
    return new PolygonPathIterator(this, at);
  }

  public PathIterator getPathIterator(AffineTransform at, double flatness) {
    return getPathIterator(at);
  }

  public Vector2f getCenter() {
    Vector2f center = new Vector2f();
    for (int i = 0; i < npoints; i++) {
      center.add(xpoints[i], ypoints[i]);
    }
    center.div(npoints);
    return center;
  }

  public float getMomentOfInertia(float m) {
    if (bounds == null) {
      calculateBounds(xpoints, ypoints, npoints);
    }
    return (float)
        (m
            * (bounds.getWidth() * bounds.getWidth()
                + bounds.getHeight() * bounds.getHeight() / 12));
  }

  static class PolygonPathIterator implements PathIterator {
    Polygon poly;
    AffineTransform transform;
    int index;

    public PolygonPathIterator(Polygon pg, AffineTransform at) {
      poly = pg;
      transform = at;
      if (pg.npoints == 0) {
        // Prevent a spurious SEG_CLOSE segment
        index = 1;
      }
    }

    public int getWindingRule() {
      return WIND_EVEN_ODD;
    }

    public boolean isDone() {
      return index > poly.npoints;
    }

    public void next() {
      index++;
    }

    public int currentSegment(float[] coords) {
      if (index >= poly.npoints) {
        return SEG_CLOSE;
      }
      coords[0] = poly.xpoints[index];
      coords[1] = poly.ypoints[index];
      if (transform != null) {
        transform.transform(coords, 0, coords, 0, 1);
      }
      return (index == 0 ? SEG_MOVETO : SEG_LINETO);
    }

    public int currentSegment(double[] coords) {
      if (index >= poly.npoints) {
        return SEG_CLOSE;
      }
      coords[0] = poly.xpoints[index];
      coords[1] = poly.ypoints[index];
      if (transform != null) {
        transform.transform(coords, 0, coords, 0, 1);
      }
      return (index == 0 ? SEG_MOVETO : SEG_LINETO);
    }
  }
}
