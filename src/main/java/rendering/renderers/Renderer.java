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

public interface Renderer {

  Collection<Texture> getTextures();

  int getDrawCalls();

  int getNbObjects();

  Collection<VertexArrayObject> getVaos();

  Collection<ShaderProgram> getShaders();

  void cleanUp();
}
