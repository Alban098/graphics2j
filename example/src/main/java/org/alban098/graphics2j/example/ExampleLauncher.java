/*
 * Copyright (c) 2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package org.alban098.graphics2j.example;

import java.util.HashSet;
import java.util.Set;
import org.alban098.graphics2j.common.Window;
import org.alban098.graphics2j.common.components.Camera;
import org.alban098.graphics2j.common.shaders.data.Texture;
import org.alban098.graphics2j.common.utils.ResourceLoader;
import org.alban098.graphics2j.debug.DebugImGuiTab;
import org.alban098.graphics2j.entities.EntityRenderingManager;
import org.alban098.graphics2j.example.entities.ColoredEntity;
import org.alban098.graphics2j.example.entities.TexturedEntity;
import org.alban098.graphics2j.example.entities.UpdatableEntity;
import org.alban098.graphics2j.example.interfaces.ExampleInterface;
import org.alban098.graphics2j.example.renderer.ColoredEntityRenderer;
import org.alban098.graphics2j.fonts.FontManager;
import org.alban098.graphics2j.input.MouseState;
import org.alban098.graphics2j.interfaces.InterfaceRenderingManager;
import org.alban098.graphics2j.interfaces.windows.UserInterface;
import org.apache.log4j.PropertyConfigurator;
import org.joml.Random;
import org.joml.Vector2f;
import org.joml.Vector4f;

public class ExampleLauncher {

  private static final int FPS = 1200;
  private static final int TPS = 1200;
  private static final int NB_ENTITIES = 1000;
  private static final boolean FPS_CAP = false;

  private final Window window;
  private final Timer timer;
  private final MouseState mouseState;
  private final EntityRenderingManager entityManager;
  private final InterfaceRenderingManager interfaceManager;
  private final Camera camera;

  private final Set<UpdatableEntity> entities;

  public static void main(String[] args) {
    PropertyConfigurator.configure("./log4j.properties");
    new ExampleLauncher();
  }

  public ExampleLauncher() {
    window = new Window("Example", 1200, 600, true);
    mouseState = new MouseState();
    mouseState.linkCallbacks(window);
    timer = new Timer();
    entityManager = new EntityRenderingManager();
    interfaceManager = new InterfaceRenderingManager(window, mouseState);
    camera = new Camera(window, new Vector2f());
    entities = new HashSet<>();

    init();
    loop();
    window.cleanUp();
  }

  private void init() {
    window.addDebugInterface(new DebugImGuiTab(window, entityManager, interfaceManager));

    FontManager.registerFont("Candara", "assets/fonts/");
    FontManager.registerFont("Calibri", "assets/fonts/");
    FontManager.registerFont("Arial", "assets/fonts/");

    // Setup custom entity renderer
    entityManager.registerRenderer(ColoredEntity.class, new ColoredEntityRenderer());

    Texture texture0 = ResourceLoader.loadTexture("assets/textures/texture.png");
    Texture texture1 = ResourceLoader.loadTexture("assets/textures/texture2.png");

    Random random = new Random();
    for (int i = 0; i < NB_ENTITIES / 2; i++) {
      UpdatableEntity texturedEntity =
          new TexturedEntity(
              new Vector2f(random.nextFloat() * 150f - 75f, random.nextFloat() * 150f - 75f),
              new Vector2f(random.nextFloat() + 0.2f),
              (float) (random.nextFloat() * Math.PI * 2f),
              random.nextInt(100) < 50 ? texture0 : texture1);
      UpdatableEntity coloredEntity =
          new ColoredEntity(
              new Vector2f(random.nextFloat() * 150f - 75f, random.nextFloat() * 150f - 75f),
              new Vector2f(random.nextFloat() + 0.2f),
              (float) (random.nextFloat() * Math.PI * 2f),
              new Vector4f(random.nextFloat(), random.nextFloat(), random.nextFloat(), 1));

      entities.add(texturedEntity);
      entities.add(coloredEntity);

      entityManager.add(texturedEntity);
      entityManager.add(coloredEntity);
    }

    UserInterface ui = new ExampleInterface(window, "Demo");
    interfaceManager.add(ui);
    interfaceManager.setVisibility(ui, true);
  }

  private void loop() {
    double accumulator = 0f;
    double interval;

    // While running
    while (!window.windowShouldClose()) {
      window.newFrame();

      // Calculate an update duration and get the elapsed time since last loop
      interval = 1f / TPS;
      accumulator += timer.getElapsedTime();

      // Handle user inputs
      mouseState.update();
      camera.update(window, mouseState);
      interfaceManager.processUserInput();

      // Update as many times as needed to respect the number of updates per second
      while (accumulator >= interval) {
        update(interval);
        accumulator -= interval;
      }

      // Render the frame
      entityManager.render(window, camera);
      interfaceManager.render();

      // Draw the frame
      window.endFrame();

      if (FPS_CAP) {
        sync();
      }
    }
  }

  private void update(double elapsedTime) {
    interfaceManager.update(elapsedTime);
    entities.forEach(e -> e.update(elapsedTime));
  }

  private void sync() {
    float loopSlot = 1f / FPS;
    double endTime = timer.getLastFrameTime() + loopSlot;
    while (timer.getTime() < endTime) {
      try {
        Thread.sleep(1);
      } catch (InterruptedException ignored) {
      }
    }
  }
}
