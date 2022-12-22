/*
 * Copyright (c) 2022, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering.debug;

import imgui.ImGui;
import imgui.ImVec2;
import imgui.extension.implot.ImPlot;
import imgui.extension.implot.flag.ImPlotFlags;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import rendering.Engine;
import rendering.Texture;
import rendering.data.VAO;
import rendering.data.VBO;
import rendering.entities.Entity;
import rendering.renderers.Renderer;
import rendering.shaders.ShaderAttribute;

public class DebugLayer extends ImGuiLayer {

  private final Double[][] frametimes = new Double[2][256];
  private Class<? extends Entity> sceneSelectedType;
  private Entity sceneSelectedEntity;
  private Renderer<? extends Entity> selectedRenderer;

  public DebugLayer(Debugger debugger) {
    super(debugger);
    for (int i = 0; i < frametimes[0].length; i++) {
      frametimes[0][i] = (double) i;
      frametimes[1][i] = 0d;
    }
  }

  @Override
  public void render() {
    ImGui.begin("Debug");
    if (ImGui.beginTabBar("tab")) {
      drawTimingTab();
      drawSceneTab();
      drawRenderersTab();
      drawSelectedEntityTab();
      ImGui.endTabBar();
    }
    ImGui.end();
  }

  private void drawRenderersTab() {
    if (ImGui.beginTabItem("Renderers")) {
      ImGui.setWindowSize(680, 462);
      Collection<Renderer<? extends Entity>> renderers = engine.getRenderer().getRenderers();
      if (ImGui.beginListBox("##types", 170, Math.min(400, renderers.size() * 19f))) {
        for (Renderer<? extends Entity> renderer : renderers) {
          if (ImGui.selectable(
              renderer.getClass().getSimpleName(), (renderer.equals(selectedRenderer)))) {
            selectedRenderer = renderer;
          }
        }
        ImGui.endListBox();
      }
      ImGui.sameLine();
      if (selectedRenderer != null) {
        ImGui.beginChild("renderer");
        ImGui.beginTabBar("renderer_tab");
        if (ImGui.beginTabItem("General")) {
          DebugUtils.drawAttrib(
              "Type of Renderer", selectedRenderer.getClass().getSimpleName(), 10, 160);
          DebugUtils.drawAttrib("Registered Objects", selectedRenderer.getNbObjects(), 10, 160);
          DebugUtils.drawAttrib(
              "Registered Textures", selectedRenderer.getTextures().size(), 10, 160);
          DebugUtils.drawAttrib("Draw Calls / frame", selectedRenderer.getDrawCalls(), 10, 160);
          ImGui.endTabItem();
        }
        if (ImGui.beginTabItem("Registered Textures")) {
          for (Texture texture : selectedRenderer.getTextures()) {
            if (texture != null) {
              ImGui.separator();
              if (ImGui.treeNode("Texture " + texture.getId())) {
                ImGui.beginChild("textureInfo##" + texture.getId(), 160, 130);
                ImGui.separator();
                ImGui.textColored(255, 0, 0, 255, "Metadata");
                DebugUtils.drawAttrib("Id", texture.getId(), 10, 60);
                DebugUtils.drawAttrib("Size", DebugUtils.formatSize(texture.getSize()), 10, 60);
                DebugUtils.drawAttrib("Type", texture.getTypeDescriptor(), 10, 60);
                ImGui.separator();
                ImGui.textColored(255, 0, 0, 255, "Dimension");
                DebugUtils.drawAttrib("Width", texture.getWidth() + " px", 10, 70);
                DebugUtils.drawAttrib("Height", texture.getHeight() + " px", 10, 70);
                ImGui.endChild();
                ImGui.sameLine();
                ImGui.image(texture.getId(), texture.getAspectRatio() * 130, 130);
                ImGui.treePop();
              }
            }
          }
          ImGui.endTabItem();
        }
        if (ImGui.beginTabItem("VAO")) {
          VAO vao = selectedRenderer.getVao();
          ImGui.beginChild("vao", 200, 150);
          ImGui.textColored(255, 0, 0, 255, "General Info");
          DebugUtils.drawAttrib("Id", vao.getId(), 10, 105);
          DebugUtils.drawAttrib("Capacity", vao.getMaxQuadCapacity() + " quads", 10, 105);
          ImGui.separator();
          ImGui.textColored(255, 0, 0, 255, "SSBO");
          DebugUtils.drawAttrib("Id", vao.getSsbo().getId(), 10, 105);
          DebugUtils.drawAttrib("Location", vao.getSsbo().getLocation(), 10, 105);
          DebugUtils.drawAttrib("Size", DebugUtils.formatSize(vao.getSsbo().getSize()), 10, 105);
          DebugUtils.drawAttrib(
              "Filled",
              String.format(
                  "%.2f%%%%", 100.0 * vao.getSsbo().getFilled() / vao.getSsbo().getSize()),
              10,
              105);
          ImGui.endChild();
          ImGui.sameLine();
          ImGui.beginChild("vbos");
          ImGui.textColored(255, 0, 0, 255, "VBOs");
          for (Map.Entry<ShaderAttribute, VBO> entry : vao.getVbos().entrySet()) {
            ShaderAttribute attribute = entry.getKey();
            VBO vbo = entry.getValue();
            ImGui.newLine();
            ImGui.sameLine(20);
            ImGui.beginChild("vbo##" + attribute.getName() + "_" + vbo.getId(), 180, 105);
            ImGui.separator();
            ImGui.textColored(0, 0, 255, 255, attribute.getName());
            DebugUtils.drawAttrib("Id", vbo.getId(), 10, 105);
            DebugUtils.drawAttrib("Location", vbo.getLocation(), 10, 105);
            DebugUtils.drawAttrib("Dimension", vbo.getDataDim(), 10, 105);
            DebugUtils.drawAttrib("Size", DebugUtils.formatSize(vbo.getSize()), 10, 105);
            DebugUtils.drawAttrib(
                "Filled",
                String.format("%.2f%%%%", 100.0 * vbo.getFilled() / vbo.getSize()),
                10,
                105);
            ImGui.endChild();
          }
          ImGui.endChild();
          ImGui.endTabItem();
        }
        if (ImGui.beginTabItem("Shader")) {
          ImGui.text("TODO");
          ImGui.endTabItem();
        }
        ImGui.endTabBar();
        ImGui.endChild();
      }
      ImGui.endTabItem();
    }
  }

  private void drawSelectedEntityTab() {
    if (ImGui.beginTabItem("Selected Entity")) {
      ImGui.textColored(255, 255, 0, 255, "Nothing here yet");
      ImGui.endTabItem();
    }
  }

  private void drawTimingTab() {
    if (ImGui.beginTabItem("Timing")) {
      ImGui.setWindowSize(640, 228);
      ImGui.beginChild("timing", 150, 170);
      ImGui.separator();
      ImGui.textColored(255, 0, 0, 255, "Target");
      DebugUtils.drawAttrib("FPS", Engine.TARGET_FPS, 10, 90);
      DebugUtils.drawAttrib("Ticks/s", Engine.TARGET_TPS, 10, 90);

      ImGui.separator();
      ImGui.textColored(255, 0, 0, 255, "Actual");
      DebugUtils.drawAttrib("FPS", (int) (1.0 / engine.getFrameTime()), 10, 90);
      DebugUtils.drawAttrib("Ticks/s", (int) (engine.getNbUpdates()), 10, 90);

      ImGui.separator();
      ImGui.textColored(255, 0, 0, 255, "Times");
      DebugUtils.drawAttrib("Frame", (int) (engine.getFrameTime() * 10000) / 10f + " ms", 10, 90);
      DebugUtils.drawAttrib("Tick", (int) (10000 / engine.getNbUpdates()) / 10f + " ms", 10, 90);

      ImGui.endChild();
      ImGui.sameLine();

      if (frametimes[1].length - 1 >= 0) {
        System.arraycopy(frametimes[1], 1, frametimes[1], 0, frametimes[1].length - 1);
      }
      frametimes[1][255] = engine.getFrameTime() * 1000;

      ImPlot.setNextPlotLimits(0, 256, 0, 1.0 / Engine.TARGET_FPS * 8000, 1);
      if (ImPlot.beginPlot(
          "Frametime plot",
          "Frames",
          "Time in ms",
          new ImVec2(466, 170),
          ImPlotFlags.CanvasOnly,
          0,
          0)) {
        ImPlot.plotLine("time", frametimes[0], frametimes[1]);
        ImPlot.endPlot();
      }
      ImGui.endTabItem();
    }
  }

  private void drawSceneTab() {
    if (ImGui.beginTabItem("Scene")) {
      ImGui.setWindowSize(680, 462);
      Collection<Class<? extends Entity>> types = scene.getTypes();
      if (ImGui.beginListBox("##types", 170, Math.min(400, types.size() * 19f))) {
        for (Class<? extends Entity> type : types) {
          List<? extends Entity> entities = scene.getObjects(type);
          if (ImGui.selectable(
              type.getSimpleName() + " (" + entities.size() + ")",
              (type.equals(sceneSelectedType)))) {
            sceneSelectedType = type;
            if (sceneSelectedEntity != null && !sceneSelectedEntity.getClass().equals(type)) {
              sceneSelectedEntity = null;
            }
          }
        }
        ImGui.endListBox();
      }
      ImGui.sameLine();
      if (sceneSelectedType != null) {
        Collection<? extends Entity> entities = scene.getObjects(sceneSelectedType);
        ImGui.beginChild("##entitiesSummary", 120, Math.min(400, entities.size() * 19f));
        if (ImGui.beginListBox("##entities", 120, Math.min(400, entities.size() * 19f))) {
          for (Entity e : entities) {
            if (ImGui.selectable(e.getName(), e.equals(sceneSelectedEntity))) {
              sceneSelectedEntity = e;
            }
          }
          ImGui.endListBox();
        }
        ImGui.endChild();
        ImGui.sameLine();
        if (sceneSelectedEntity != null) {
          debugger.getDebugGUI(sceneSelectedType).render(this, sceneSelectedEntity);
        }
      }
      ImGui.endTabItem();
    }
  }

  public void setSelectedEntity(Entity entity) {
    this.sceneSelectedType = entity.getClass();
    this.sceneSelectedEntity = entity;
  }
}
