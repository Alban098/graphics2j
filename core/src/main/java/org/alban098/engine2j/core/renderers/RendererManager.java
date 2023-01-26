/*
 * Copyright (c) 2022-2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package org.alban098.engine2j.core.renderers;

import static org.lwjgl.opengl.GL11.*;

import java.util.*;
import org.alban098.engine2j.core.Engine;
import org.alban098.engine2j.core.Scene;
import org.alban098.engine2j.core.Window;
import org.alban098.engine2j.core.objects.Renderable;
import org.alban098.engine2j.core.objects.entities.Entity;
import org.alban098.engine2j.core.objects.interfaces.UserInterface;
import org.alban098.engine2j.core.renderers.entities.DefaultEntityRenderer;
import org.alban098.engine2j.core.renderers.interfaces.FontRenderer;
import org.alban098.engine2j.core.renderers.interfaces.InterfaceRenderer;
import org.alban098.engine2j.core.renderers.interfaces.LineRenderer;
import org.lwjgl.opengl.GL11;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** This class is responsible for managing all {@link DebuggableRenderer}s of the {@link Engine} */
public final class RendererManager {

  private static final Logger LOGGER = LoggerFactory.getLogger(RendererManager.class);

  /**
   * A Map of all registered {@link DebuggableRenderer} that will render {@link Entity}, indexed by
   * Entity type
   */
  private Map<Class<? extends Entity>, RegisterableRenderer<? extends Renderable>> entityRenderers;
  /** The {@link DebuggableRenderer} in charge of rendering {@link UserInterface}s */
  private InterfaceRenderer interfaceRenderer;
  /**
   * The {@link DebuggableRenderer} in charge of rendering text, only used by the {@link
   * RendererManager#interfaceRenderer}
   */
  private FontRenderer fontRenderer;
  /**
   * The {@link DebuggableRenderer} in charge of rendering {@link
   * org.alban098.engine2j.core.objects.interfaces.element.Line}s, only used by the {@link
   * RendererManager#interfaceRenderer}
   */
  private LineRenderer lineRenderer;
  /** A List of all registered {@link DebuggableRenderer}s, used for debugging interfaces */
  private Set<DebuggableRenderer> rendererList;
  /** The default {@link RenderingMode} */
  private RenderingMode renderingMode = RenderingMode.FILL;

  /**
   * Initializes the Manager and create all mandatory {@link DebuggableRenderer}s
   *
   * @param window the {@link Window} in which to render
   */
  public void init(Window window) {
    this.entityRenderers = new HashMap<>();
    this.rendererList = new HashSet<>();
    this.fontRenderer = new FontRenderer();
    this.lineRenderer = new LineRenderer();
    this.interfaceRenderer = new InterfaceRenderer(window, fontRenderer, lineRenderer);

    // default renderer
    mapEntityRenderer(Entity.class, new DefaultEntityRenderer());
    rendererList.add(interfaceRenderer);
    rendererList.add(fontRenderer);
    rendererList.add(lineRenderer);

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
   * Attaches a {@link DebuggableRenderer} to an {@link Entity} type
   *
   * @param type the {@link Entity} class type to attach to
   * @param renderer the {@link DebuggableRenderer} to attach
   * @param <T> the {@link Entity } type to attach to
   */
  public <T extends Entity> void mapEntityRenderer(
      Class<T> type, RegisterableRenderer<? extends Entity> renderer) {
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
   * @param scene the {@link Scene} to render
   */
  public void render(Window window, Scene scene) {
    switch (renderingMode) {
      case FILL:
        glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
        glEnable(GL_TEXTURE_2D);
        break;
      case WIREFRAME:
        glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
        glDisable(GL_TEXTURE_2D);
        break;
    }

    // Prepare subsequent renderers
    fontRenderer.prepare();
    lineRenderer.prepare();
    interfaceRenderer.prepare();

    // Render objects
    for (RegisterableRenderer<? extends Renderable> renderer : entityRenderers.values()) {
      renderer.render(window, scene);
    }

    // Render GUIs on top
    interfaceRenderer.render(window, scene);
  }

  /** Clears the Manager by clearing every mapped {@link DebuggableRenderer} */
  public void cleanUp() {
    for (DebuggableRenderer renderer : rendererList) {
      renderer.cleanUp();
    }
  }

  /**
   * Register an {@link Entity} to be rendered until unregistered
   *
   * @param object the {@link Entity} to register
   * @param type the class type of the {@link Entity}
   */
  public void register(Entity object, Class<? extends Entity> type) {
    RegisterableRenderer<Renderable> renderer =
        (RegisterableRenderer<Renderable>) entityRenderers.get(type);
    if (renderer != null) {
      renderer.register(object);
    } else {
      RegisterableRenderer<Entity> defaultRenderer =
          (RegisterableRenderer<Entity>) entityRenderers.get(Entity.class);
      defaultRenderer.register(object);
    }
  }

  /**
   * Registers a {@link UserInterface} to be rendered until unregistered
   *
   * @param userInterface the {@link UserInterface} to register
   * @param <T> the type of the {@link UserInterface}
   */
  public <T extends UserInterface> void register(T userInterface) {
    interfaceRenderer.register(userInterface);
    LOGGER.debug(
        "Registered new UserInterface of type [{}] with name {}",
        userInterface.getClass().getSimpleName(),
        userInterface.getName());
  }

  /**
   * Unregister an {@link Entity} to no longer be rendered
   *
   * @param entity the {@link Entity} to unregister
   * @param type the class type of the {@link Entity}
   */
  public void unregister(Entity entity, Class<? extends Entity> type) {
    RegisterableRenderer<Entity> renderer =
        (RegisterableRenderer<Entity>) entityRenderers.get(type);
    if (renderer != null) {
      renderer.unregister(entity);
    } else {
      RegisterableRenderer<Entity> defaultRenderer =
          (RegisterableRenderer<Entity>) entityRenderers.get(Entity.class);
      defaultRenderer.unregister(entity);
    }
    LOGGER.debug(
        "Unregistered an Entity of type [{}] with name {}",
        entity.getClass().getSimpleName(),
        entity.getName());
  }

  /**
   * Unregister an {@link UserInterface} to no longer be rendered
   *
   * @param userInterface the {@link UserInterface} to unregister
   * @param <T> the class type of the {@link UserInterface}
   */
  public <T extends UserInterface> void unregister(T userInterface) {
    interfaceRenderer.unregister(userInterface);
    LOGGER.debug(
        "Unregistered a UserInterface of type [{}] with name {}",
        userInterface.getClass().getSimpleName(),
        userInterface.getName());
  }

  /**
   * Returns a Collection of all mapped {@link DebuggableRenderer}s
   *
   * @return a Collection of all mapped {@link DebuggableRenderer}s
   */
  public Collection<DebuggableRenderer> getRenderers() {
    return rendererList;
  }

  /**
   * Returns the {@link DebuggableRenderer} mapped to an {@link Entity} type
   *
   * @param type the class type of {@link Entity} to retrieve the {@link DebuggableRenderer} of
   * @return the {@link DebuggableRenderer} mapped to an {@link Entity} type
   * @param <T> the type of {@link Entity} to retrieve the {@link DebuggableRenderer} of
   */
  public <T extends Entity> RegisterableRenderer<? extends Renderable> getRenderer(Class<T> type) {
    RegisterableRenderer<T> value = (RegisterableRenderer<T>) entityRenderers.get(type);
    if (value == null) {
      return entityRenderers.get(Entity.class);
    }
    return value;
  }

  /**
   * Returns the {@link RegisterableRenderer} in charge of rendering {@link UserInterface}
   *
   * @return the {@link RegisterableRenderer} in charge of rendering {@link UserInterface}
   */
  public InterfaceRenderer getInterfaceRenderer() {
    return interfaceRenderer;
  }
}
