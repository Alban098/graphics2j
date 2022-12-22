/*
 * Copyright (c) 2022, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering.renderers;

import static org.lwjgl.opengl.GL11.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.lwjgl.opengl.GL11;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rendering.ILogic;
import rendering.Window;
import rendering.entities.Entity;
import simulation.renderer.EntityRenderer;

public class MasterRenderer {

  private static final Logger LOGGER = LoggerFactory.getLogger(MasterRenderer.class);

  private Map<Class<? extends Entity>, Renderer<? extends Entity>> renderers;
  private RenderingMode renderingMode = RenderingMode.FILL;

  public void init() {
    renderers = new HashMap<>();
    // default renderer
    mapRenderer(Entity.class, new EntityRenderer());
  }

  public void setRenderingMode(RenderingMode mode) {
    renderingMode = mode;
  }

  public <T extends Entity> void mapRenderer(Class<T> type, Renderer<? extends Entity> renderer) {
    renderers.put(type, renderer);
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

    for (Renderer<?> renderer : renderers.values()) {
      renderer.renderNative(window, logic.getCamera(), logic.getScene(), renderingMode);
    }
  }

  public void cleanUp() {
    for (Renderer<?> renderer : renderers.values()) {
      renderer.cleanUpNative();
    }
  }

  public <T extends Entity> void register(T entity, Class<T> type) {
    Renderer<T> renderer = (Renderer<T>) renderers.get(type);
    if (renderer != null) {
      renderer.register(entity);
    } else if (entity != null) {
      Renderer<Entity> defaultRenderer = (Renderer<Entity>) renderers.get(Entity.class);
      defaultRenderer.register(entity);
    }
  }

  public <T extends Entity> void unregister(T entity, Class<T> type) {
    Renderer<T> renderer = (Renderer<T>) renderers.get(type);
    if (renderer != null) {
      renderer.unregister(entity);
    } else if (entity != null) {
      Renderer<Entity> defaultRenderer = (Renderer<Entity>) renderers.get(Entity.class);
      defaultRenderer.unregister(entity);
    }
  }

  public Collection<Renderer<? extends Entity>> getRenderers() {
    return renderers.values();
  }

  public <T extends Entity> Renderer<? extends Entity> getRenderer(Class<T> type) {
    return renderers.getOrDefault(type, renderers.get(Entity.class));
  }
}
