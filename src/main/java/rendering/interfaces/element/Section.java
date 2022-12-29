/*
 * Copyright (c) 2022, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering.interfaces.element;

import org.joml.Vector4f;
import rendering.MouseInput;
import rendering.Texture;
import rendering.interfaces.UIElement;

public class Section extends UIElement<Section> {

  public Section() {
    this(new Vector4f());
  }

  public Section(Vector4f color) {
    super(color);
  }

  public Section(Texture texture) {
    super(texture);
  }

  @Override
  public void update(double elapsedTime) {}

  @Override
  public boolean input(MouseInput input) {
    return false;
  }
}
