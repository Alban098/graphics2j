/*
 * Copyright (c) 2022, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering.renderers;

import java.util.Collection;
import org.joml.Vector4f;
import rendering.ILogic;
import rendering.Texture;
import rendering.Window;
import rendering.data.VAO;
import rendering.shaders.ShaderProgram;

public interface Renderer<T extends Renderable> {

  void render(Window window, ILogic logic, RenderingMode renderingMode);

  void cleanUp();

  void register(T object);

  void unregister(T object);

  Collection<Texture> getTextures();

  int getDrawCalls();

  int getNbObjects();

  void setWireframeColor(Vector4f wireframeColor);

  Vector4f getWireframeColor();

  Collection<VAO> getVaos();

  Collection<ShaderProgram> getShaders();
}
