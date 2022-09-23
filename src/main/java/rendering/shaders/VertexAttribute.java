/*
 * Copyright (c) 2022, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering.shaders;

import static org.lwjgl.opengl.GL11.GL_FLOAT;

import java.util.Objects;
import rendering.data.Quad;

public class VertexAttribute {

  public static final VertexAttribute POSITION =
      new VertexAttribute(0, "position", GL_FLOAT, Quad.VERTICES_DIM);
  public static final VertexAttribute TEXTURE_COORDINATES =
      new VertexAttribute(1, "textureCoords", GL_FLOAT, Quad.VERTICES_DIM);

  private final int location;
  private final String name;
  private final int type;
  private final int dimension;

  public VertexAttribute(int location, String name, int type, int dimension) {
    this.location = location;
    this.name = name;
    this.type = type;
    this.dimension = dimension;
  }

  public int getLocation() {
    return location;
  }

  public String getName() {
    return name;
  }

  public int getType() {
    return type;
  }

  public int getDimension() {
    return dimension;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    VertexAttribute that = (VertexAttribute) o;
    return location == that.location
        && type == that.type
        && dimension == that.dimension
        && Objects.equals(name, that.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(location, name, type, dimension);
  }
}
