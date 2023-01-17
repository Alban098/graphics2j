/*
 * Copyright (c) 2022-2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package simulation;

import org.alban098.engine2j.core.Engine;
import org.alban098.engine2j.core.Logic;
import org.alban098.engine2j.core.Scene;
import org.alban098.engine2j.debug.component.ComponentDebugInterfaceProvider;
import org.alban098.engine2j.debug.renderable.RenderableDebugInterfaceProvider;
import org.alban098.engine2j.debug.renderable.entity.EntityDebugInterface;
import org.alban098.engine2j.debug.renderable.interfaces.UserInterfaceDebugInterface;
import org.alban098.engine2j.objects.entities.Entity;
import org.alban098.engine2j.objects.entities.component.RenderableComponent;
import org.alban098.engine2j.objects.entities.component.TransformComponent;
import org.alban098.engine2j.objects.interfaces.UserInterface;
import org.alban098.engine2j.shaders.data.Texture;
import org.alban098.engine2j.utils.ResourceLoader;
import org.joml.Random;
import org.joml.Vector2f;
import org.joml.Vector4f;
import simulation.debug.LightSourceDebugInterface;
import simulation.debug.RotationProviderComponentDebugInterface;
import simulation.entities.ExampleEntity;
import simulation.entities.LightSource;
import simulation.entities.components.RotationProviderComponent;
import simulation.interfaces.DemoInterface;
import simulation.renderer.LightRenderer;

public class Simulation extends Logic {

  @Override
  public void initDebugger() {
    RenderableDebugInterfaceProvider.register(LightSource.class, new LightSourceDebugInterface());
    RenderableDebugInterfaceProvider.register(Entity.class, new EntityDebugInterface<>());
    RenderableDebugInterfaceProvider.register(
        UserInterface.class, new UserInterfaceDebugInterface<>());
    ComponentDebugInterfaceProvider.register(new RotationProviderComponentDebugInterface());
  }

  /** Initialize meshes, models and generate the scene of the simulation */
  @Override
  public void init() {
    Engine engine = getEngine();
    Scene scene = getScene();
    engine.mapEntityRenderer(LightSource.class, new LightRenderer());
    generateEntities(50);

    Texture texture0 = ResourceLoader.loadTexture("resources/textures/texture.png");
    Texture texture1 = ResourceLoader.loadTexture("resources/textures/texture2.png");

    TransformComponent tr0 = new TransformComponent(new Vector2f(2, 0), .5f, 0);
    TransformComponent tr1 = new TransformComponent(new Vector2f(0, 2), .5f, 0);
    TransformComponent tr2 = new TransformComponent(new Vector2f(-2, 0), .5f, 0);
    TransformComponent tr3 = new TransformComponent(new Vector2f(0, -2), .5f, 0);

    ExampleEntity parent = new ExampleEntity();
    parent
        .addComponent(new TransformComponent(new Vector2f(2, 0), new Vector2f(1, 1), 0))
        .addComponent(new RenderableComponent(texture1))
        .addComponent(new RotationProviderComponent((float) Math.PI / 5));

    ExampleEntity child0 = createChild(tr0, texture0, texture1);
    ExampleEntity child1 = createChild(tr1, texture0, texture1);
    ExampleEntity child2 = createChild(tr2, texture0, texture1);
    ExampleEntity child3 = createChild(tr3, texture0, texture1);

    parent.addChild(child0);
    parent.addChild(child1);
    parent.addChild(child2);
    parent.addChild(child3);

    scene.add(parent, ExampleEntity.class);

    UserInterface ui = new DemoInterface(getWindow(), "Demo");
    scene.add(ui);
    scene.setVisibility(ui, true);
  }

  private ExampleEntity createChild(
      TransformComponent transform, Texture texture, Texture childTexture) {
    ExampleEntity entity = new ExampleEntity();
    entity
        .addComponent(transform)
        .addComponent(new RenderableComponent(texture))
        .addComponent(new RotationProviderComponent((float) (Math.PI * -.5f)));

    RenderableComponent childRenderable = new RenderableComponent(childTexture);
    entity.addChild(
        new ExampleEntity()
            .addComponent(new TransformComponent(new Vector2f(1.25f, 0), 0.5f, 0))
            .addComponent(childRenderable));
    entity.addChild(
        new ExampleEntity()
            .addComponent(new TransformComponent(new Vector2f(0, 1.25f), 0.5f, 0))
            .addComponent(childRenderable));
    entity.addChild(
        new ExampleEntity()
            .addComponent(new TransformComponent(new Vector2f(-1.25f, 0), 0.5f, 0))
            .addComponent(childRenderable));
    entity.addChild(
        new ExampleEntity()
            .addComponent(new TransformComponent(new Vector2f(0, -1.25f), 0.5f, 0))
            .addComponent(childRenderable));

    RotationProviderComponent rotationProviderComponentChild =
        new RotationProviderComponent((float) (Math.PI));
    entity.getChildren().forEach(e -> e.addComponent(rotationProviderComponentChild));

    entity.addChild(new LightSource(new Vector2f(1f), 0.5f, new Vector4f(1f, 0f, 0, 0.75f)));
    entity.addChild(new LightSource(new Vector2f(-1f), 0.5f, new Vector4f(1f, 0f, 0, 0.75f)));
    entity.addChild(new LightSource(new Vector2f(1f, -1f), 0.5f, new Vector4f(1f, 0f, 0, 0.75f)));
    entity.addChild(new LightSource(new Vector2f(-1f, 1f), 0.5f, new Vector4f(1f, 0f, 0, 0.75f)));

    return entity;
  }

  private void generateEntities(int nb) {
    Texture texture0 = ResourceLoader.loadTexture("resources/textures/texture.png");
    Texture texture1 = ResourceLoader.loadTexture("resources/textures/texture2.png");
    RenderableComponent r0 = new RenderableComponent(texture0);
    RenderableComponent r1 = new RenderableComponent(texture1);

    Random random = new Random();
    for (int i = 0; i < nb; i++) {
      TransformComponent transform =
          new TransformComponent(
              new Vector2f(random.nextFloat() * 20f - 10f, random.nextFloat() * 20f - 10f),
              random.nextFloat() + 0.2f,
              (float) (random.nextFloat() * Math.PI * 2f));
      getScene()
          .add(
              (ExampleEntity)
                  (new ExampleEntity()
                      .addComponent(transform)
                      .addComponent(random.nextFloat() < 1f ? r0 : r1)),
              ExampleEntity.class);
    }
  }

  /**
   * Called before all the scene element will be updated, may be called multiple time per frame
   * Entities and components are automatically updated after this call
   *
   * @param elapsedTime time elapsed since last update in seconds
   */
  @Override
  protected void prepare(double elapsedTime) {}

  @Override
  protected void update(double elapsedTime) {
    getScene().update(ExampleEntity.class, elapsedTime);
    getScene().update(LightSource.class, elapsedTime);
  }

  /**
   * Called after all the scene element have been updated, may be called multiple time per frame
   * Entities and components are automatically updated before this call
   *
   * @param elapsedTime time elapsed since last update in seconds
   */
  @Override
  protected void end(double elapsedTime) {}
}
