/*
 * Copyright (c) 2022, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering.scene;

import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rendering.entities.RenderableObject;
import rendering.renderers.MasterRenderer;
import rendering.renderers.Renderer;

public class Scene {

  private static final Logger LOGGER = LoggerFactory.getLogger(Scene.class);

  private final Map<Class<? extends RenderableObject>, List<RenderableObject>> objects;
  private final MasterRenderer renderer;

  private int nbObjects = 0;

  public Scene(MasterRenderer renderer) {
    objects = new HashMap<>();
    this.renderer = renderer;
  }

  public void cleanUp() {
    for (Map.Entry<Class<? extends RenderableObject>, List<RenderableObject>> entry :
        objects.entrySet()) {
      entry.getValue().forEach(RenderableObject::cleanUp);
    }
  }

  public <T extends RenderableObject> void add(T object, Class<T> type) {
    objects.computeIfAbsent(type, t -> new ArrayList<>());
    objects.get(type).add(object);
    nbObjects++;

    renderer.register(object, type);
    LOGGER.trace("Added an object of type [{}] to the scene", object.getClass().getName());
  }

  public <T extends RenderableObject> void remove(T object, Class<T> type) {
    List<RenderableObject> list = objects.get(type);
    if (list != null) {
      if (list.remove(object)) {
        nbObjects--;
        if (list.isEmpty()) {
          objects.remove(type);
        }
      }
    }
    renderer.unregister(object, type);
    LOGGER.trace("Removed an object of type [{}] from the scene", object.getClass().getName());
  }

  public <T extends RenderableObject> List<T> getObjects(Class<T> ofType) {
    return (List<T>) objects.get(ofType);
  }

  public int getTotalObjects() {
    return nbObjects;
  }

  public Collection<Renderer<?>> getRenderers() {
    return renderer.getRenderers();
  }
}
