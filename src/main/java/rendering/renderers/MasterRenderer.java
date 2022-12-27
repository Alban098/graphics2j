/*
 * Copyright (c) 2022, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering.renderers;

import static org.lwjgl.opengl.GL11.*;

import java.util.*;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rendering.ILogic;
import rendering.Window;
import rendering.entities.Entity;
import rendering.interfaces.UserInterface;
import rendering.renderers.entity.DefaultEntityRenderer;
import rendering.renderers.interfaces.InterfaceRenderer;

public class MasterRenderer {

  private static final Logger LOGGER = LoggerFactory.getLogger(MasterRenderer.class);

  private Map<Class<? extends Entity>, Renderer<? extends Renderable>> entityRenderers;
  private InterfaceRenderer interfaceRenderer;
  private Set<Renderer<? extends Renderable>> rendererList;
  private RenderingMode renderingMode = RenderingMode.FILL;

  public void init() {
    this.entityRenderers = new HashMap<>();
    this.rendererList = new HashSet<>();
    this.interfaceRenderer = new InterfaceRenderer(new Vector4f(1f, 1f, 1f, 1f));

    // default renderer
    mapEntityRenderer(Entity.class, new DefaultEntityRenderer());
    rendererList.add(interfaceRenderer);
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
    for (Renderer<? extends Renderable> renderer : entityRenderers.values()) {
      renderer.render(window, logic, renderingMode);
    }

    // Render GUIs on top
    interfaceRenderer.render(window, logic, renderingMode);
  }

  public void cleanUp() {
    for (Renderer<? extends Renderable> renderer : entityRenderers.values()) {
      renderer.cleanUp();
    }
    interfaceRenderer.cleanUp();
  }

  public void register(Renderable object, Class<? extends Renderable> type) {
    Renderer<Renderable> renderer = (Renderer<Renderable>) entityRenderers.get(type);
    if (renderer != null) {
      renderer.register(object);
    } else {
      Renderer<Entity> defaultRenderer = (Renderer<Entity>) entityRenderers.get(Entity.class);
      defaultRenderer.register((Entity) object);
    }
  }

  public <T extends UserInterface> void register(T userInterface) {
    interfaceRenderer.register(userInterface);
  }

  public void unregister(Renderable object, Class<? extends Renderable> type) {
    Renderer<Renderable> renderer = (Renderer<Renderable>) entityRenderers.get(type);
    if (renderer != null) {
      renderer.unregister(object);
    } else {
      Renderer<Entity> defaultRenderer = (Renderer<Entity>) entityRenderers.get(Entity.class);
      defaultRenderer.unregister((Entity) object);
    }
  }

  public <T extends UserInterface> void unregister(T userInterface) {
    interfaceRenderer.unregister(userInterface);
  }

  public Collection<Renderer<? extends Renderable>> getRenderers() {
    return rendererList;
  }

  public <T extends Entity> Renderer<? extends Renderable> getRenderer(Class<T> type) {
    Renderer<T> value = (Renderer<T>) entityRenderers.get(type);
    if (value == null) {
      return entityRenderers.get(Entity.class);
    }
    return value;
  }
}
