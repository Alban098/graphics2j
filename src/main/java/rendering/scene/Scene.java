/*
 * Copyright (c) 2022, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering.scene;

import java.util.ArrayList;
import java.util.List;
import rendering.Renderer;
import rendering.entities.Entity;

public class Scene {

  private final List<Entity> entities;
  private final Renderer renderer;

  public Scene(Renderer renderer) {
    entities = new ArrayList<>();
    this.renderer = renderer;
  }

  public void cleanUp() {
    entities.forEach(Entity::cleanUp);
  }

  public void add(Entity entity) {
    entities.add(entity);
    renderer.register(entity);
  }

  public void remove(Entity entity) {
    entities.remove(entity);
    renderer.unregister(entity);
  }

  public void update(double elapsedTime) {
    for (Entity e : entities) {
      e.process(elapsedTime);
    }
  }
}
