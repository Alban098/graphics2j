/*
 * Copyright (c) 2022-2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package org.alban098.engine2j.example;

import org.alban098.engine2j.common.components.Transform;
import org.alban098.engine2j.common.shaders.data.Texture;
import org.alban098.engine2j.common.utils.ResourceLoader;
import org.alban098.engine2j.example.engine.Logic;
import org.alban098.engine2j.example.entities.ExampleColoredEntity;
import org.alban098.engine2j.example.entities.ExampleTexturedEntity;
import org.alban098.engine2j.example.interfaces.ExampleInterface;
import org.alban098.engine2j.example.renderer.ExampleColoredEntityRenderer;
import org.alban098.engine2j.fonts.FontManager;
import org.alban098.engine2j.interfaces.windows.UserInterface;
import org.joml.Random;
import org.joml.Vector2f;
import org.joml.Vector4f;

public class ExampleLogic extends Logic {

  @Override
  protected void initFontManager() {
    FontManager.registerFont("Candara", "assets/fonts/");
    FontManager.registerFont("Calibri", "assets/fonts/");
    FontManager.registerFont("Arial", "assets/fonts/");
  }

  /** Initialize meshes, models and generate the scene of the simulation */
  @Override
  public void init() {
    entityManager.mapEntityRenderer(ExampleColoredEntity.class, new ExampleColoredEntityRenderer());
    // generateEntities(20000);

    UserInterface ui = new ExampleInterface(getWindow(), "Demo");
    interfaceManager.add(ui);
    interfaceManager.setVisibility(ui, true);
  }

  private void generateEntities(int nb) {
    Texture texture0 = ResourceLoader.loadTexture("assets/textures/texture.png");
    Texture texture1 = ResourceLoader.loadTexture("assets/textures/texture2.png");

    Random random = new Random();
    for (int i = 0; i < nb / 2; i++) {
      Transform transform0 =
          new Transform(
              new Vector2f(random.nextFloat() * 150f - 75f, random.nextFloat() * 150f - 75f),
              random.nextFloat() + 0.2f,
              (float) (random.nextFloat() * Math.PI * 2f));
      entityManager.add(
          new ExampleTexturedEntity(random.nextInt(100) < 100 ? texture0 : texture1)
              .setTransform(transform0));
      Transform transform1 =
          new Transform(
              new Vector2f(random.nextFloat() * 150f - 75f, random.nextFloat() * 150f - 75f),
              random.nextFloat() + 0.2f,
              (float) (random.nextFloat() * Math.PI * 2f));
      entityManager.add(
          new ExampleColoredEntity(
                  new Vector4f(random.nextFloat(), random.nextFloat(), random.nextFloat(), 1))
              .setTransform(transform1));
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
  protected void update(double elapsedTime) {}

  /**
   * Called after all the scene element have been updated, may be called multiple time per frame
   * Entities and components are automatically updated before this call
   *
   * @param elapsedTime time elapsed since last update in seconds
   */
  @Override
  protected void end(double elapsedTime) {}
}
