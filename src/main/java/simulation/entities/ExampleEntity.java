/*
 * Copyright (c) 2022, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package simulation.entities;

import rendering.entities.Entity;
import rendering.entities.component.TransformComponent;
import simulation.entities.components.RotationProviderComponent;

public class ExampleEntity extends Entity {

  public ExampleEntity() {
    super();
  }

  public ExampleEntity(String name) {
    super(name);
  }

  @Override
  public void update(double elapsedTime) {
    RotationProviderComponent component = getComponent(RotationProviderComponent.class);
    if (component != null && hasComponent(TransformComponent.class)) {
      this.getTransform().rotate((float) (component.getValue() * elapsedTime));
    }
  }

  @Override
  protected void cleanUp() {}
}
