/*
 * Copyright (c) 2022, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package simulation.entities;

import org.joml.Math;
import org.joml.Vector2f;
import org.joml.Vector3f;
import rendering.entities.RenderableObject;
import rendering.entities.component.Transform;
import simulation.renderer.ShaderAttributes;

public class LightSource extends RenderableObject {

  private final Float[] color;

  public LightSource(Vector2f position, float intensity, Vector3f color) {
    super(new Transform(position, intensity, 0), color, ShaderAttributes.COLOR_ATTRIBUTE);
    this.color = new Float[] {color.x, color.y, color.z};
  }

  public Float[] getColor() {
    return color;
  }

  public void setColor(Float[] color) {
    System.arraycopy(color, 0, this.color, 0, 3);
    renderable.setAttributes(ShaderAttributes.COLOR_ATTRIBUTE, color);
  }

  @Override
  public void update(double elapsedTime) {
    setColor(new Float[] {(float) Math.random(), (float) Math.random(), (float) Math.random()});
  }
}
