package org.alban098.graphics2j.common.shaders.data.model;

import org.alban098.graphics2j.common.shaders.data.vbo.FloatVertexBufferObject;

public class Model {

  private final String name;
  private final Primitive primitive;
  private final int primitiveCount;
  private final float[] vertices;
  private final float[] uvs;

  public Model(String name, Primitive primitive, int primitiveCount, float[] vertices, float[] uvs) {
    this.name = name;
    this.primitive = primitive;
    this.primitiveCount = primitiveCount;
    this.vertices = vertices;
    this.uvs = uvs;
  }

  public Primitive getPrimitive() {
    return primitive;
  }

  public int getVerticesCount() {
    return  primitive.verticesCount;
  }

  public void fillWithVertices(FloatVertexBufferObject vbo) {
    vbo.buffer(vertices);
  }

  public void fillWithUVs(FloatVertexBufferObject vbo) {
    vbo.buffer(uvs);
  }
}
