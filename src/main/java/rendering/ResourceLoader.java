/*
 * Copyright (c) 2022, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;
import static org.lwjgl.stb.STBImage.*;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import org.lwjgl.system.MemoryStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Utility class in charge of loading external resources */
public class ResourceLoader {

  private static final Logger LOGGER = LoggerFactory.getLogger(ResourceLoader.class);

  /**
   * Read a file into a String
   *
   * @param filePath the path of the file to read
   * @return the content of the file, empty if an error occurs
   */
  public static String loadFile(String filePath) {
    StringBuilder read = new StringBuilder();
    try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
      reader.lines().forEach(line -> read.append(line).append("\n"));
    } catch (IOException e) {
      LOGGER.error("Unable to load file {}", filePath);
    }
    return read.toString();
  }

  /**
   * Load a texture from a file
   *
   * @param fileName the Path to the texture file
   * @return a Texture retrieved from an image file
   */
  public static Texture loadTexture(String fileName) {
    int width;
    int height;
    ByteBuffer buf;
    try (MemoryStack stack = MemoryStack.stackPush()) {
      IntBuffer w = stack.mallocInt(1);
      IntBuffer h = stack.mallocInt(1);
      IntBuffer channels = stack.mallocInt(1);

      buf = stbi_load(fileName, w, h, channels, 4);
      if (buf == null) {
        LOGGER.error("Image file [" + fileName + "] not loaded: " + stbi_failure_reason());
        return null;
      }

      width = w.get();
      height = h.get();
    }
    int size = buf.limit() * 4;
    // Create a new OpenGL texture
    int textureId = glGenTextures();
    // Bind the texture
    glBindTexture(GL_TEXTURE_2D, textureId);

    // Tell OpenGL how to unpack the RGBA bytes. Each component is 1 byte size
    glPixelStorei(GL_UNPACK_ALIGNMENT, 1);

    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

    // Upload the texture data
    glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, buf);
    // Generate Mip Map
    glGenerateMipmap(GL_TEXTURE_2D);

    // Free used memory
    stbi_image_free(buf);

    return new Texture(textureId, width, height, size);
  }
}
