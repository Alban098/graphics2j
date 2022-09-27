/*
 * Copyright (c) 2022, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering.renderers;

import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rendering.Window;
import rendering.entities.Entity;
import rendering.entities.RenderableObject;
import rendering.scene.Camera;
import rendering.scene.Scene;
import simulation.renderer.EntityRenderer;

public class MasterRenderer {

  private static final Logger LOGGER = LoggerFactory.getLogger(MasterRenderer.class);

  private Map<Class<? extends RenderableObject>, Renderer<? extends RenderableObject>> renderers;

  public void init() {
    renderers = new HashMap<>();

    // default renderer
    mapRenderer(Entity.class, new EntityRenderer());
  }

  public <T extends RenderableObject> void mapRenderer(
      Class<T> type, Renderer<? extends RenderableObject> renderer) {
    renderers.put(type, renderer);
    LOGGER.debug(
        "Registered new renderer of type [{}] for entities of type [{}]",
        renderer.getClass().getName(),
        type.getName());
  }

  public void render(Window window, Camera camera, Scene scene) {
    for (Renderer<?> renderer : renderers.values()) {
      renderer.renderNative(window, camera, scene);
    }
  }

  public void cleanUp() {
    for (Renderer<?> renderer : renderers.values()) {
      renderer.cleanUpNative();
    }
  }

  public <T extends RenderableObject> void register(T object, Class<T> type) {
    Renderer<T> renderer = (Renderer<T>) renderers.get(type);
    if (renderer != null) {
      renderer.register(object);
    } else if (object instanceof Entity) {
      Renderer<Entity> defaultRenderer = (Renderer<Entity>) renderers.get(Entity.class);
      defaultRenderer.register((Entity) object);
    }
  }

  public <T extends RenderableObject> void unregister(T object, Class<T> type) {
    Renderer<T> renderer = (Renderer<T>) renderers.get(type);
    if (renderer != null) {
      renderer.unregister(object);
    } else if (object instanceof Entity) {
      Renderer<Entity> defaultRenderer = (Renderer<Entity>) renderers.get(Entity.class);
      defaultRenderer.unregister((Entity) object);
    }
  }
}
