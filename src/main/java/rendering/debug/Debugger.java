/*
 * Copyright (c) 2022, @Author Alban098
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
import rendering.debug.entity.DefaultEntityDebugInterface;
import rendering.debug.entity.EntityDebugInterfaceProvider;
import rendering.debug.tab.*;
import rendering.entities.Entity;

public class Debugger extends ImGuiLayer {

  private final Map<String, DebugTab> tabs = new HashMap<>();
  private final List<EntityContainer> subscribedEntityContainers = new ArrayList<>();

  public Debugger(Engine engine) {
    super(engine);
    EntityDebugInterfaceProvider.setDefault(new DefaultEntityDebugInterface());
    ComponentDebugInterfaceProvider.setDefault(new DefaultComponentDebugInterface());
    ComponentDebugInterfaceProvider.register(new TransformComponentDebugInterface());
    ComponentDebugInterfaceProvider.register(new RenderableComponentDebugInterface());

    SceneTab sceneTab = new SceneTab(this);
    tabs.put("Timing", new TimingTab(this));
    tabs.put("Scene", sceneTab);
    tabs.put("Renderers", new RenderersTab(this));

    subscribedEntityContainers.add(sceneTab);
  }

  @Override
  public void render() {
    ImGui.begin("Debug");
    if (ImGui.beginTabBar("tab")) {
      tabs.values().forEach(DebugTab::render);
      ImGui.endTabBar();
    }
    ImGui.end();
  }

  public void registerTab(DebugTab tab) {
    if (tab instanceof EntityContainer) {
      subscribedEntityContainers.add((EntityContainer) tab);
    }
    tabs.put(tab.getName(), tab);
  }

  public void setSelectedEntity(Entity entity) {
    subscribedEntityContainers.forEach((subscriber) -> subscriber.setSelectedEntity(entity));
  }

  public Engine getEngine() {
    return engine;
  }
}
