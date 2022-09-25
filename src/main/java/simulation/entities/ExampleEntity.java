/*
 * Copyright (c) 2022, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package simulation.entities;

import rendering.Texture;
import rendering.entities.Entity;
import rendering.entities.component.Transform;
import simulation.entities.components.ExampleComponent;

public class ExampleEntity extends Entity {

  public ExampleEntity(Transform transform, Texture texture) {
    super(transform, texture);
    addComponent("exampleComponent", new ExampleComponent());
  }

  @Override
  public void update(double elapsedTime) {
    super.update(elapsedTime);
    ExampleComponent component = getComponent("exampleComponent", ExampleComponent.class);
    if (component != null) {
      this.transform.rotate(component.getValue());
    }
  }
}
