/*
 * Copyright (c) 2022-2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering.scene.entities.component;

import rendering.scene.entities.Entity;

public abstract class Component {

  public abstract void cleanUp();

  public abstract void update(Entity entity);
}
