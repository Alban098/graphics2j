/*
 * Copyright (c) 2022, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package simulation.entities.components;

import rendering.entities.component.Component;

public class RotationProviderComponent extends Component {

  private float value;

  public RotationProviderComponent(float value) {
    this.value = value;
  }

  @Override
  public void cleanUp() {}

  @Override
  public void update() {}

  public float getValue() {
    return value;
  }
}
