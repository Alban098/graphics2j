/*
 * Copyright (c) 2022-2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package org.alban098.graphics2j.example.entities;

import org.alban098.graphics2j.common.shaders.data.Texture;
import org.joml.Vector2f;

public class TexturedEntity extends UpdatableEntity {

  public TexturedEntity(Vector2f position, Vector2f scale, float rotation, Texture texture) {
    super(position, scale, rotation, 10, "Textured", texture);
  }

  @Override
  public void update(double elapsedTime) {
    transform.commit();
  }
}
