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
  private final int dimension;

  public ShaderAttribute(int location, String name, int dimension) {
    this.location = location;
    this.name = name;
    this.dimension = dimension;
  }

  public int getLocation() {
    return location;
  }

  public String getName() {
    return name;
  }

  public int getDimension() {
    return dimension;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ShaderAttribute that = (ShaderAttribute) o;
    return location == that.location
        && dimension == that.dimension
        && Objects.equals(name, that.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(location, name, dimension);
  }
}
