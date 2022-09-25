/*
 * Copyright (c) 2022, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package simulation;

import rendering.Texture;
import rendering.entities.Entity;
import rendering.entities.Transform;

public class ExampleEntity extends Entity {

  public ExampleEntity(Transform transform, Texture texture) {
    super(transform, texture);
    addComponent("exampleComponent", new ExampleComponent());
  }

  @Override
  protected void update(double elapsedTime) {
    ExampleComponent component = getComponent("exampleComponent", ExampleComponent.class);
    if (component != null) {
      this.transform.rotate(component.getValue());
    }
  }
}
