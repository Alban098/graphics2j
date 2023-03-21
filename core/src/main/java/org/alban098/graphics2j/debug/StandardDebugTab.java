/*
 * Copyright (c) 2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package org.alban098.graphics2j.debug;

import imgui.ImGui;
import imgui.ImVec2;
import imgui.extension.implot.flag.ImPlotFlags;
import imgui.flag.ImGuiStyleVar;
import java.util.*;
import org.alban098.graphics2j.common.Renderer;
import org.alban098.graphics2j.common.Window;
import org.alban098.graphics2j.common.shaders.ShaderAttribute;
import org.alban098.graphics2j.common.shaders.ShaderProgram;
import org.alban098.graphics2j.common.shaders.data.Texture;
import org.alban098.graphics2j.common.shaders.data.VertexArrayObject;
import org.alban098.graphics2j.common.shaders.data.uniform.Uniform;
import org.alban098.graphics2j.common.shaders.data.vbo.VertexBufferObject;
import org.alban098.graphics2j.entities.EntityRenderingManager;
import org.alban098.graphics2j.interfaces.InterfaceRenderingManager;

/**
 * The standard {@link DebugTab} displaying timing, {@link Renderer}s, {@link ShaderProgram}, {@link
 * VertexArrayObject}, {@link VertexBufferObject} and more
 */
public class StandardDebugTab extends DebugTab {

  /** Delay between 2 timing delay updates */
  private static final int LABEL_RENEW_TIME = 100;
  /** The {@link Window} the Tab is associated to */
  private final Window window;
  /** A buffer for the frametime label */
  private double frametime = 0;
  /** A buffer for the lastframe label */
  private double lastFrame = 0;
  /** A buffer for the fps label */
  private int fps = 0;
  /** The time at which labels must be refreshed in ms */
  private long refreshLabelAt = 0;

  /** The currently selected {@link Renderer} */
  private Renderer selectedRenderer;
  /** A Collection of all available {@link Renderer} */
  private final Collection<Renderer> renderers = new ArrayList<>();
  /** The Plot of time passed in each {@link ShaderProgram} */
  private final TimePlot<ShaderProgram> shaderTimePlot;
  /** The Plot for frame time */
  private final TimePlot<String> frameTimePlot;

  /**
   * Creates a new {@link StandardDebugTab}
   *
   * @param window the {@link Window} to associate the Tab to
   * @param entityRenderingManager the {@link EntityRenderingManager} to associate the Tab to
   * @param interfaceRenderingManager the {@link InterfaceRenderingManager} to associate the Tab to
   */
  public StandardDebugTab(
      Window window,
      EntityRenderingManager entityRenderingManager,
      InterfaceRenderingManager interfaceRenderingManager) {
    super("Internal Debugger");
    this.window = window;
    renderers.addAll(entityRenderingManager.getRenderers());
    renderers.addAll(interfaceRenderingManager.getRenderers());
    shaderTimePlot =
        new TimePlot<>("Shader Rendering times", 128, new ImVec2(485, 183), Collections::emptyMap);
    frameTimePlot =
        new TimePlot<>(
            "Frametime plot",
            128,
            new ImVec2(438, 183),
            () -> Map.of("frameTime", window.getFrametime()));
  }

  /**
   * The main rendering method, {@link ImGui#beginTabItem(String)} & {@link ImGui#endTabItem()}
   * calls are already handled, only render the content of the tab inside this method
   */
  @Override
  public void render() {
    ImGui.setWindowSize(1140, 663);
    ImGui.pushStyleVar(ImGuiStyleVar.ChildRounding, 5.0f);
    shaderTimePlot.update();
    frameTimePlot.update();
    // Top half of the window
    if (ImGui.beginChild("top half##" + uuid, 1200, 400)) {
      // Row 0
      if (ImGui.beginChild("list/infos##" + uuid, 200, 400)) {
        displayRendererSelectionList();
        displayRendererInfoSection();
      }
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
    }
    ImGui.endChild();

    // Bottom half of the window
    if (ImGui.beginChild("bottom half##" + uuid, 1200, 200)) {
      // Row 0
      displayTimingInfoSection();

      // Row 1
      ImGui.sameLine();
      displayShadersTimePlot();
    }
    ImGui.endChild();
    ImGui.popStyleVar();
  }

  /** Displays the Section with all Timing info such as frame time, FPS, and frametime plot */
  private void displayTimingInfoSection() {
    if (ImGui.beginChild("Timing##" + uuid, 613, 200, true)) {
      if (System.currentTimeMillis() >= refreshLabelAt) {
        fps =
            window.getTimeSinceLastFrame() == 0 ? 0 : (int) (1.0 / window.getTimeSinceLastFrame());
        frametime = (int) (window.getFrametime() * 10000) / 10.0;
        lastFrame = (int) (window.getTimeSinceLastFrame() * 10000) / 10.0;
        refreshLabelAt = System.currentTimeMillis() + LABEL_RENEW_TIME;
      }
      if (ImGui.beginChild("timing##" + uuid, 150, 170)) {
        ImGui.textColored(255, 0, 0, 255, "Performance");
        DebugUtils.drawAttrib("FPS", fps, 10, 100);

        ImGui.separator();
        ImGui.textColored(255, 0, 0, 255, "Frame");
        DebugUtils.drawAttrib("Computation ", frametime + " ms", 10, 100);
        DebugUtils.drawAttrib("Duration", lastFrame + " ms", 10, 100);
      }
      ImGui.endChild();
      ImGui.sameLine();
      frameTimePlot.render(0, 60, ImPlotFlags.NoLegend);
    }
    ImGui.endChild();
  }

  /**
   * Displays the Plot of time passed in each {@link ShaderProgram} of the selected {@link Renderer}
   */
  private void displayShadersTimePlot() {
    if (ImGui.beginChild("shaders time plot##" + uuid, 503, 200, true)) {
      shaderTimePlot.render(0, 1, 0);
    }
    ImGui.endChild();
  }

  /**
   * Displays the section in which all {@link Texture}s registered to the selected {@link Renderer}
   */
  private void displayTexturesSection() {
    if (ImGui.beginChild("textures", 300, 400, true)) {
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
    }
    ImGui.endChild();
  }

  /** Displays the section with all {@link ShaderProgram} used by the selected {@link Renderer} */
  private void displayShadersSection() {
    if (ImGui.beginChild("shaders", 300, 400, true)) {
      if (selectedRenderer != null) {
        for (ShaderProgram shader : selectedRenderer.getShaders()) {
          if (ImGui.beginChild("shader##" + shader.getProgramId(), 272, 350, true)) {
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
          }
          ImGui.endChild();
        }
      } else {
        ImGui.textColored(255, 0, 255, 255, "No renderer selected");
      }
    }
    ImGui.endChild();
  }

  /**
   * Displays the section with all {@link VertexArrayObject} used by the selected {@link Renderer}
   */
  private void displayVaoSection() {
    if (ImGui.beginChild("vao##" + uuid, 300, 400, true)) {
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
        if (ImGui.beginChild("vbos##" + uuid + "_" + vao.getId(), 272, 240)) {
          ImGui.separator();
          for (Map.Entry<ShaderAttribute, VertexBufferObject<?>> entry : vao.getVbos().entrySet()) {
            ShaderAttribute attribute = entry.getKey();
            VertexBufferObject<?> vbo = entry.getValue();
            if (ImGui.beginChild(
                "vbo##" + uuid + "_" + attribute.getName() + "_" + vbo.getId() + "_" + vao.getId(),
                260,
                105)) {
              ImGui.newLine();
              ImGui.sameLine(10);
              ImGui.textColored(0, 255, 0, 255, attribute.getName());
              DebugUtils.drawAttrib("Id", vbo.getId(), 20, 105);
              DebugUtils.drawAttrib("Location", vbo.getLocation(), 20, 105);
              DebugUtils.drawAttrib("Dimension", vbo.getDataDim(), 20, 105);
              DebugUtils.drawAttrib("Size", DebugUtils.formatSize((int) vbo.getSize()), 20, 105);
              DebugUtils.drawAttrib("Type", vbo.getType().getSimpleName(), 20, 105);
            }
            ImGui.endChild();
            ImGui.separator();
          }
        }
        ImGui.endChild();
      } else {
        ImGui.textColored(255, 0, 255, 255, "No renderer selected");
      }
    }
    ImGui.endChild();
  }

  /** Displays the section for selecting the current selected {@link Renderer} */
  private void displayRendererSelectionList() {
    if (ImGui.beginListBox("##types_" + uuid, 200, 198)) {
      for (Renderer renderer : renderers) {
        if (ImGui.selectable(
            renderer.getClass().getSimpleName(), (renderer.equals(selectedRenderer)))) {
          selectRenderer(renderer);
        }
      }
      ImGui.endListBox();
    }
  }

  /**
   * Selects a {@link Renderer} and propagate the event
   *
   * @param renderer the new selected {@link Renderer}
   */
  private void selectRenderer(Renderer renderer) {
    selectedRenderer = renderer;
    shaderTimePlot.setSampleSupplier(selectedRenderer::getShaderTimes);
  }

  /** Displays the section where info about the {@link Renderer} are shown */
  private void displayRendererInfoSection() {
    if (ImGui.beginChild("infos##" + uuid, 200, 197, true)) {
      if (selectedRenderer != null) {
        DebugUtils.drawAttrib2(
            "Type of Renderer", selectedRenderer.getClass().getSimpleName(), 0, 20);
        DebugUtils.drawAttrib2("Registered Objects", selectedRenderer.getNbObjects(), 0, 20);
        DebugUtils.drawAttrib2("Registered Textures", selectedRenderer.getTextures().size(), 0, 20);
        DebugUtils.drawAttrib2("Draw Calls / frame", selectedRenderer.getDrawCalls(), 0, 20);
        DebugUtils.drawAttrib2(
            "Shader binds / frame", selectedRenderer.getShaderBoundCount(), 0, 20);
      } else {
        DebugUtils.drawAttrib2("Type of Renderer", null, 0, 20);
        DebugUtils.drawAttrib2("Registered Objects", 0, 0, 20);
        DebugUtils.drawAttrib2("Registered Textures", 0, 0, 20);
        DebugUtils.drawAttrib2("Draw Calls / frame", 0, 0, 20);
        DebugUtils.drawAttrib2("Shader binds / frame", 0, 0, 20);
      }
    }
    ImGui.endChild();
  }

  /**
   * Draws a {@link Texture} and its information
   *
   * @param texture the {@link Texture}
   */
  public void drawTextureInfo(Texture texture) {
    if (ImGui.beginChild(
        "textureInfo##" + uuid + "_" + texture.getId(),
        272,
        123 + 255 / texture.getAspectRatio(),
        true)) {
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
    }
    ImGui.endChild();
  }
}
