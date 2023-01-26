/*
 * Copyright (c) 2022-2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package org.alban098.example.engine2j.entities;

import org.alban098.engine2j.core.objects.entities.Entity;

public class ExampleTexturedEntity extends Entity {

  public ExampleTexturedEntity() {
    super();
  }

  public ExampleTexturedEntity(String name) {
    super(name);
  }

  @Override
  public void update(double elapsedTime) {}

  @Override
  protected void cleanUp() {}
}
