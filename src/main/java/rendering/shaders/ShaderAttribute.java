/*
 * Copyright (c) 2022, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering.shaders;

import java.util.Objects;

public class ShaderAttribute {

  private final int location;
  private final int dimension;
  private final String name;

  public ShaderAttribute(int location, String name, int dimension) {
    this.location = location;
    this.dimension = dimension;
    this.name = name;
  }

  public int getLocation() {
    return location;
  }

  public int getDimension() {
    return dimension;
  }

  public String getName() {
    return name;
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
}
