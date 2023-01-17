/*
 * Copyright (c) 2022-2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package simulation.entities;

import org.alban098.engine2j.objects.entities.Entity;
import org.alban098.engine2j.objects.entities.component.RenderableComponent;
import org.alban098.engine2j.objects.entities.component.TransformComponent;
import org.alban098.engine2j.shaders.ShaderAttributes;
import org.joml.Vector2f;
import org.joml.Vector4f;

public class LightSource extends Entity {

  private final Vector4f color;

  public LightSource(Vector2f position, float intensity, Vector4f color) {
    super();
    this.color = new Vector4f(color.x, color.y, color.z, color.w);
    addComponent(new TransformComponent(position, intensity, 0));
    addComponent(new RenderableComponent(color));
  }

  public Vector4f getColor() {
    return color;
  }

  public void setColor(Vector4f color) {
    this.color.set(color);
    if (hasComponent(RenderableComponent.class)) {
      getRenderable().setAttributeValue(ShaderAttributes.COLOR_ATTRIBUTE, color);
    }
  }

  @Override
  public void update(double elapsedTime) {
    setColor(
        new Vector4f((float) Math.random(), (float) Math.random(), (float) Math.random(), 0.75f));
  }

  @Override
  protected void cleanUp() {}
}
