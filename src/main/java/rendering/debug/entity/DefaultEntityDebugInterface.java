/*
 * Copyright (c) 2022, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering.debug.entity;

import rendering.debug.Debugger;
import rendering.entities.Entity;

public class DefaultEntityDebugInterface extends EntityDebugInterface<Entity> {

  @Override
  public Class<Entity> getEntityClass() {
    return Entity.class;
  }

  @Override
  protected void renderTabs(Debugger caller, Entity entity) {
    // Nothing to do
  }

  @Override
  protected boolean showComponentTab() {
    return true;
  }

  @Override
  protected boolean showChildrenTab() {
    return true;
  }

  @Override
  protected boolean showRenderingTab() {
    return true;
  }
}
