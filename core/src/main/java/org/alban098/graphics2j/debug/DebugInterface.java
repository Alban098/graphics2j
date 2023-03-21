/*
 * Copyright (c) 2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package org.alban098.graphics2j.debug;

import imgui.ImGui;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

/** An ImGui interface used to display debug information, can be extended with {@link DebugTab} */
public final class DebugInterface {

  /** A flag indicating if the Layer is visible or not */
  private boolean visible = true;
  /** The UUID used to uniquely identify the Interface */
  private final String uuid;
  /** The title of the Interface */
  private final String title;
  /** A Collection of all {@link DebugTab}s of the Interface */
  private final Collection<DebugTab> tabs;

  /**
   * Creates a new {@link DebugInterface}
   *
   * @param title the title of the Interface
   */
  public DebugInterface(String title) {
    this.title = title;
    this.uuid = UUID.randomUUID().toString();
    this.tabs = new ArrayList<>();
  }

  /** Renders the Interface to the screen */
  public void render() {
    if (ImGui.begin(title + "##" + uuid)) {
      if (ImGui.beginTabBar("tabBar##" + uuid)) {
        tabs.forEach(DebugTab::renderInternal);
        ImGui.endTabBar();
      }
    }
    ImGui.end();
  }

  /**
   * Adds a new {@link DebugTab} to the Interface
   *
   * @param tab the {@link DebugTab} to add
   */
  public void addTab(DebugTab tab) {
    tabs.add(tab);
  }

  /**
   * Return whether the layer is visible or not
   *
   * @return is the layer visible
   */
  public boolean isVisible() {
    return visible;
  }

  /**
   * Set the layer as visible or non-visible
   *
   * @param visible should the layer be visible or not
   */
  public void setVisible(boolean visible) {
    this.visible = visible;
  }

  /**
   * Returns the title of the Interface
   *
   * @return the title of the Interface
   */
  public String getTitle() {
    return title;
  }
}
