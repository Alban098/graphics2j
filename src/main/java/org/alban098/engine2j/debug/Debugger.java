/*
 * Copyright (c) 2022-2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package org.alban098.engine2j.debug;

import imgui.ImGui;
import java.util.*;
import org.alban098.engine2j.core.Engine;
import org.alban098.engine2j.debug.component.ComponentDebugInterfaceProvider;
import org.alban098.engine2j.debug.component.DefaultComponentDebugInterface;
import org.alban098.engine2j.debug.component.RenderableComponentDebugInterface;
import org.alban098.engine2j.debug.component.TransformComponentDebugInterface;
import org.alban098.engine2j.debug.renderable.DefaultDebugInterface;
import org.alban098.engine2j.debug.renderable.RenderableDebugInterfaceProvider;
import org.alban098.engine2j.debug.tab.*;
import org.alban098.engine2j.objects.entities.Entity;

/**
 * Represents the Debugging interface of the {@link Engine}, each {@link DebugTab} must be
 * registered to be displayed in the {@link Debugger}
 */
public final class Debugger extends ImGuiLayer {

  /** A Map of all registered {@link DebugTab} */
  private final Map<String, DebugTab> tabs = new HashMap<>();
  /** A List of all tabs that need to be notified when the selected entity has changed */
  private final List<RenderableContainer> subscribedEntityContainers = new ArrayList<>();

  /**
   * Create a new Debugger for a specified {@link Engine}
   *
   * @param engine the engine to link
   */
  public Debugger(Engine engine) {
    super(engine);
    RenderableDebugInterfaceProvider.setDefault(new DefaultDebugInterface());
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
    if (tab instanceof RenderableContainer) {
      subscribedEntityContainers.add((RenderableContainer) tab);
    }
    tabs.put(tab.getName(), tab);
  }

  /**
   * Changes the selected {@link Entity} and notify subscribed {@link RenderableContainer}s
   *
   * @param entity the new selected {@link Entity}
   */
  public void setSelectedEntity(Entity entity) {
    subscribedEntityContainers.forEach((subscriber) -> subscriber.setSelectedRenderable(entity));
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
