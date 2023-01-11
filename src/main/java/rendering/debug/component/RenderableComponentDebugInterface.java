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

/**
 * A concrete implementation of {@link ComponentDebugInterface} in charge of displaying {@link
 * RenderableComponent}s
 */
public final class RenderableComponentDebugInterface
    implements ComponentDebugInterface<RenderableComponent> {

  /**
   * Returns the class type of the {@link Component}
   *
   * @return RenderableComponent.class
   */
  @Override
  public Class<RenderableComponent> getComponentClass() {
    return RenderableComponent.class;
  }

  /**
   * Returns the name of the {@link Component}
   *
   * @return "Renderable"
   */
  @Override
  public String getDisplayName() {
    return "Renderable";
  }

  /**
   * Draws the interface into its container
   *
   * @param component the {@link Component} to display in the interface
   * @return true the component is a {@link RenderableComponent} false otherwise
   */
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
