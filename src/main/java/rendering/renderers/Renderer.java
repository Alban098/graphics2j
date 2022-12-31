/*
 * Copyright (c) 2022, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering.renderers;

import java.util.Collection;
import rendering.Texture;
import rendering.data.VertexArrayObject;
import rendering.shaders.ShaderProgram;

public interface Renderer {

  Collection<Texture> getTextures();

  int getDrawCalls();

  int getNbObjects();

  Collection<VertexArrayObject> getVaos();

  Collection<ShaderProgram> getShaders();

  void cleanUp();
}
