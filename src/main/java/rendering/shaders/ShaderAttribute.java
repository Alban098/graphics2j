/*
 * Copyright (c) 2022-2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering.shaders;

import java.util.Objects;

public class ShaderAttribute {

  private final int location;
  private final int dimension;
  private final String name;
  private final Class<? extends Number> type;

  public ShaderAttribute(int location, String name, int dimension, Class<? extends Number> type) {
    this.location = location;
    this.dimension = dimension;
    this.name = name;
    this.type = type;
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

  public Class<?> getDataType() {
    return type;
  }
}
