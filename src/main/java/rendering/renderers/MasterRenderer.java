/*
 * Copyright (c) 2022-2023, @Author Alban098
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
import rendering.renderers.interfaces.FontRenderer;
import rendering.renderers.interfaces.InterfaceRenderer;

public class MasterRenderer {

  private static final Logger LOGGER = LoggerFactory.getLogger(MasterRenderer.class);

  private Map<Class<? extends Entity>, RegisterableRenderer<? extends Renderable>> entityRenderers;
  private InterfaceRenderer interfaceRenderer;
  private FontRenderer fontRenderer;
  private Set<Renderer> rendererList;
  private RenderingMode renderingMode = RenderingMode.FILL;

  public void init(Window window) {
    this.entityRenderers = new HashMap<>();
    this.rendererList = new HashSet<>();
    this.fontRenderer = new FontRenderer();
    this.interfaceRenderer = new InterfaceRenderer(window, fontRenderer);

    // default renderer
    mapEntityRenderer(Entity.class, new DefaultEntityRenderer());
    rendererList.add(interfaceRenderer);
    rendererList.add(fontRenderer);
  }

  public void setRenderingMode(RenderingMode mode) {
    renderingMode = mode;
  }

  public <T extends Entity> void mapEntityRenderer(
      Class<T> type, RegisterableRenderer<? extends Entity> renderer) {
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

    fontRenderer.prepare();
    interfaceRenderer.prepare();
    // Render game objects
    for (RegisterableRenderer<? extends Renderable> renderer : entityRenderers.values()) {
      renderer.render(window, logic);
    }

    // Render GUIs on top
    interfaceRenderer.render(window, logic);
  }

  public void cleanUp() {
    for (Renderer renderer : rendererList) {
      renderer.cleanUp();
    }
  }

  public void register(Renderable object, Class<? extends Renderable> type) {
    RegisterableRenderer<Renderable> renderer =
        (RegisterableRenderer<Renderable>) entityRenderers.get(type);
    if (renderer != null) {
      renderer.register(object);
    } else {
      RegisterableRenderer<Entity> defaultRenderer =
          (RegisterableRenderer<Entity>) entityRenderers.get(Entity.class);
      defaultRenderer.register((Entity) object);
    }
  }

  public <T extends UserInterface> void register(T userInterface) {
    interfaceRenderer.register(userInterface);
  }

  public void unregister(Renderable object, Class<? extends Renderable> type) {
    RegisterableRenderer<Renderable> renderer =
        (RegisterableRenderer<Renderable>) entityRenderers.get(type);
    if (renderer != null) {
      renderer.unregister(object);
    } else {
      RegisterableRenderer<Entity> defaultRenderer =
          (RegisterableRenderer<Entity>) entityRenderers.get(Entity.class);
      defaultRenderer.unregister((Entity) object);
    }
  }

  public <T extends UserInterface> void unregister(T userInterface) {
    interfaceRenderer.unregister(userInterface);
  }

  public Collection<Renderer> getRenderers() {
    return rendererList;
  }

  public <T extends Entity> RegisterableRenderer<? extends Renderable> getRenderer(Class<T> type) {
    RegisterableRenderer<T> value = (RegisterableRenderer<T>) entityRenderers.get(type);
    if (value == null) {
      return entityRenderers.get(Entity.class);
    }
    return value;
  }
}
