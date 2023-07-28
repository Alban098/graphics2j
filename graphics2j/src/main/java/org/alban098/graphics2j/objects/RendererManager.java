/*
 * Copyright (c) 2022-2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package org.alban098.graphics2j.objects;

import static org.lwjgl.opengl.GL11.*;

import java.util.*;
import org.alban098.graphics2j.common.*;
import org.alban098.graphics2j.common.components.Camera;
import org.alban098.graphics2j.objects.renderers.AbstractRenderer;
import org.alban098.graphics2j.objects.renderers.DefaultRenderer;
import org.lwjgl.opengl.GL11;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is responsible for managing and dispatching rendering for all {@link
 * RenderableComponent}s to be rendered by {@link AbstractRenderer}s
 */
public final class RendererManager {

  /** Just a Logger to log events */
  private static final Logger LOGGER = LoggerFactory.getLogger(RendererManager.class);

  /**
   * A Map of all registered {@link AbstractRenderer} that will render {@link Renderable}, indexed
   * by Renderable type
   */
  private final Map<Class<? extends Renderable>, AbstractRenderer<? extends Renderable>> renderers;

  /** A List of all registered {@link AbstractRenderer}s, used for debugging interfaces */
  private final Set<Renderer> rendererList;
  /** The default {@link RenderingMode} */
  private RenderingMode renderingMode = RenderingMode.FILL;

  /** Initializes the Manager and create all mandatory {@link AbstractRenderer}s */
  public RendererManager() {
    this.renderers = new HashMap<>();
    this.rendererList = new HashSet<>();

    // default renderer
    registerRenderer(Renderable.class, new DefaultRenderer());

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
   * Attaches a {@link AbstractRenderer} to a {@link Renderable} forType
   *
   * @param forType the {@link Renderable} class forType to attach to
   * @param renderer the {@link AbstractRenderer} to attach
   * @param <T> the {@link Renderable} forType to attach to
   */
  public <T extends Renderable> void registerRenderer(
      Class<T> forType, AbstractRenderer<? extends Renderable> renderer) {
    renderers.put(forType, renderer);
    rendererList.add(renderer);
    LOGGER.info(
        "Registered new renderer of forType [{}] for entities of forType [{}]",
        renderer.getClass().getName(),
        forType.getName());
  }

  public <T extends Renderable> void clearRenderer(Class<T> forType) {
    renderers.get(forType).clear();
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
    for (AbstractRenderer<? extends Renderable> renderer : renderers.values()) {
      renderer.render(window, camera);
    }
  }

  /**
   * Register an {@link Renderable} to be rendered until unregistered
   *
   * @param renderable the {@link Renderable} to register
   */
  public void add(Renderable renderable) {
    AbstractRenderer<Renderable> renderer =
        (AbstractRenderer<Renderable>) renderers.get(renderable.getClass());
    if (renderer != null) {
      renderer.register(renderable);
    } else {
      AbstractRenderer<Renderable> defaultRenderer =
          (AbstractRenderer<Renderable>) renderers.get(Renderable.class);
      defaultRenderer.register(renderable);
    }
  }

  /**
   * Unregister an {@link Renderable} to no longer be rendered
   *
   * @param renderable the {@link Renderable} to unregister
   */
  public void remove(Renderable renderable) {
    AbstractRenderer<Renderable> renderer =
        (AbstractRenderer<Renderable>) renderers.get(renderable.getClass());
    if (renderer != null) {
      renderer.unregister(renderable);
    } else {
      AbstractRenderer<Renderable> defaultRenderer =
          (AbstractRenderer<Renderable>) renderers.get(Renderable.class);
      defaultRenderer.unregister(renderable);
    }
    LOGGER.debug(
        "Unregistered a Renderable of type [{}] with name {}",
        renderable.getClass().getSimpleName(),
        renderable.getRenderableComponent().getName());
  }

  /**
   * Returns a List of all {@link AbstractRenderer} registered into this Manager
   *
   * @return a List of all {@link AbstractRenderer} registered into this Manager
   */
  public Collection<Renderer> getRenderers() {
    return rendererList;
  }
}
