/*
 * Copyright (c) 2022, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering.debug;

import imgui.ImGui;
import imgui.type.ImBoolean;
import rendering.Engine;

public class ExampleLayer extends ImGuiLayer {

  private final ImBoolean imBoolWireframe = new ImBoolean(false);

  public ExampleLayer(Engine engine) {
    super(engine);
  }

  @Override
  public void render() {
    ImGui.begin("Settings");
    ImGui.setWindowSize(200, 200);
    ImGui.textColored(255, 255, 0, 255, engine.getFps() + " fps");
    ImGui.end();
  }
}
