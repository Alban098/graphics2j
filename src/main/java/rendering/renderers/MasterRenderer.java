/*
 * Copyright (c) 2022, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering.renderers;

import static org.lwjgl.opengl.GL11.*;

import java.util.*;
import org.lwjgl.opengl.GL11;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rendering.ILogic;
import rendering.Window;
import rendering.entities.Entity;
import rendering.interfaces.UserInterface;
import rendering.renderers.entity.DefaultEntityRenderer;
import rendering.renderers.interfaces.DefaultInterfaceRenderer;

public class MasterRenderer {

  private static final Logger LOGGER = LoggerFactory.getLogger(MasterRenderer.class);

  private Map<Class<? extends Entity>, Renderer<? extends Entity>> entityRenderers;
  private Map<Class<? extends UserInterface>, Renderer<? extends UserInterface>>
      userInterfaceRenderers;
  private Set<Renderer<? extends Renderable>> rendererList;
  private RenderingMode renderingMode = RenderingMode.FILL;

  public void init() {
    entityRenderers = new HashMap<>();
    userInterfaceRenderers = new HashMap<>();
    rendererList = new HashSet<>();
    // default renderer
    mapEntityRenderer(Entity.class, new DefaultEntityRenderer());
    mapUIRenderer(UserInterface.class, new DefaultInterfaceRenderer());
  }

  public void setRenderingMode(RenderingMode mode) {
    renderingMode = mode;
  }

  public <T extends Entity> void mapEntityRenderer(
      Class<T> type, Renderer<? extends Entity> renderer) {
    entityRenderers.put(type, renderer);
    rendererList.add(renderer);
    LOGGER.debug(
        "Registered new renderer of type [{}] for entities of type [{}]",
        renderer.getClass().getName(),
        type.getName());
  }

  public <T extends UserInterface> void mapUIRenderer(
      Class<T> type, Renderer<? extends UserInterface> renderer) {
    userInterfaceRenderers.put(type, renderer);
    rendererList.add(renderer);
    LOGGER.debug(
        "Registered new renderer of type [{}] for entities of type [{}]",
        renderer.getClass().getName(),
        type.getName());
  }

  public void render(Window window, ILogic logic) {
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

    // Render game objects
    for (Renderer<? extends Entity> renderer : entityRenderers.values()) {
      renderer.render(window, logic.getCamera(), logic.getScene(), renderingMode);
    }

    // Render GUIs on top
    for (Renderer<? extends UserInterface> renderer : userInterfaceRenderers.values()) {
      renderer.render(window, logic.getCamera(), logic.getScene(), renderingMode);
    }
  }

  public void cleanUp() {
    for (Renderer<? extends Entity> renderer : entityRenderers.values()) {
      renderer.cleanUp();
    }
    for (Renderer<? extends UserInterface> renderer : userInterfaceRenderers.values()) {
      renderer.cleanUp();
    }
  }

  public <T extends Entity> void registerEntity(T object, Class<T> type) {
    Renderer<T> renderer = (Renderer<T>) entityRenderers.get(type);
    if (renderer != null) {
      renderer.register(object);
    } else {
      Renderer<Entity> defaultRenderer = (Renderer<Entity>) entityRenderers.get(Entity.class);
      defaultRenderer.register(object);
    }
  }

  public <T extends UserInterface> void registerUI(T userInterface, Class<T> type) {
    Renderer<T> renderer = (Renderer<T>) userInterfaceRenderers.get(type);
    if (renderer != null) {
      renderer.register(userInterface);
    }
  }

  public <T extends Entity> void unregisterEntity(T object, Class<T> type) {
    Renderer<T> renderer = (Renderer<T>) entityRenderers.get(type);
    if (renderer != null) {
      renderer.unregister(object);
    } else {
      Renderer<Entity> defaultRenderer = (Renderer<Entity>) entityRenderers.get(Entity.class);
      defaultRenderer.unregister(object);
    }
  }

  public <T extends UserInterface> void unregisterUI(T userInterface, Class<T> type) {
    Renderer<T> renderer = (Renderer<T>) userInterfaceRenderers.get(type);
    if (renderer != null) {
      renderer.unregister(userInterface);
    }
  }

  public Collection<Renderer<? extends Renderable>> getRenderers() {
    return rendererList;
  }

  public <T extends Renderable> Renderer<? extends Renderable> getRenderer(Class<T> type) {
    Renderer<T> value = (Renderer<T>) entityRenderers.get(type);
    if (value == null) {
      return entityRenderers.get(Entity.class);
    }
    return value;
  }
}
