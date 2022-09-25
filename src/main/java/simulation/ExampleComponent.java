/*
 * Copyright (c) 2022, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package simulation;

import rendering.entities.Component;

public class ExampleComponent extends Component {

  private float value = 0;

  @Override
  public void cleanUp() {}

  @Override
  public void update() {
    value = (float) (Math.random() - 0.5f);
  }

  public float getValue() {
    return value;
  }
}
