/*
 * Copyright (c) 2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering.interfaces.element.text;

import org.joml.Vector2f;
import rendering.entities.component.RenderableComponent;
import rendering.entities.component.TransformComponent;
import rendering.renderers.Renderable;

public class Character implements Renderable {

  private final RenderableComponent renderableComponent;
  private final TransformComponent transformComponent;

  private final Vector2f position = new Vector2f();
  private final Vector2f size = new Vector2f();
  private final Vector2f offset = new Vector2f();

  public Character() {
    renderableComponent = new RenderableComponent();
    transformComponent = new TransformComponent();
  }

  @Override
  public RenderableComponent getRenderable() {
    return renderableComponent;
  }

  @Override
  public TransformComponent getTransform() {
    return transformComponent;
  }

  public void updateTransform(Vector2f viewport) {
    position.add(offset);
    float width = 2f * size.x / viewport.x;
    float height = 2f * size.y / viewport.y;
    transformComponent.setScale(width, height);
    transformComponent.setDisplacement(
        2f * position.x / viewport.x - 1 + width / 2f,
        2f * -position.y / viewport.y + 1 - height / 2f);
    transformComponent.update(null);
    position.sub(offset);
  }

  public void cleanup() {
    renderableComponent.cleanUp();
    transformComponent.cleanUp();
  }

  public void setPosition(Vector2f position) {
    this.position.set(position);
  }

  public void setSize(Vector2f size) {
    this.size.set(size);
  }

  public void addPosition(Vector2f position) {
    this.position.add(position);
  }

  public void setOffset(Vector2f offset) {
    this.offset.set(offset);
  }
}
