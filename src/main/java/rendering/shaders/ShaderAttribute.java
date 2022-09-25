/*
 * Copyright (c) 2022, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering.shaders;

import java.util.Objects;

public class ShaderAttribute {

  private final int location;
  private final String name;
  private final int type;
  private final int dimension;
  private final boolean perVertex;

  public ShaderAttribute(int location, String name, int type, int dimension, boolean perVertex) {
    this.location = location;
    this.name = name;
    this.type = type;
    this.dimension = dimension;
    this.perVertex = perVertex;
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

  public boolean isPerVertex() {
    return perVertex;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ShaderAttribute that = (ShaderAttribute) o;
    return location == that.location
        && type == that.type
        && dimension == that.dimension
        && perVertex == that.perVertex
        && Objects.equals(name, that.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(location, name, type, perVertex, dimension);
  }
}
