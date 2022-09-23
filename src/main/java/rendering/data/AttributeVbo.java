/*
 * Copyright (c) 2022, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering.data;

public abstract class AttributeVbo<T extends Number> extends Vbo<T> {

  protected final int location;

  public AttributeVbo(int type, int location, int dataDim, int maxCapacity, int dataType) {
    super(type, dataDim, maxCapacity, dataType);
    this.location = location;
  }
}
