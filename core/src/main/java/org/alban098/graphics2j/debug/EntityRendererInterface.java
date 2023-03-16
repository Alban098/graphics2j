/*
 * Copyright (c) 2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package org.alban098.graphics2j.debug;

import java.util.Collection;
import org.alban098.graphics2j.common.Renderer;
import org.alban098.graphics2j.entities.EntityRenderingManager;

public class EntityRendererInterface extends RendererInterface {

  private final EntityRenderingManager entityRenderingManager;

  public EntityRendererInterface(EntityRenderingManager entityRenderingManager) {
    super("Entity Renderers");
    this.entityRenderingManager = entityRenderingManager;
  }

  @Override
  protected Collection<Renderer> getRenderers() {
    return entityRenderingManager.getRenderers();
  }
}
