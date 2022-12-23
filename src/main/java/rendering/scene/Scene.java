/*
 * Copyright (c) 2022, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering.scene;

import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rendering.renderers.Componentable;
import rendering.renderers.MasterRenderer;

public class Scene {

  private static final Logger LOGGER = LoggerFactory.getLogger(Scene.class);

  private final Map<Class<? extends Componentable>, List<Componentable>> objects;
  private final MasterRenderer renderer;

  private int nbObjects = 0;

  public Scene(MasterRenderer renderer) {
    objects = new HashMap<>();
    this.renderer = renderer;
  }

  public void cleanUp() {
    for (Map.Entry<Class<? extends Componentable>, List<Componentable>> entry :
        objects.entrySet()) {
      entry.getValue().forEach(Componentable::cleanUpInternal);
    }
  }

  public <T extends Componentable> void add(T object, Class<T> type) {
    objects.computeIfAbsent(type, t -> new ArrayList<>());
    objects.get(type).add(object);
    nbObjects++;

    renderer.register(object, type);
    LOGGER.trace("Added an object of type [{}] to the scene", object.getClass().getName());
  }

  public <T extends Componentable> void remove(T object, Class<T> type) {
    List<Componentable> list = objects.get(type);
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

  public List<? extends Componentable> getObjects(Class<? extends Componentable> ofType) {
    return objects.getOrDefault(ofType, Collections.emptyList());
  }

  public int getTotalObjects() {
    return nbObjects;
  }

  public Collection<Class<? extends Componentable>> getTypes() {
    return objects.keySet();
  }

  public void update(Class<? extends Componentable> entityClass, double elapsedTime) {
    for (Componentable e : getObjects(entityClass)) {
      e.updateInternal(elapsedTime);
    }
  }

  public void update(double elapsedTime) {
    for (Class<? extends Componentable> entityClass : getTypes()) {
      update(entityClass, elapsedTime);
    }
  }
}
