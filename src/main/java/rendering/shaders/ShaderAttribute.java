/*
 * Copyright (c) 2022-2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering.shaders;

import java.util.Objects;

/**
 * Represents a Shader Attribute, it will be bound to a {@link
 * rendering.shaders.data.vbo.VertexBufferObject} and appear in a Vertex Shader as
 *
 * <p>layout (location = X) in *type* *name*;
 */
public final class ShaderAttribute {

  /** The binding location of the attribute as specified in the Vertex Shader (layout=X) */
  private final int location;
  /** The dimension of the stored attribute (2 for vec2, 3 for vec3 ...) */
  private final int dimension;
  /** The name of the attribute as it appears in the Vertex Shader */
  private final String name;
  /** The type of data stored in this attribute */
  private final Class<? extends Number> type;

  /**
   * Creates a new Shader Attribute
   *
   * @param location the binding location of the attribute as specified in the Vertex Shader
   *     (layout=X)
   * @param name the name of the attribute as it appears in the Vertex Shader
   * @param dimension the dimension of the stored attribute (2 for vec2, 3 for vec3 ...)
   * @param type the type of data stored in this attribute
   */
  public ShaderAttribute(int location, String name, int dimension, Class<? extends Number> type) {
    this.location = location;
    this.dimension = dimension;
    this.name = name;
    this.type = type;
  }

  /**
   * Returns the binding location of the attribute as specified in the Vertex Shader (layout=X)
   *
   * @return the binding location of the attribute
   */
  public int getLocation() {
    return location;
  }

  /**
   * Returns the dimension of the stored attribute (2 for vec2, 3 for vec3 ...)
   *
   * @return the dimension of the stored attribute (2 for vec2, 3 for vec3 ...)
   */
  public int getDimension() {
    return dimension;
  }

  /**
   * Returns the name of the attribute as it appears in the Vertex Shader
   *
   * @return the name of the attribute as it appears in the Vertex Shader
   */
  public String getName() {
    return name;
  }

  /**
   * Returns the type of data stored in this attribute
   *
   * @return the type of data stored in this attribute
   */
  public Class<?> getDataType() {
    return type;
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
