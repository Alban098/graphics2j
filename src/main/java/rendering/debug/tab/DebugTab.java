/*
 * Copyright (c) 2022-2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering.debug.tab;

import imgui.ImGui;
import rendering.debug.Debugger;

/** Represents an abstraction of a tab inside the Debugging view */
public abstract class DebugTab {

  /** The name of the tab */
  protected final String name;
  /** The {@link Debugger} this tab is present in */
  protected final Debugger parent;

  /**
   * Creates a new Tab
   *
   * @param name the name of the tab
   * @param parent the parent {@link Debugger}
   */
  protected DebugTab(String name, Debugger parent) {
    this.name = name;
    this.parent = parent;
  }

  /** Renders the Tab to the Debugger view */
  public final void render() {
    if (ImGui.beginTabItem(name)) {
      draw();
      ImGui.endTabItem();
    }
  }

  /** Draws the content of the Tab to the tabview */
  public abstract void draw();

  /**
   * Returns the name of the Tab
   *
   * @return the name of the Tab
   */
  public String getName() {
    return name;
  }
}
