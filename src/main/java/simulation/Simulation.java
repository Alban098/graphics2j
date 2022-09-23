/*
 * Copyright (c) 2022, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package simulation;

import org.joml.Random;
import org.joml.Vector2f;
import rendering.ConcreteLogic;
import rendering.ResourceLoader;
import rendering.Texture;
import rendering.Window;
import rendering.entities.Transform;

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
    generateEntities(50);
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
      scene.add(new ExampleEntity(transform, random.nextFloat() < .5f ? texture0 : texture1));
    }
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
