/*
 * Copyright (c) 2022, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package simulation;

import org.joml.Random;
import org.joml.Vector2f;
import org.joml.Vector3f;
import rendering.ConcreteLogic;
import rendering.ResourceLoader;
import rendering.Texture;
import rendering.Window;
import rendering.entities.Entity;
import rendering.entities.component.Transform;
import simulation.entities.ExampleEntity;
import simulation.entities.LightSource;
import simulation.entities.components.RotationProviderComponent;
import simulation.renderer.LightRenderer;

public class Simulation extends ConcreteLogic {

  /**
   * Initialize meshes, models and generate the scene of the simulation
   *
   * @param window the Window when the Simulation will be rendered
   * @throws Exception thrown when models or textures can't be loaded
   */
  @Override
  public void init(Window window) throws Exception {
    super.init(window);
    renderer.mapRenderer(LightSource.class, new LightRenderer());
    // generateEntities(50);

    RotationProviderComponent rotationProviderComponent = new RotationProviderComponent(0.02f);

    Texture texture0 = ResourceLoader.loadTexture("src/main/resources/textures/texture.png");
    Texture texture1 = ResourceLoader.loadTexture("src/main/resources/textures/texture2.png");

    Transform tr0 = new Transform(new Vector2f(0, 0), 2, 0);
    Transform tr00 = new Transform(new Vector2f(3, 0), 1, 0);
    Transform tr01 = new Transform(new Vector2f(0, 3), 1, 0);
    Transform tr02 = new Transform(new Vector2f(-3, 0), 1, 0);
    Transform tr03 = new Transform(new Vector2f(0, -3), 1, 0);

    ExampleEntity parent = new ExampleEntity(tr0, texture0);
    parent.addComponent("rotationProvider", rotationProviderComponent);
    ExampleEntity child0 = creatChild(tr00, texture0, texture1);
    ExampleEntity child1 = creatChild(tr01, texture0, texture1);
    ExampleEntity child2 = creatChild(tr02, texture0, texture1);
    ExampleEntity child3 = creatChild(tr03, texture0, texture1);

    parent.addChild(child0);
    parent.addChild(child1);
    parent.addChild(child2);
    parent.addChild(child3);

    scene.add(parent, ExampleEntity.class);
    parent.getChildren().forEach(e -> scene.add((ExampleEntity) e, ExampleEntity.class));
    parent
        .getChildren()
        .forEach(
            e -> e.getChildren().forEach(e1 -> scene.add((ExampleEntity) e1, ExampleEntity.class)));
  }

  private ExampleEntity creatChild(Transform transform, Texture texture, Texture childTexture) {
    Transform tr0 = new Transform(new Vector2f(1.25f, 0), 0.5f, 0);
    Transform tr1 = new Transform(new Vector2f(0, 1.25f), 0.5f, 0);
    Transform tr2 = new Transform(new Vector2f(-1.25f, 0), 0.5f, 0);
    Transform tr3 = new Transform(new Vector2f(0, -1.25f), 0.5f, 0);

    RotationProviderComponent rotationProviderComponent = new RotationProviderComponent(0.1f);
    RotationProviderComponent rotationProviderComponentChild = new RotationProviderComponent(1f);

    ExampleEntity entity = new ExampleEntity(transform, texture);
    entity.addComponent("rotationProvider", rotationProviderComponent);
    entity.addChild(new ExampleEntity(tr0, childTexture));
    entity.addChild(new ExampleEntity(tr1, childTexture));
    entity.addChild(new ExampleEntity(tr2, childTexture));
    entity.addChild(new ExampleEntity(tr3, childTexture));
    entity
        .getChildren()
        .forEach(e -> e.addComponent("rotationProvider", rotationProviderComponentChild));

    return entity;
  }

  private void generateEntities(int nb) {
    Texture texture0 = ResourceLoader.loadTexture("src/main/resources/textures/texture.png");
    Texture texture1 = ResourceLoader.loadTexture("src/main/resources/textures/texture2.png");

    Random random = new Random();
    for (int i = 0; i < nb; i++) {
      Transform transform =
          new Transform(
              new Vector2f(random.nextFloat() * 20f - 10f, random.nextFloat() * 20f - 10f),
              random.nextFloat() + 0.2f,
              (float) (random.nextFloat() * Math.PI * 2f));
      scene.add(
          new ExampleEntity(transform, random.nextFloat() < 1f ? texture0 : texture1),
          ExampleEntity.class);
    }
    scene.add(new LightSource(new Vector2f(), 1, new Vector3f(1f, 0, 0)), LightSource.class);
  }

  /**
   * Called before all the scene element will be updated, may be called multiple time per frame
   * Entities and components are automatically updated after this call
   *
   * @param window the Window where the simulation is rendered
   * @param elapsedTime time elapsed since last update in seconds
   */
  @Override
  protected void preUpdate(Window window, double elapsedTime) {}

  @Override
  protected void update(Window window, double elapsedTime) {
    for (Entity e : scene.getObjects(ExampleEntity.class)) {
      e.update(elapsedTime);
    }
  }

  /**
   * Called after all the scene element have been updated, may be called multiple time per frame
   * Entities and components are automatically updated before this call
   *
   * @param window the Window where the simulation is rendered
   * @param elapsedTime time elapsed since last update in seconds
   */
  @Override
  protected void postUpdate(Window window, double elapsedTime) {}
}
