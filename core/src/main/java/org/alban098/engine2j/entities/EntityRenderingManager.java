/*
 * Copyright (c) 2022-2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package org.alban098.engine2j.entities;

import static org.lwjgl.opengl.GL11.*;

import java.util.*;
import org.alban098.engine2j.common.RenderingMode;
import org.alban098.engine2j.common.Window;
import org.alban098.engine2j.common.components.Camera;
import org.alban098.engine2j.engine.Engine;
import org.alban098.engine2j.entities.renderers.DefaultEntityRenderer;
import org.alban098.engine2j.entities.renderers.EntityRenderer;
import org.lwjgl.opengl.GL11;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** This class is responsible for managing all {@link EntityRenderer}s of the {@link Engine} */
public final class EntityRenderingManager {

  /** Just a Logger to log events */
  private static final Logger LOGGER = LoggerFactory.getLogger(EntityRenderingManager.class);

  /**
   * A Map of all registered {@link EntityRenderer} that will render {@link Entity}, indexed by
   * Entity type
   */
  private Map<Class<? extends Entity>, EntityRenderer<? extends Entity>> entityRenderers;

  /** A List of all registered {@link EntityRenderer}s, used for debugging interfaces */
  private Set<EntityRenderer<? extends Entity>> rendererList;
  /** The default {@link RenderingMode} */
  private RenderingMode renderingMode = RenderingMode.FILL;

  /** Initializes the Manager and create all mandatory {@link EntityRenderer}s */
  public EntityRenderingManager() {
    this.entityRenderers = new HashMap<>();
    this.rendererList = new HashSet<>();

    // default renderer
    mapEntityRenderer(Entity.class, new DefaultEntityRenderer());

    LOGGER.info("Successfully initialized RendererManager");
  }

  /**
   * Changes the current {@link RenderingMode}
   *
   * @param mode the requested {@link RenderingMode}
   */
  public void setRenderingMode(RenderingMode mode) {
    renderingMode = mode;
    LOGGER.debug("Rendering mode changed to {}", renderingMode);
  }

  /**
   * Attaches a {@link EntityRenderer} to an {@link Entity} type
   *
   * @param type the {@link Entity} class type to attach to
   * @param renderer the {@link EntityRenderer} to attach
   * @param <T> the {@link Entity } type to attach to
   */
  public <T extends Entity> void mapEntityRenderer(
      Class<T> type, EntityRenderer<? extends Entity> renderer) {
    entityRenderers.put(type, renderer);
    rendererList.add(renderer);
    LOGGER.info(
        "Registered new renderer of type [{}] for entities of type [{}]",
        renderer.getClass().getName(),
        type.getName());
  }

  /**
   * Renders a Scene to the screen
   *
   * @param window the {@link Window} to render into
   * @param camera the {@link Camera} to render from
   */
  public void render(Window window, Camera camera) {
    switch (renderingMode) {
      case FILL -> {
        glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
        glEnable(GL_TEXTURE_2D);
      }
      case WIREFRAME -> {
        glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
        glDisable(GL_TEXTURE_2D);
      }
    }

    // Render objects
    for (EntityRenderer<? extends Entity> renderer : entityRenderers.values()) {
      renderer.render(window, camera);
    }
  }

  /** Clears the Manager by clearing every mapped {@link EntityRenderer} */
  public void cleanUp() {
    for (EntityRenderer<? extends Entity> renderer : rendererList) {
      renderer.cleanUp();
    }
  }

  /**
   * Register an {@link Entity} to be rendered until unregistered
   *
   * @param entity the {@link Entity} to register
   */
  public void add(Entity entity) {
    EntityRenderer<Entity> renderer =
        (EntityRenderer<Entity>) entityRenderers.get(entity.getClass());
    if (renderer != null) {
      renderer.register(entity);
    } else {
      EntityRenderer<Entity> defaultRenderer =
          (EntityRenderer<Entity>) entityRenderers.get(Entity.class);
      defaultRenderer.register(entity);
    }
  }

  /**
   * Unregister an {@link Entity} to no longer be rendered
   *
   * @param entity the {@link Entity} to unregister
   */
  public void remove(Entity entity) {
    EntityRenderer<Entity> renderer =
        (EntityRenderer<Entity>) entityRenderers.get(entity.getClass());
    if (renderer != null) {
      renderer.unregister(entity);
    } else {
      EntityRenderer<Entity> defaultRenderer =
          (EntityRenderer<Entity>) entityRenderers.get(Entity.class);
      defaultRenderer.unregister(entity);
    }
    LOGGER.debug(
        "Unregistered an Entity of type [{}] with name {}",
        entity.getClass().getSimpleName(),
        entity.getName());
  }
}
