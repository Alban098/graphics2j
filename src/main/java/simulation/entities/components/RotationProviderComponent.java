/*
 * Copyright (c) 2022, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package simulation.entities.components;

import rendering.entities.component.Component;
import rendering.renderers.Componentable;

public class RotationProviderComponent extends Component {

  private final float value;

  public RotationProviderComponent(float value) {
    this.value = value;
  }

  @Override
  public void cleanUp() {}

  @Override
  public void update(Componentable componentable) {}

  public float getValue() {
    return value;
  }
}
