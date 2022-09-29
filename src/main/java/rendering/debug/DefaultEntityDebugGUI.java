/*
 * Copyright (c) 2022, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering.debug;

import rendering.entities.Entity;

public class DefaultEntityDebugGUI extends EntityDebugGUI {

  @Override
  protected void renderTabs(DebugLayer caller, Entity entity) {
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
  protected boolean showTransformTab() {
    return true;
  }

  @Override
  protected boolean showTextureTab() {
    return true;
  }
}
