/*
 * Copyright (c) 2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package org.alban098.graphics2j.debug;

import imgui.ImGui;
import java.util.UUID;

/** An abstract representation of a Tab appearing in the {@link ImGuiOverlay} */
public abstract class ImGuiTab {

  /** A UUID used to uniquely identify the Tab */
  protected final String uuid;
  /** The title of the Tab */
  private final String title;

  /**
   * Create a new {@link ImGuiTab}
   *
   * @param title the title of the Tab
   */
  public ImGuiTab(String title) {
    this.title = title;
    uuid = UUID.randomUUID().toString();
  }

  /** The internal rendering routine, creating the ImGui TabItem */
  public final void renderInternal() {
    if (ImGui.beginTabItem(title + "##" + uuid)) {
      render();
      ImGui.endTabItem();
    }
  }

  /**
   * The main rendering method, {@link ImGui#beginTabItem(String)} & {@link ImGui#endTabItem()}
   * calls are already handled, only render the content of the tab inside this method
   */
  public abstract void render();
}
