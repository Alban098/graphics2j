/*
 * Copyright (c) 2022, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering.scene;

import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rendering.interfaces.UserInterface;
import rendering.renderers.MasterRenderer;

public class InterfaceManager {

  private static final Logger LOGGER = LoggerFactory.getLogger(InterfaceManager.class);

  private final Map<Class<? extends UserInterface>, List<UserInterface>> objects;
  private final MasterRenderer renderer;

  private int nbObjects = 0;

  public InterfaceManager(MasterRenderer renderer) {
    this.objects = new HashMap<>();
    this.renderer = renderer;
  }

  public void cleanUp() {
    for (Map.Entry<Class<? extends UserInterface>, List<UserInterface>> entry :
        objects.entrySet()) {
      entry.getValue().forEach(UserInterface::cleanUp);
    }
  }

  public <T extends UserInterface> void add(T object, Class<T> type) {
    objects.computeIfAbsent(type, t -> new ArrayList<>());
    objects.get(type).add(object);
    nbObjects++;

    renderer.registerUI(object, type);
    LOGGER.trace("Added an object of type [{}] to the scene", object.getClass().getName());
  }

  public <T extends UserInterface> void remove(T object, Class<T> type) {
    List<UserInterface> list = objects.get(type);
    if (list != null) {
      if (list.remove(object)) {
        nbObjects--;
        if (list.isEmpty()) {
          objects.remove(type);
        }
      }
    }
    renderer.unregisterUI(object, type);
    LOGGER.trace("Removed an object of type [{}] from the scene", object.getClass().getName());
  }

  public List<? extends UserInterface> getObjects(Class<? extends UserInterface> ofType) {
    return objects.getOrDefault(ofType, Collections.emptyList());
  }

  public int getTotalObjects() {
    return nbObjects;
  }

  public Collection<Class<? extends UserInterface>> getTypes() {
    return objects.keySet();
  }

  public void update(Class<UserInterface> userInterfaceClass, double elapsedTime) {
    for (UserInterface e : getObjects(userInterfaceClass)) {
      e.update(elapsedTime, null);
    }
  }
}
