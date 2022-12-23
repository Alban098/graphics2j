/*
 * Copyright (c) 2022, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering.debug.tab;

import imgui.ImGui;
import rendering.debug.Debugger;

public abstract class DebugTab {

  protected final String name;
  protected final Debugger parent;

  protected DebugTab(String name, Debugger parent) {
    this.name = name;
    this.parent = parent;
  }

  public final void render() {
    if (ImGui.beginTabItem(name)) {
      draw();
      ImGui.endTabItem();
    }
  }

  public abstract void draw();

  public String getName() {
    return name;
  }
}
