/*
 * Copyright (c) 2022-2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package example;

import example.debug.ExampleColoredEntityDebugInterface;
import example.debug.RotationProviderComponentDebugInterface;
import example.entities.ExampleColoredEntity;
import example.entities.ExampleTexturedEntity;
import example.entities.components.RotationProviderComponent;
import example.interfaces.ExampleInterface;
import example.renderer.ExampleColoredEntityRenderer;
import org.alban098.engine2j.core.Engine;
import org.alban098.engine2j.core.Logic;
import org.alban098.engine2j.core.Scene;
import org.alban098.engine2j.debug.component.ComponentDebugInterfaceProvider;
import org.alban098.engine2j.debug.renderable.RenderableDebugInterfaceProvider;
import org.alban098.engine2j.objects.entities.component.RenderableComponent;
import org.alban098.engine2j.objects.entities.component.TransformComponent;
import org.alban098.engine2j.objects.interfaces.UserInterface;
import org.alban098.engine2j.shaders.data.Texture;
import org.alban098.engine2j.utils.ResourceLoader;
import org.joml.Random;
import org.joml.Vector2f;

public class ExampleLogic extends Logic {

  @Override
  public void initDebugger() {
    RenderableDebugInterfaceProvider.register(
        ExampleColoredEntity.class, new ExampleColoredEntityDebugInterface());
    ComponentDebugInterfaceProvider.register(new RotationProviderComponentDebugInterface());
  }

  /** Initialize meshes, models and generate the scene of the simulation */
  @Override
  public void init() {
    Engine engine = getEngine();
    Scene scene = getScene();
    engine.mapEntityRenderer(ExampleColoredEntity.class, new ExampleColoredEntityRenderer());
    generateEntities(40000);

    UserInterface ui = new ExampleInterface(getWindow(), "Demo");
    scene.add(ui);
    scene.setVisibility(ui, true);
  }

  private void generateEntities(int nb) {
    Texture texture0 = ResourceLoader.loadTexture("engine2j/example/textures/texture.png");
    Texture texture1 = ResourceLoader.loadTexture("engine2j/example/textures/texture2.png");
    RenderableComponent r0 = new RenderableComponent(texture0);
    RenderableComponent r1 = new RenderableComponent(texture1);

    Random random = new Random();
    for (int i = 0; i < nb / 2; i++) {
      TransformComponent transform0 =
          new TransformComponent(
              new Vector2f(random.nextFloat() * 150f - 75f, random.nextFloat() * 150f - 75f),
              random.nextFloat() + 0.2f,
              (float) (random.nextFloat() * Math.PI * 2f));
      getScene()
          .add(
              new ExampleTexturedEntity()
                  .addComponent(transform0)
                  .addComponent(random.nextInt(100) < 100 ? r0 : r1)
                  .addComponent(new RotationProviderComponent(random.nextFloat())));
      TransformComponent transform1 =
          new TransformComponent(
              new Vector2f(random.nextFloat() * 150f - 75f, random.nextFloat() * 150f - 75f),
              random.nextFloat() + 0.2f,
              (float) (random.nextFloat() * Math.PI * 2f));
      getScene()
          .add(
              new ExampleColoredEntity()
                  .addComponent(transform1)
                  .addComponent(new RotationProviderComponent(random.nextFloat())));
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
    getScene().update(elapsedTime);
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
