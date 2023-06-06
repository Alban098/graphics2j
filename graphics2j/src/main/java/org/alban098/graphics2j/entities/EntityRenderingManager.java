/*
 * Copyright (c) 2022-2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package org.alban098.graphics2j.entities;

import static org.lwjgl.opengl.GL11.*;

import java.util.*;
import org.alban098.graphics2j.common.Renderable;
import org.alban098.graphics2j.common.Renderer;
import org.alban098.graphics2j.common.RenderingMode;
import org.alban098.graphics2j.common.Window;
import org.alban098.graphics2j.common.components.Camera;
import org.alban098.graphics2j.entities.renderers.DefaultEntityRenderer;
import org.alban098.graphics2j.entities.renderers.EntityRenderer;
import org.lwjgl.opengl.GL11;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is responsible for managing and dispatching rendering for all {@link Renderable}s to
 * be rendered by {@link EntityRenderer}s
 */
public final class EntityRenderingManager {

  /** Just a Logger to log events */
  private static final Logger LOGGER = LoggerFactory.getLogger(EntityRenderingManager.class);

  /**
   * A Map of all registered {@link EntityRenderer} that will render {@link Renderable}, indexed by
   * Entity type
   */
  private final Map<Class<? extends Renderable>, EntityRenderer<? extends Renderable>>
      entityRenderers;

  /** A List of all registered {@link EntityRenderer}s, used for debugging interfaces */
  private final Set<Renderer> rendererList;
  /** The default {@link RenderingMode} */
  private RenderingMode renderingMode = RenderingMode.FILL;

  /** Initializes the Manager and create all mandatory {@link EntityRenderer}s */
  public EntityRenderingManager() {
    this.entityRenderers = new HashMap<>();
    this.rendererList = new HashSet<>();

    // default renderer
    registerRenderer(Renderable.class, new DefaultEntityRenderer());

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
   * Attaches a {@link EntityRenderer} to an {@link Renderable} forType
   *
   * @param forType the {@link Renderable} class forType to attach to
   * @param renderer the {@link EntityRenderer} to attach
   * @param <T> the {@link Renderable} forType to attach to
   */
  public <T extends Renderable> void registerRenderer(
      Class<T> forType, EntityRenderer<? extends Renderable> renderer) {
    entityRenderers.put(forType, renderer);
    rendererList.add(renderer);
    LOGGER.info(
        "Registered new renderer of forType [{}] for entities of forType [{}]",
        renderer.getClass().getName(),
        forType.getName());
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
    for (EntityRenderer<? extends Renderable> renderer : entityRenderers.values()) {
      renderer.render(window, camera);
    }
  }

  /**
   * Register an {@link Renderable} to be rendered until unregistered
   *
   * @param entity the {@link Renderable} to register
   */
  public void add(Renderable entity) {
    EntityRenderer<Renderable> renderer =
        (EntityRenderer<Renderable>) entityRenderers.get(entity.getClass());
    if (renderer != null) {
      renderer.register(entity);
    } else {
      EntityRenderer<Renderable> defaultRenderer =
          (EntityRenderer<Renderable>) entityRenderers.get(Renderable.class);
      defaultRenderer.register(entity);
    }
  }

  /**
   * Unregister an {@link Renderable} to no longer be rendered
   *
   * @param entity the {@link Renderable} to unregister
   */
  public void remove(Renderable entity) {
    EntityRenderer<Renderable> renderer =
        (EntityRenderer<Renderable>) entityRenderers.get(entity.getClass());
    if (renderer != null) {
      renderer.unregister(entity);
    } else {
      EntityRenderer<Renderable> defaultRenderer =
          (EntityRenderer<Renderable>) entityRenderers.get(Renderable.class);
      defaultRenderer.unregister(entity);
    }
    LOGGER.debug(
        "Unregistered an Entity of type [{}] with name {}",
        entity.getClass().getSimpleName(),
        entity.getName());
  }

  /**
   * Returns a List of all {@link EntityRenderer} registered into this Manager
   *
   * @return a List of all {@link EntityRenderer} registered into this Manager
   */
  public Collection<Renderer> getRenderers() {
    return rendererList;
  }
}
