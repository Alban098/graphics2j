/*
 * Copyright (c) 2022-2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package org.alban098.graphics2j.common.utils;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;
import static org.lwjgl.stb.STBImage.*;

import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collection;
import javax.imageio.ImageIO;
import org.alban098.graphics2j.common.shaders.data.Texture;
import org.alban098.graphics2j.fonts.CharacterDescriptor;
import org.alban098.graphics2j.fonts.Font;
import org.joml.Vector2f;
import org.lwjgl.BufferUtils;
import org.lwjgl.system.MemoryStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Utility class in charge of loading external resources */
public final class ResourceLoader {

  /** Just a Logger to log events */
  private static final Logger LOGGER = LoggerFactory.getLogger(ResourceLoader.class);

  /** Empty private constructor to prevent instantiation */
  private ResourceLoader() {}

  /**
   * Reads a file into a String
   *
   * @param filePath the file to read
   * @return the content of the file, empty if an error occurs
   */
  public static String loadFile(File filePath) {
    StringBuilder read = new StringBuilder();
    try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
      reader.lines().forEach(line -> read.append(line).append("\n"));
    } catch (IOException e) {
      LOGGER.error("Unable to load file [{}]", filePath);
      return "";
    }
    LOGGER.info("File [{}] successfully loaded", filePath);
    return read.toString();
  }

  /**
   * Loads a texture from a file
   *
   * @param filePath the Path to the texture file
   * @return a Texture retrieved from an image file
   */
  public static Texture loadTexture(String filePath) {
    int width;
    int height;
    ByteBuffer buf;
    try (MemoryStack stack = MemoryStack.stackPush()) {
      IntBuffer w = stack.mallocInt(1);
      IntBuffer h = stack.mallocInt(1);
      IntBuffer channels = stack.mallocInt(1);

      buf = stbi_load(filePath, w, h, channels, 4);
      if (buf == null) {
        LOGGER.error("Image file [{}] not loaded: {}", filePath, stbi_failure_reason());
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

    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

    // Upload the texture data
    glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, buf);
    // Generate Mip Map
    glGenerateMipmap(GL_TEXTURE_2D);

    // Free used memory
    stbi_image_free(buf);
    LOGGER.info(
        "Texture [{}] successfully loaded, size is {}*{} with Linear filtering in RGBA mode",
        filePath,
        width,
        height);
    return new Texture(textureId, width, height, size, true);
  }

  /**
   * Loads a Bitmap font from the file system
   *
   * @param name the name of the font
   * @param file the path of the files of the font, without extension
   * @return the read {@link Font}
   */
  public static Font loadFont(String name, String file) {
    String fontFile = file + ".fnt";
    Float[] padding;
    float fontFactor;
    Collection<CharacterDescriptor> characters = new ArrayList<>();
    try (BufferedReader reader = new BufferedReader(new FileReader(fontFile))) {
      // meta
      String[] firstLine = reader.readLine().split(" ");
      String[] padding_str =
          Arrays.stream(firstLine)
              .filter(s -> s.startsWith("padding"))
              .findFirst()
              .orElse("padding=0,0,0,0")
              .split("=")[1]
              .split(",");
      String fontSizeStr =
          Arrays.stream(firstLine)
              .filter(s -> s.startsWith("size"))
              .findFirst()
              .orElse("size=1")
              .split("=")[1];
      String widthStr =
          Arrays.stream(reader.readLine().split(" "))
              .filter(s -> s.startsWith("scaleW"))
              .findFirst()
              .orElse("scaleW=0")
              .split("=")[1];
      float fontSize = Float.parseFloat(fontSizeStr);
      padding =
          Arrays.stream(padding_str).map(s -> Float.parseFloat(s) / fontSize).toArray(Float[]::new);
      float width = Float.parseFloat(widthStr);
      fontFactor = width / fontSize;

      // useless lines
      reader.readLine();
      reader.readLine();

      // reads every character
      reader
          .lines()
          .forEach(
              line -> {
                String[] pairs = line.split(" ");
                int id = 0;
                Vector2f pos = new Vector2f();
                Vector2f size = new Vector2f();
                Vector2f offset = new Vector2f();
                float advance = 0;
                for (String pair : pairs) {
                  String[] value = pair.split("=");
                  switch (value[0]) {
                    case "id" -> id = Integer.parseInt(value[1]);
                    case "x" -> pos.x = Integer.parseInt(value[1]) / width;
                    case "y" -> pos.y = Integer.parseInt(value[1]) / width;
                    case "width" -> size.x = Integer.parseInt(value[1]) / width;
                    case "height" -> size.y = Integer.parseInt(value[1]) / width;
                    case "xoffset" -> offset.x = Integer.parseInt(value[1]) / width;
                    case "yoffset" -> offset.y = Integer.parseInt(value[1]) / width;
                    case "xadvance" -> advance = Integer.parseInt(value[1]) / width;
                  }
                }
                characters.add(new CharacterDescriptor(id, pos, size, offset, advance));
              });

    } catch (IOException e) {
      LOGGER.error("Unable to load file [{}]", file);
      return null;
    }
    LOGGER.info("Font [{}] successfully loaded ({} characters)", file, characters.size());
    return new Font(name, characters, loadTexture(file + ".png"), padding, fontFactor);
  }

  /**
   * Decodes the bytes of a texture
   *
   * @param base64 the raw byte representing the texture file as base64
   * @return a Texture retrieved from an image file bytes
   */
  public static Texture decodeTexture(String base64) {
    try {
      // Decode the image and loads it into a buffer
      BufferedImage image =
          ImageIO.read(new ByteArrayInputStream(Base64.getDecoder().decode(base64)));
      int size = image.getWidth() * image.getHeight();
      ByteBuffer data = BufferUtils.createByteBuffer(size * 4);
      for (int pixel :
          image.getRGB(0, 0, image.getWidth(), image.getHeight(), new int[size], 0, 64)) {
        data.put((byte) ((pixel >>> 16) & 0xFF));
        data.put((byte) ((pixel >>> 8) & 0xFF));
        data.put((byte) ((pixel) & 0xFF));
        data.put((byte) ((pixel >>> 24) & 0xFF));
      }
      // Flip the buffer before feeding it to VRAM
      data.flip();
      // Create a new OpenGL texture
      int textureId = glGenTextures();
      // Bind the texture
      glBindTexture(GL_TEXTURE_2D, textureId);

      // Tell OpenGL how to unpack the RGBA bytes. Each component is 1 byte size
      glPixelStorei(GL_UNPACK_ALIGNMENT, 1);

      glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
      glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

      // Upload the texture data
      glTexImage2D(
          GL_TEXTURE_2D,
          0,
          GL_RGBA,
          image.getWidth(),
          image.getHeight(),
          0,
          GL_RGBA,
          GL_UNSIGNED_BYTE,
          data);
      // Generate Mip Map
      glGenerateMipmap(GL_TEXTURE_2D);

      // Free used memory

      LOGGER.info(
          "Texture successfully decoded, size is {}*{} with Linear filtering in RGBA mode",
          image.getWidth(),
          image.getHeight());
      return new Texture(textureId, image.getWidth(), image.getHeight(), size * 4, true);
    } catch (Exception e) {
      LOGGER.error("Unable to decode image file, caused by : {}", e.getMessage());
    }
    return null;
  }
}
