/*
 * Copyright (c) 2022, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering;

import static org.lwjgl.opengl.GL11.*;

import java.nio.ByteBuffer;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Represents a Texture that can be applied to a Quad */
public class Texture {

  private static final Logger LOGGER = LoggerFactory.getLogger(Texture.class);

  private final int id;
  private final int width;
  private final int height;

  /**
   * Create a new empty Texture from attributes
   *
   * @param id the id of the texture provided by OpenGL
   * @param width the Texture width in pixels
   * @param height the Texture height in pixels
   */
  public Texture(int id, int width, int height) {
    this.id = id;
    this.width = width;
    this.height = height;
    LOGGER.debug("Created texture with id {} ({}x{})", id, width, height);
  }

  /**
   * Create a new empty Texture
   *
   * @param width the Texture width in pixels
   * @param height the Texture height in pixels
   */
  public Texture(int width, int height) {
    this.width = width;
    this.height = height;
    // Generate the texture
    id = glGenTextures();
    bind();
    // Set the filtering mode
    glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
    glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
    GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
    GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
    // Load the buffer in VRAM
    glTexImage2D(
        GL_TEXTURE_2D,
        0,
        GL_RGBA,
        width,
        height,
        0,
        GL_RGBA,
        GL_UNSIGNED_BYTE,
        BufferUtils.createByteBuffer(width * height * 4));
    LOGGER.debug("Created texture with id {} ({}x{})", id, width, height);
  }

  /** Bind the texture for rendering */
  public void bind() {
    glBindTexture(GL_TEXTURE_2D, id);
    LOGGER.trace("Bound texture {}", id);
  }

  /**
   * Return the Texture id
   *
   * @return the Texture id
   */
  public int getId() {
    return id;
  }

  public int getWidth() {
    return width;
  }

  public int getHeight() {
    return height;
  }

  /**
   * Load a byte buffer in the texture
   *
   * @param buf the buffer to load
   */
  public void load(ByteBuffer buf) {
    bind();
    glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, buf);
  }

  /** Cleanup the Texture */
  public void cleanup() {
    glDeleteTextures(id);
    LOGGER.trace("texture {} cleaned up", id);
  }

  /** Unbind the texture after use */
  public void unbind() {
    glBindTexture(GL_TEXTURE_2D, 0);
  }
}
