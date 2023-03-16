/*
 * Copyright (c) 2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package org.alban098.graphics2j.common;

import java.util.Collection;
import org.alban098.graphics2j.common.shaders.ShaderProgram;
import org.alban098.graphics2j.common.shaders.data.Texture;
import org.alban098.graphics2j.common.shaders.data.VertexArrayObject;

public interface Renderer {

  /**
   * Returns a Collection of all {@link Texture}s the Renderer can use during the rendering of a
   * frame
   *
   * @return a Collection of all {@link Texture}s the Renderer can use during the rendering of a
   *     frame
   */
  Collection<Texture> getTextures();

  /**
   * Returns the number of drawcalls to the GPU that occurred during the last frame, emanating from
   * this Renderer
   *
   * @return the number of drawcalls to the GPU that occurred during the last frame, emanating from
   *     this Renderer
   */
  int getDrawCalls();

  /**
   * Returns the number of Objects rendered by this Renderer during the last frame
   *
   * @return the number of Objects rendered by this Renderer during the last frame
   */
  int getNbObjects();

  /**
   * Returns the {@link VertexArrayObject}s used by this Renderer
   *
   * @return a the {@link VertexArrayObject}s used by this Renderer
   */
  VertexArrayObject getVao();

  /**
   * Return the {@link ShaderProgram}s of this Renderer
   *
   * @return a the {@link ShaderProgram}s of this Renderer
   */
  Collection<ShaderProgram> getShaders();
}
