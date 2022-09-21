/*
 * Copyright (c) 2022, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;

import java.util.HashMap;
import java.util.Map;

/** Represent an Object that has a texture and is represented by a Quad */
public class TexturedObject {

  private final Quad quad;
  private final Texture texture;
  private final Map<Integer, Texture> extra_samplers;

  /**
   * Create an Object from a Quad and a base Texture
   *
   * @param quad
   * @param texture
   */
  public TexturedObject(Quad quad, Texture texture) {
    this.quad = quad;
    this.texture = texture;
    extra_samplers = new HashMap<>();
  }

  /** Initialize the renderer to render the Object by binding Textures and Quad */
  private void initRender() {
    if (texture != null) {
      // Activate first texture bank
      glActiveTexture(GL_TEXTURE0);
      // Bind the texture
      texture.bind();
    }

    for (Map.Entry<Integer, Texture> entry : extra_samplers.entrySet()) {
      glActiveTexture(entry.getKey());
      entry.getValue().bind();
    }

    // Bind the mesh
    quad.initRender();
  }

  /** End the renderer for this Mesh by unbinding VAO */
  private void endRender() {
    glBindTexture(GL_TEXTURE_2D, 0);
    quad.endRender();
  }

  /** Render the object */
  public void render() {
    initRender();
    quad.render();
    endRender();
  }

  /** Clean up the Mesh, Textures and VAO/VBOs */
  public void cleanUp() {
    // Delete the texture
    if (texture != null) {
      texture.cleanup();
    }

    extra_samplers.forEach((unit, tex) -> tex.cleanup());
    quad.cleanUp();
  }

  /**
   * Add a new Texture to the model along with its texture location (location from GLSL shaders)
   *
   * @param textureUnit the Texture location (from GLSL shaders)
   * @param texture the Texture to add
   */
  public void addTexture(int textureUnit, Texture texture) {
    extra_samplers.put(textureUnit, texture);
  }

  /** Clear the Texture map */
  public void clearSamplers() {
    extra_samplers.clear();
  }
}
