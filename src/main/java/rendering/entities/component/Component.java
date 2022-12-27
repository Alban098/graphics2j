/*
 * Copyright (c) 2022, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering.entities.component;

import rendering.entities.Entity;

public abstract class Component {

  public abstract void cleanUp();

  public abstract void update(Entity entity);
}
