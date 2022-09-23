/*
 * Copyright (c) 2022, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering.data;

import static org.lwjgl.opengl.GL15.*;

public abstract class Vbo<T extends Number> {

  public static final int NO_LOCATION = -1;
  private final int id;
  protected final int dataDim;
  protected final int location;
  protected final int dataType;
  protected final int type;

  public Vbo(int type, int location, int dataDim, int maxCapacity, int dataType) {
    this.id = glGenBuffers();
    this.type = type;
    this.location = location;
    this.dataDim = dataDim;
    this.dataType = dataType;
    bind();
    glBufferData(type, (long) maxCapacity * dataDim, GL_DYNAMIC_DRAW);
  }

  public abstract void buffer(Number[] data);

  public abstract void load();

  public void bind() {
    glBindBuffer(type, id);
  }

  public void cleanUp() {
    glDeleteBuffers(id);
  }

  public static void unbind(int type) {
    glBindBuffer(type, 0);
  }
}
