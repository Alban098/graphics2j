/*
 * Copyright (c) 2022, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package simulation.entities;

import rendering.Texture;
import rendering.entities.Entity;
import rendering.entities.component.Transform;
import simulation.entities.components.RotationProviderComponent;

public class ExampleEntity extends Entity {

  public ExampleEntity(Transform transform, Texture texture) {
    super(transform, texture);
  }

  @Override
  public void update(double elapsedTime) {
    super.update(elapsedTime);
    RotationProviderComponent component =
        getComponent("rotationProvider", RotationProviderComponent.class);
    if (component != null) {
      this.transform.rotate(component.getValue());
    }
  }
}
