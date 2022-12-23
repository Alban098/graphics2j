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

  private Map<Class<? extends Componentable>, AbstractRenderer<? extends Componentable>>
      entityRenderers;
  private Map<Class<? extends Componentable>, AbstractRenderer<? extends Componentable>>
      userInterfaceRenderers;
  private Set<AbstractRenderer<? extends Componentable>> rendererList;
  private RenderingMode renderingMode = RenderingMode.FILL;

  public void init() {
    entityRenderers = new HashMap<>();
    userInterfaceRenderers = new HashMap<>();
    rendererList = new HashSet<>();
    // default renderer
    mapRenderer(Entity.class, new DefaultEntityRenderer());
    mapRenderer(UserInterface.class, new DefaultInterfaceRenderer());
  }

  public void setRenderingMode(RenderingMode mode) {
    renderingMode = mode;
  }

  public <T extends Componentable> void mapRenderer(
      Class<T> type, AbstractRenderer<? extends Componentable> renderer) {
    if (type.isAssignableFrom(Entity.class)) {
      entityRenderers.put(type, renderer);
    } else if (type.isAssignableFrom(UserInterface.class)) {
      userInterfaceRenderers.put(type, renderer);
    }
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

    for (AbstractRenderer<? extends Componentable> renderer : entityRenderers.values()) {
      renderer.renderNative(window, logic.getCamera(), logic.getScene(), renderingMode);
    }
    for (AbstractRenderer<? extends Componentable> renderer : userInterfaceRenderers.values()) {
      renderer.renderNative(window, logic.getCamera(), logic.getScene(), renderingMode);
    }
  }

  public void cleanUp() {
    for (AbstractRenderer<? extends Componentable> renderer : entityRenderers.values()) {
      renderer.cleanUpNative();
    }
    for (AbstractRenderer<? extends Componentable> renderer : userInterfaceRenderers.values()) {
      renderer.cleanUpNative();
    }
  }

  public <T extends Componentable> void register(T object, Class<T> type) {
    AbstractRenderer<T> renderer = null;
    if (object instanceof Entity) {
      renderer = (AbstractRenderer<T>) entityRenderers.get(type);
    } else if (object instanceof UserInterface) {
      renderer = (AbstractRenderer<T>) userInterfaceRenderers.get(type);
    }
    if (renderer != null) {
      renderer.register(object);
    } else if (object instanceof Entity) {
      AbstractRenderer<Entity> defaultRenderer =
          (AbstractRenderer<Entity>) entityRenderers.get(Entity.class);
      defaultRenderer.register((Entity) object);
    }
  }

  public <T extends Componentable> void unregister(T object, Class<T> type) {
    AbstractRenderer<T> renderer = null;
    if (object instanceof Entity) {
      renderer = (AbstractRenderer<T>) entityRenderers.get(type);
    } else if (object instanceof UserInterface) {
      renderer = (AbstractRenderer<T>) userInterfaceRenderers.get(type);
    }
    if (renderer != null) {
      renderer.unregister(object);
    } else if (object instanceof Entity) {
      AbstractRenderer<Entity> defaultRenderer =
          (AbstractRenderer<Entity>) entityRenderers.get(Entity.class);
      defaultRenderer.unregister((Entity) object);
    }
  }

  public Collection<AbstractRenderer<? extends Componentable>> getRenderers() {
    return rendererList;
  }

  public <T extends Componentable> AbstractRenderer<? extends Componentable> getRenderer(
      Class<T> type) {
    AbstractRenderer<T> value = (AbstractRenderer<T>) entityRenderers.get(type);
    if (value == null) {
      if (type.isAssignableFrom(Entity.class)) {
        return entityRenderers.get(Entity.class);
      }
    }
    return value;
  }
}
