/*
 * Copyright (c) 2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package org.alban098.graphics2j.debug;

import imgui.ImGui;
import imgui.ImVec2;
import imgui.extension.implot.ImPlot;
import imgui.extension.implot.flag.ImPlotFlags;
import imgui.flag.ImGuiStyleVar;
import java.util.Iterator;
import java.util.Map;
import org.alban098.graphics2j.common.Renderer;
import org.alban098.graphics2j.common.shaders.ShaderAttribute;
import org.alban098.graphics2j.common.shaders.ShaderProgram;
import org.alban098.graphics2j.common.shaders.data.Texture;
import org.alban098.graphics2j.common.shaders.data.VertexArrayObject;
import org.alban098.graphics2j.common.shaders.data.uniform.Uniform;
import org.alban098.graphics2j.common.shaders.data.vbo.VertexBufferObject;
import org.alban098.graphics2j.entities.EntityRenderingManager;
import org.alban098.graphics2j.interfaces.InterfaceRenderingManager;

public class RendererInterface extends DebugInterface {

  private Renderer selectedRenderer;
  private final EntityRenderingManager entityRenderingManager;
  private final InterfaceRenderingManager interfaceRenderingManager;

  private Double[][] shaderTimes;

  public RendererInterface(
      EntityRenderingManager entityRenderingManager,
      InterfaceRenderingManager interfaceRenderingManager) {
    super("Renderers");
    this.entityRenderingManager = entityRenderingManager;
    this.interfaceRenderingManager = interfaceRenderingManager;
  }

  @Override
  public void render() {
    ImGui.setWindowSize(1140, 640);
    ImGui.pushStyleVar(ImGuiStyleVar.ChildRounding, 5.0f);
    // Top half of the window
    ImGui.beginChild("top half", 1200, 400);
    // Row 0
    ImGui.beginChild("list/infos", 200, 400);
    displayRendererSelectionList();
    displayRendererInfoSection();
    ImGui.endChild();
    // Row 1
    ImGui.sameLine();
    displayTexturesSection();
    // Row 2
    ImGui.sameLine();
    displayShadersSection();
    // Row 3
    ImGui.sameLine();
    displayVaoSection();
    ImGui.endChild();

    // Bottom half of the window
    ImGui.beginChild("bottom half", 1200, 200);
    // Row 0
    displayRenderersPieChart();

    // Row 1
    ImGui.sameLine();
    displayShadersPieChart();

    // Row 2
    ImGui.sameLine();
    displayShadersTimePlot();

    ImGui.endChild();
    ImGui.popStyleVar();
  }

  private void displayRenderersPieChart() {
    ImGui.beginChild("renderers pie chart", 303, 200, true);
    ImGui.endChild();
  }

  private void displayShadersTimePlot() {
    ImGui.beginChild("shaders time plot", 503, 200, true);
    if (selectedRenderer != null) {
      if (shaderTimes[1].length - 1 >= 0) {
        for (int i = 1; i < shaderTimes.length; i++) {
          System.arraycopy(shaderTimes[i], 1, shaderTimes[i], 0, shaderTimes[i].length - 1);
        }
      }

      ImPlot.setNextPlotLimits(0, 128, 0, 1, 1);
      if (ImPlot.beginPlot(
          "Shader Rendering Times",
          "Frames",
          "Time in ms",
          new ImVec2(466, 170),
          ImPlotFlags.CanvasOnly,
          0,
          0)) {
        int i = 1;
        for (Map.Entry<ShaderProgram, Double> entry :
            selectedRenderer.getShaderTimes().entrySet()) {
          shaderTimes[i][127] = entry.getValue() * 1_000;
          ImPlot.plotLine(entry.getKey().toString(), shaderTimes[0], shaderTimes[i]);
          i++;
        }
        ImPlot.endPlot();
      }
    }
    ImGui.endChild();
  }

  private void displayShadersPieChart() {
    ImGui.beginChild("shaders pie chart", 303, 200, true);
    ImGui.endChild();
  }

  private void displayTexturesSection() {
    ImGui.beginChild("textures", 300, 400, true);
    if (selectedRenderer != null) {
      for (Texture texture : selectedRenderer.getTextures()) {
        if (texture != null) {
          drawTextureInfo(texture);
        }
      }
      if (selectedRenderer.getTextures().isEmpty()) {
        ImGui.textColored(255, 0, 255, 255, "No texture");
      }
    } else {
      ImGui.textColored(255, 0, 255, 255, "No renderer selected");
    }
    ImGui.endChild();
  }

  private void displayShadersSection() {
    ImGui.beginChild("shaders", 300, 400, true);
    if (selectedRenderer != null) {
      for (ShaderProgram shader : selectedRenderer.getShaders()) {
        ImGui.beginChild("shader##" + shader.getProgramId(), 272, 350, true);
        ImGui.textColored(255, 0, 0, 255, "General Info");
        DebugUtils.drawAttrib("Shader program ID", shader.getProgramId(), 30, 170);
        DebugUtils.drawAttrib(
            "Vertex   (id: " + shader.getVertexShader() + ")", shader.getVertexFile(), 30, 170);
        DebugUtils.drawAttrib(
            "Geometry (id: " + shader.getGeometryShader() + ")", shader.getGeometryFile(), 30, 170);
        DebugUtils.drawAttrib(
            "Fragment (id: " + shader.getFragmentShader() + ")", shader.getFragmentFile(), 30, 170);
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
        ImGui.endChild();
      }
    } else {
      ImGui.textColored(255, 0, 255, 255, "No renderer selected");
    }
    ImGui.endChild();
  }

  private void displayVaoSection() {
    ImGui.beginChild("vao", 300, 400, true);
    if (selectedRenderer != null) {
      VertexArrayObject vao = selectedRenderer.getVao();
      ImGui.textColored(255, 0, 0, 255, "VAO Definition");
      DebugUtils.drawAttrib("Id", vao.getId(), 20, 105);
      DebugUtils.drawAttrib("Capacity", vao.getMaxQuadCapacity() + " quads", 20, 105);
      ImGui.separator();
      ImGui.textColored(255, 0, 0, 255, "Shader Storage Buffer Object");
      if (vao.getSsbo() != null) {
        DebugUtils.drawAttrib("Id", vao.getSsbo().getId(), 20, 105);
        DebugUtils.drawAttrib("Location", vao.getSsbo().getLocation(), 20, 105);
        DebugUtils.drawAttrib("Size", DebugUtils.formatSize(vao.getSsbo().getSize()), 20, 105);
      } else {
        ImGui.newLine();
        ImGui.sameLine(20);
        ImGui.textColored(255, 0, 255, 255, "No linked SSBO");
      }
      ImGui.textColored(255, 0, 0, 255, "Vertex Buffer Objects");
      ImGui.beginChild("vbos##" + vao.getId(), 272, 240);
      ImGui.separator();
      for (Map.Entry<ShaderAttribute, VertexBufferObject<?>> entry : vao.getVbos().entrySet()) {
        ShaderAttribute attribute = entry.getKey();
        VertexBufferObject<?> vbo = entry.getValue();
        ImGui.beginChild(
            "vbo##" + attribute.getName() + "_" + vbo.getId() + "_" + vao.getId(), 260, 105);
        ImGui.textColored(0, 255, 0, 255, attribute.getName());
        DebugUtils.drawAttrib("Id", vbo.getId(), 20, 105);
        DebugUtils.drawAttrib("Location", vbo.getLocation(), 20, 105);
        DebugUtils.drawAttrib("Dimension", vbo.getDataDim(), 20, 105);
        DebugUtils.drawAttrib("Size", DebugUtils.formatSize((int) vbo.getSize()), 20, 105);
        DebugUtils.drawAttrib("Type", vbo.getType().getSimpleName(), 20, 105);
        ImGui.endChild();
        ImGui.separator();
      }
      ImGui.endChild();
    } else {
      ImGui.textColored(255, 0, 255, 255, "No renderer selected");
    }
    ImGui.endChild();
  }

  private void displayRendererSelectionList() {
    Iterator<Renderer> rendererIterator =
        new RendererIterator(
            entityRenderingManager.getRenderers().iterator(),
            interfaceRenderingManager.getRenderers().iterator());
    if (ImGui.beginListBox("##types", 200, 198)) {
      while (rendererIterator.hasNext()) {
        Renderer renderer = rendererIterator.next();
        if (ImGui.selectable(
            renderer.getClass().getSimpleName(), (renderer.equals(selectedRenderer)))) {
          selectRenderer(renderer);
        }
      }
      ImGui.endListBox();
    }
  }

  private void selectRenderer(Renderer renderer) {
    selectedRenderer = renderer;
    shaderTimes = new Double[renderer.getShaders().size() + 1][128];
    for (int i = 0; i < shaderTimes[0].length; i++) {
      shaderTimes[0][i] = (double) i;
      for (int j = 1; j <= renderer.getShaders().size(); j++) {
        shaderTimes[j][i] = 0d;
      }
    }
  }

  private void displayRendererInfoSection() {
    ImGui.beginChild("infos", 200, 197, true);
    if (selectedRenderer != null) {
      DebugUtils.drawAttrib2(
          "Type of Renderer", selectedRenderer.getClass().getSimpleName(), 0, 20);
      DebugUtils.drawAttrib2("Registered Objects", selectedRenderer.getNbObjects(), 0, 20);
      DebugUtils.drawAttrib2("Registered Textures", selectedRenderer.getTextures().size(), 0, 20);
      DebugUtils.drawAttrib2("Draw Calls / frame", selectedRenderer.getDrawCalls(), 0, 20);
    } else {
      DebugUtils.drawAttrib2("Type of Renderer", null, 0, 20);
      DebugUtils.drawAttrib2("Registered Objects", 0, 0, 20);
      DebugUtils.drawAttrib2("Registered Textures", 0, 0, 20);
      DebugUtils.drawAttrib2("Draw Calls / frame", 0, 0, 20);
    }
    ImGui.endChild();
  }

  /**
   * Draws a {@link Texture} and its info into a Debugging Layer
   *
   * @param texture the {@link Texture}
   */
  public static void drawTextureInfo(Texture texture) {
    ImGui.beginChild(
        "textureInfo##" + texture.getId(), 272, 123 + 255 / texture.getAspectRatio(), true);
    DebugUtils.drawAttrib("Id", texture.getId(), 10, 65);
    DebugUtils.drawAttrib("Size", DebugUtils.formatSize(texture.getSize()), 10, 65);
    DebugUtils.drawAttrib("Type", texture.getTypeDescriptor(), 10, 65);
    DebugUtils.drawAttrib(
        "Origin", texture.isFromFile() ? "Image file" : "Framebuffer rendering target", 10, 65);
    DebugUtils.drawAttrib("Width", texture.getWidth() + " px", 10, 65);
    DebugUtils.drawAttrib("Height", texture.getHeight() + " px", 10, 65);
    ImGui.separator();
    if (!texture.isFromFile()) {
      ImGui.image(texture.getId(), 255, 255 / texture.getAspectRatio(), 0, 1, 1, 0);
    } else {
      ImGui.image(texture.getId(), 255, 255 / texture.getAspectRatio());
    }
    ImGui.endChild();
  }

  private static final class RendererIterator implements Iterator<Renderer> {

    private final Iterator<Renderer> entityRenderers;
    private final Iterator<Renderer> interfaceRenderers;

    public RendererIterator(
        Iterator<Renderer> entityRenderers, Iterator<Renderer> interfaceRenderers) {
      this.entityRenderers = entityRenderers;
      this.interfaceRenderers = interfaceRenderers;
    }

    @Override
    public boolean hasNext() {
      return entityRenderers.hasNext() || interfaceRenderers.hasNext();
    }

    @Override
    public Renderer next() {
      if (entityRenderers.hasNext()) {
        return entityRenderers.next();
      }
      return interfaceRenderers.next();
    }
  }
}
