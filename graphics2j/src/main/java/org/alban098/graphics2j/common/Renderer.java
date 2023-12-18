/*
 * Copyright (c) 2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package org.alban098.graphics2j.common;

import java.util.Collection;
import java.util.Map;
import org.alban098.graphics2j.common.shaders.ShaderProgram;
import org.alban098.graphics2j.common.shaders.data.Texture;
import org.alban098.graphics2j.common.shaders.data.vao.ArrayObject;

/** An Interface referencing all common behavior a Renderer should be able to do */
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
   * Returns the time passed during rendering by this Renderer, binding {@link ShaderProgram},
   * {@link Texture}s loading {@link org.alban098.graphics2j.common.shaders.data.uniform.Uniform}s,
   * batching and rendering elements
   *
   * @return the total rendering time of this Renderer, in seconds
   */
  double getRenderingTime();

  /**
   * Returns the number of {@link ShaderProgram#bind()} calls during this rendering pass
   *
   * @return the number of {@link ShaderProgram#bind()} calls during this rendering pass
   */
  int getShaderBoundCount();

  /**
   * Returns a Map of the times passed with each {@link ShaderProgram} of the Renderer bound, index
   * by {@link ShaderProgram}
   *
   * @return a Map of time passed in each {@link ShaderProgram} of the Renderer
   */
  Map<ShaderProgram, Double> getShaderTimes();

  /**
   * Returns the {@link ArrayObject}s used by this Renderer
   *
   * @return a the {@link ArrayObject}s used by this Renderer
   */
  ArrayObject getVao();

  /**
   * Return the {@link ShaderProgram}s of this Renderer
   *
   * @return a the {@link ShaderProgram}s of this Renderer
   */
  Collection<ShaderProgram> getShaders();

  /**
   * Set the {@link RenderingMode} to be used to render entities
   *
   * @param mode the new {@link RenderingMode}
   */
  void setRenderingMode(RenderingMode mode);

  /**
   * Return the current {@link RenderingMode}
   *
   * @return the current {@link RenderingMode}
   */
  RenderingMode getRenderingMode();
}
