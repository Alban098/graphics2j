/*
 * Copyright (c) 2022-2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package simulation.entities;

import org.joml.Vector2f;
import org.joml.Vector3f;
import rendering.entities.Entity;
import rendering.entities.component.RenderableComponent;
import rendering.entities.component.TransformComponent;
import rendering.shaders.ShaderAttributes;

public class LightSource extends Entity {

  private final Vector3f color;

  public LightSource(Vector2f position, float intensity, Vector3f color) {
    super();
    this.color = new Vector3f(color.x, color.y, color.z);
    addComponent(new TransformComponent(position, intensity, 0));
    addComponent(new RenderableComponent(color));
  }

  public Vector3f getColor() {
    return color;
  }

  public void setColor(Vector3f color) {
    this.color.set(color);
    if (hasComponent(RenderableComponent.class)) {
      getRenderable().setAttributeValue(ShaderAttributes.COLOR_ATTRIBUTE, color);
    }
  }

  @Override
  public void update(double elapsedTime) {
    setColor(new Vector3f((float) Math.random(), (float) Math.random(), (float) Math.random()));
  }

  @Override
  protected void cleanUp() {}
}
