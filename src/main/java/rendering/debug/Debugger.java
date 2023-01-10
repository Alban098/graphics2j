/*
 * Copyright (c) 2022-2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering.debug;

import imgui.ImGui;
import java.util.*;
import rendering.Engine;
import rendering.debug.component.ComponentDebugInterfaceProvider;
import rendering.debug.component.DefaultComponentDebugInterface;
import rendering.debug.component.RenderableComponentDebugInterface;
import rendering.debug.component.TransformComponentDebugInterface;
import rendering.debug.entity.EntityDebugInterface;
import rendering.debug.entity.EntityDebugInterfaceProvider;
import rendering.debug.tab.*;
import rendering.scene.entities.Entity;

/**
 * Represents the Debugging interface of the {@link Engine}, each {@link DebugTab} must be
 * registered to be displayed in the {@link Debugger}
 */
public final class Debugger extends ImGuiLayer {

  /** A Map of all registered {@link DebugTab} */
  private final Map<String, DebugTab> tabs = new HashMap<>();
  /** A List of all tabs that need to be notified when the selected entity has changed */
  private final List<EntityContainer> subscribedEntityContainers = new ArrayList<>();

  /**
   * Create a new Debugger for a specified {@link Engine}
   *
   * @param engine the engine to link
   */
  public Debugger(Engine engine) {
    super(engine);
    EntityDebugInterfaceProvider.setDefault(new EntityDebugInterface());
    ComponentDebugInterfaceProvider.setDefault(new DefaultComponentDebugInterface());
    ComponentDebugInterfaceProvider.register(new TransformComponentDebugInterface());
    ComponentDebugInterfaceProvider.register(new RenderableComponentDebugInterface());

    registerTab(new TimingTab(this));
    registerTab(new SceneTab(this));
    registerTab(new RenderersTab(this));
  }

  /** Renders all the tabs and interfaces */
  @Override
  public void render() {
    ImGui.begin("Debug");
    if (ImGui.beginTabBar("tab")) {
      tabs.values().forEach(DebugTab::render);
      ImGui.endTabBar();
    }
    ImGui.end();
  }

  /**
   * Registers a new {@link DebugTab} to the Debugger
   *
   * @param tab the {@link DebugTab} to register
   */
  public void registerTab(DebugTab tab) {
    if (tab instanceof EntityContainer) {
      subscribedEntityContainers.add((EntityContainer) tab);
    }
    tabs.put(tab.getName(), tab);
  }

  /**
   * Changes the selected {@link Entity} and notify subscribed {@link EntityContainer}s
   *
   * @param entity the new selected {@link Entity}
   */
  public void setSelectedEntity(Entity entity) {
    subscribedEntityContainers.forEach((subscriber) -> subscriber.setSelectedEntity(entity));
  }

  /**
   * Returns the {@link Engine} currently linked to this Debugger
   *
   * @return the {@link Engine} currently linked to this Debugger
   */
  public Engine getEngine() {
    return engine;
  }
}
