/*
 * Copyright (c) 2022-2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering.renderers;

import java.util.Collection;
import rendering.Texture;
import rendering.shaders.ShaderProgram;
import rendering.shaders.data.VertexArrayObject;

/** Represents an abstraction of a Renderer that is debuggable */
public interface DebuggableRenderer {

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
   * Returns a Collection of all {@link VertexArrayObject}s used by this Renderer
   *
   * @return a Collection of all {@link VertexArrayObject}s used by this Renderer
   */
  Collection<VertexArrayObject> getVaos();

  /**
   * Returns a Collection of all {@link ShaderProgram}s used by this Renderer
   *
   * @return a Collection of all {@link ShaderProgram}s used by this Renderer
   */
  Collection<ShaderProgram> getShaders();

  /** Clears this Renderer from RAM and VRAM */
  void cleanUp();
}
