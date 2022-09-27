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
import rendering.shaders.ShaderAttributes;

public class LightSource extends RenderableObject {

  private final Vector3f color;

  public LightSource(Vector2f position, float intensity, Vector3f color) {
    super(new Transform(position, intensity, 0), color, ShaderAttributes.COLOR_ATTRIBUTE);
    this.color = new Vector3f(color.x, color.y, color.z);
  }

  public Vector3f getColor() {
    return color;
  }

  public void setColor(Vector3f color) {
    this.color.set(color);
    renderable.setAttributes(ShaderAttributes.COLOR_ATTRIBUTE, color);
  }

  @Override
  public void update(double elapsedTime) {
    setColor(new Vector3f((float) Math.random(), (float) Math.random(), (float) Math.random()));
  }
}
