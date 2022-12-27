/*
 * Copyright (c) 2022, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering.renderers.interfaces;

import org.joml.Vector4f;

public class DefaultInterfaceRenderer extends InterfaceRenderer {

  public DefaultInterfaceRenderer() {
    super(new Vector4f(1, 0, 0, 1));
  }

  @Override
  public void cleanUp() {}
}
