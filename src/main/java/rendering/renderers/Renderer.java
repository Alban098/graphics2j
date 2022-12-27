/*
 * Copyright (c) 2022, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering.renderers;

import java.util.Collection;
import org.joml.Vector4f;
import rendering.Texture;
import rendering.Window;
import rendering.scene.Camera;
import rendering.scene.Scene;

public interface Renderer<T> {

  void render(Window window, Camera camera, Scene scene, RenderingMode renderingMode);

  void cleanUp();

  void register(T object);

  void unregister(T object);

  Collection<Texture> getTextures();

  int getDrawCalls();

  int getNbObjects();

  void setWireframeColor(Vector4f wireframeColor);

  Vector4f getWireframeColor();
}
