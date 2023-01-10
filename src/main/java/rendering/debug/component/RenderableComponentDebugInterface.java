/*
 * Copyright (c) 2022-2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering.debug.component;

import rendering.Texture;
import rendering.debug.DebugUtils;
import rendering.scene.entities.component.Component;
import rendering.scene.entities.component.RenderableComponent;

public final class RenderableComponentDebugInterface
    implements ComponentDebugInterface<RenderableComponent> {

  @Override
  public Class<RenderableComponent> getComponentClass() {
    return RenderableComponent.class;
  }

  @Override
  public String getDisplayName() {
    return "Renderable";
  }

  @Override
  public boolean draw(Component component) {
    if (component instanceof RenderableComponent) {
      RenderableComponent renderableComponent = (RenderableComponent) component;
      Texture texture = renderableComponent.getTexture();
      if (texture != null) {
        DebugUtils.drawTextureInfo(texture);
      }
      return true;
    }
    return false;
  }
}
