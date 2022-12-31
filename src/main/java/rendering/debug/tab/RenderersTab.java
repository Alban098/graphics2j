/*
 * Copyright (c) 2022, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering.debug.tab;

import imgui.ImGui;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;
import rendering.Texture;
import rendering.data.VertexArrayObject;
import rendering.data.VertexBufferObject;
import rendering.debug.DebugUtils;
import rendering.debug.Debugger;
import rendering.renderers.Renderer;
import rendering.shaders.ShaderAttribute;
import rendering.shaders.ShaderProgram;
import rendering.shaders.uniform.Uniform;

public class RenderersTab extends DebugTab {

  private Renderer selectedRenderer;

  public RenderersTab(Debugger parent) {
    super("Renderers", parent);
  }

  @Override
  public void draw() {
    ImGui.setWindowSize(800, 462);
    Collection<Renderer> renderers = parent.getEngine().getRenderer().getRenderers();
    if (ImGui.beginListBox("##types", 170, Math.min(400, renderers.size() * 19f))) {
      for (Renderer renderer : renderers) {
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
              DebugUtils.drawTextureInfo(texture);
              ImGui.treePop();
            }
          }
        }
        ImGui.endTabItem();
      }
      if (ImGui.beginTabItem("VAO")) {
        for (VertexArrayObject vao : selectedRenderer.getVaos()) {
          if (ImGui.treeNode("VAO id(" + vao.getId() + ")")) {
            ImGui.beginChild("vao##" + vao.hashCode(), 200, 150);
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
                    Locale.ENGLISH,
                    "%.2f%%%%",
                    100.0 * vao.getSsbo().getFilled() / vao.getSsbo().getSize()),
                10,
                105);
            ImGui.endChild();
            ImGui.sameLine();
            ImGui.beginChild("vbos##" + vao.getId(), 220, 150);
            ImGui.textColored(255, 0, 0, 255, "VBOs");
            for (Map.Entry<ShaderAttribute, VertexBufferObject> entry : vao.getVbos().entrySet()) {
              ShaderAttribute attribute = entry.getKey();
              VertexBufferObject vbo = entry.getValue();
              ImGui.newLine();
              ImGui.sameLine(20);
              ImGui.beginChild(
                  "vbo##" + attribute.getName() + "_" + vbo.getId() + "_" + vao.getId(), 180, 105);
              ImGui.separator();
              ImGui.textColored(0, 0, 255, 255, attribute.getName());
              DebugUtils.drawAttrib("Id", vbo.getId(), 10, 105);
              DebugUtils.drawAttrib("Location", vbo.getLocation(), 10, 105);
              DebugUtils.drawAttrib("Dimension", vbo.getDataDim(), 10, 105);
              DebugUtils.drawAttrib("Size", DebugUtils.formatSize(vbo.getSize()), 10, 105);
              DebugUtils.drawAttrib(
                  "Filled",
                  String.format(
                      Locale.ENGLISH, "%.2f%%%%", 100.0 * vbo.getFilled() / vbo.getSize()),
                  10,
                  105);
              ImGui.endChild();
            }
            ImGui.endChild();
            ImGui.treePop();
            ImGui.separator();
          }
        }
        ImGui.endTabItem();
      }
      if (ImGui.beginTabItem("Shader")) {
        for (ShaderProgram shader : selectedRenderer.getShaders()) {
          if (ImGui.treeNode("Shader id(" + shader.getProgramId() + ")")) {
            ImGui.textColored(255, 0, 0, 255, "General Info");
            DebugUtils.drawAttrib("Shader program ID", shader.getProgramId(), 30, 170);
            DebugUtils.drawAttrib(
                "Vertex   (id: " + shader.getVertexShader() + ")", shader.getVertexFile(), 30, 170);
            DebugUtils.drawAttrib(
                "Geometry (id: " + shader.getGeometryShader() + ")",
                shader.getGeometryFile(),
                30,
                170);
            DebugUtils.drawAttrib(
                "Fragment (id: " + shader.getFragmentShader() + ")",
                shader.getFragmentFile(),
                30,
                170);
            ImGui.separator();
            ImGui.textColored(255, 0, 0, 255, "Attributes");
            for (ShaderAttribute attrib : shader.getAttributes()) {
              if (ImGui.treeNode(attrib.getName() + "##" + shader.getProgramId())) {
                DebugUtils.drawAttrib("location", attrib.getLocation(), 40, 110);
                DebugUtils.drawAttrib("size", attrib.getDimension() * 4 + " bytes", 40, 110);
                ImGui.treePop();
              }
            }
            ImGui.separator();
            ImGui.textColored(255, 0, 0, 255, "Uniforms");
            for (Uniform<?> uniform : shader.getUniforms().values()) {
              if (ImGui.treeNode(uniform.getName() + "##" + shader.getProgramId())) {
                DebugUtils.drawAttrib("type", uniform.getType(), 40, 110);
                DebugUtils.drawAttrib("location", uniform.getLocation(), 40, 110);
                DebugUtils.drawAttrib(
                    "size",
                    uniform.getDimension() + " byte" + (uniform.getDimension() > 1 ? "s" : ""),
                    40,
                    110);
                ImGui.treePop();
              }
            }
            ImGui.treePop();
            ImGui.separator();
          }
        }
        ImGui.endTabItem();
      }
      ImGui.endTabBar();
      ImGui.endChild();
    }
  }
}
