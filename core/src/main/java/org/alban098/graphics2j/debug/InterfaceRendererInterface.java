/*
 * Copyright (c) 2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package org.alban098.graphics2j.debug;

import java.util.Collection;
import org.alban098.graphics2j.common.Renderer;
import org.alban098.graphics2j.interfaces.InterfaceRenderingManager;

public class InterfaceRendererInterface extends RendererInterface {

  private final InterfaceRenderingManager interfaceRenderingManager;

  public InterfaceRendererInterface(InterfaceRenderingManager interfaceRenderingManager) {
    super("User Interface Renderers");
    this.interfaceRenderingManager = interfaceRenderingManager;
  }

  @Override
  protected Collection<Renderer> getRenderers() {
    return interfaceRenderingManager.getRenderers();
  }
}
