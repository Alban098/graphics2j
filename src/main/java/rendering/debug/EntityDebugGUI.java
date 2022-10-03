/*
 * Copyright (c) 2022, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering.debug;

import imgui.ImGui;
import org.joml.Math;
import org.joml.Vector3f;
import rendering.Texture;
import rendering.entities.Entity;
import rendering.renderers.Renderer;

public abstract class EntityDebugGUI {

  protected abstract void renderTabs(DebugLayer caller, Entity entity);

  protected abstract boolean showComponentTab();

  protected abstract boolean showChildrenTab();

  protected abstract boolean showTransformTab();

  protected abstract boolean showRenderingTab();

  public void render(DebugLayer caller, Entity entity) {

    ImGui.beginChild("entity");
    ImGui.beginTabBar("entity_tab");
    drawTransformTab(caller, entity);
    drawHierarchyTab(caller, entity);
    drawComponentTab(caller, entity);
    drawRenderingTab(caller, entity);
    this.renderTabs(caller, entity);
    ImGui.endTabBar();
    ImGui.endChild();
  }

  private void drawRenderingTab(DebugLayer caller, Entity entity) {
    Texture texture = entity.getRenderable().getTexture();
    Renderer<?> renderer = caller.engine.getRenderer(entity.getClass());
    if (showRenderingTab() && ImGui.beginTabItem("Rendering")) {
      if (texture != null) {
        ImGui.beginChild("textureInfo", 160, 130);
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
      }
      ImGui.beginChild("renderer", 300, 130);
      ImGui.separator();
      ImGui.textColored(255, 0, 0, 255, "Renderer");
      DebugUtils.drawAttrib("Type of Renderer", renderer.getClass().getSimpleName(), 10, 160);
      DebugUtils.drawAttrib("Registered Objects", renderer.getNbObjects(), 10, 160);
      DebugUtils.drawAttrib("Registered Textures", renderer.getTextures().size(), 10, 160);
      DebugUtils.drawAttrib("Draw Calls / frame", renderer.getDrawCalls(), 10, 160);
      ImGui.text("More info on \"Renderers\" tab ...");
      ImGui.separator();
      ImGui.endChild();
      ImGui.endTabItem();
    }
  }

  private void drawComponentTab(DebugLayer caller, Entity entity) {
    if (showComponentTab() && ImGui.beginTabItem("Components")) {
      ImGui.text("TODO");
      ImGui.endTabItem();
    }
  }

  private void drawHierarchyTab(DebugLayer caller, Entity entity) {
    if (showChildrenTab() && ImGui.beginTabItem("Hierarchy")) {
      if (entity.getParent() != null) {
        ImGui.separator();
        ImGui.textColored(255, 0, 0, 255, "Parent");
        ImGui.newLine();
        ImGui.sameLine(20);
        ImGui.beginChild("entity_parent", 250, 63);
        ImGui.separator();
        ImGui.textColored(0, 0, 255, 255, entity.getParent().getName());
        DebugUtils.drawAttrib("Type", entity.getParent().getClass().getSimpleName(), 20, 90);
        DebugUtils.drawAttrib("Children", entity.getParent().getChildren().size(), 20, 90);
        ImGui.endChild();
        if (ImGui.isItemClicked()) {
          caller.setSelectedEntity(entity.getParent());
        }
      }
      ImGui.separator();
      if (!entity.getChildren().isEmpty()) {
        ImGui.textColored(
            255, 0, 0, 255, String.format("Children (%d)", entity.getChildren().size()));
        for (Entity child : entity.getChildren()) {
          ImGui.newLine();
          ImGui.sameLine(20);
          ImGui.beginChild("entity_children_" + child.hashCode(), 280, 55);
          ImGui.separator();
          ImGui.textColored(0, 0, 255, 255, child.getName());
          DebugUtils.drawAttrib("Type", child.getClass().getSimpleName(), 20, 90);
          DebugUtils.drawAttrib("Children", child.getChildren().size(), 20, 90);
          ImGui.endChild();
          if (ImGui.isItemClicked()) {
            caller.setSelectedEntity(child);
          }
        }
      }
      ImGui.endTabItem();
    }
  }

  private void drawTransformTab(DebugLayer caller, Entity entity) {
    if (showTransformTab() && ImGui.beginTabItem("Transform")) {
      Vector3f position = entity.getTransform().getAbsolutePosition();
      ImGui.separator();
      ImGui.textColored(255, 0, 0, 255, "Absolute");
      ImGui.newLine();
      ImGui.sameLine(10);
      ImGui.textColored(255, 0, 255, 255, "Position");
      ImGui.sameLine(100);
      ImGui.textColored(255, 255, 0, 255, String.format("%.2f", position.x));
      ImGui.sameLine(160);
      ImGui.textColored(255, 255, 0, 255, String.format("%.2f", position.y));
      DebugUtils.drawAttrib(
          "Scale", String.format("%.2f", entity.getTransform().getAbsoluteScale()), 10, 100);
      DebugUtils.drawAttrib(
          "Rotation",
          String.format("%.2f", Math.toDegrees(entity.getTransform().getAbsoluteRotation())),
          10,
          100);
      ImGui.separator();
      ImGui.textColored(255, 0, 0, 255, "Relative");
      ImGui.newLine();
      ImGui.sameLine(10);
      ImGui.textColored(255, 0, 255, 255, "Position");
      ImGui.sameLine(100);
      ImGui.textColored(
          255, 255, 0, 255, String.format("%.2f", entity.getTransform().getDisplacement().x));
      ImGui.sameLine(160);
      ImGui.textColored(
          255, 255, 0, 255, String.format("%.2f", entity.getTransform().getDisplacement().y));
      DebugUtils.drawAttrib(
          "Scale", String.format("%.2f", entity.getTransform().getScale()), 10, 100);
      DebugUtils.drawAttrib(
          "Rotation",
          String.format("%.2f", Math.toDegrees(entity.getTransform().getRotation())),
          10,
          100);
      ImGui.endTabItem();
    }
  }
}
