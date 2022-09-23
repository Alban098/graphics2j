/*
 * Copyright (c) 2022, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering;

import static org.lwjgl.opengl.GL20.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Represent a Shader program loaded into the GPU */
public class ShaderProgram {

  private static final Logger LOGGER = LoggerFactory.getLogger(ShaderProgram.class);

  private final int programId;
  private final int vertexShader;
  private final int fragmentShader;

  /**
   * Create a new Shader program
   *
   * @param vertex path of the vertex shader
   * @param fragment path of the fragment shader
   */
  public ShaderProgram(String vertex, String fragment) {
    programId = glCreateProgram();

    vertexShader = glCreateShader(GL_VERTEX_SHADER);
    glShaderSource(vertexShader, ResourceLoader.loadFile(vertex));
    compile(vertexShader);

    fragmentShader = glCreateShader(GL_FRAGMENT_SHADER);
    glShaderSource(fragmentShader, ResourceLoader.loadFile(fragment));
    compile(fragmentShader);

    glAttachShader(programId, vertexShader);
    glAttachShader(programId, fragmentShader);

    glBindAttribLocation(programId, 0, "position");
    glBindAttribLocation(programId, 1, "textureCoords");

    glLinkProgram(programId);
    if (glGetProgrami(programId, GL_LINK_STATUS) == GL_FALSE) {
      LOGGER.error("{}", glGetProgramInfoLog(programId));
      System.exit(1);
    }
    glValidateProgram(programId);
    if (glGetProgrami(programId, GL_VALIDATE_STATUS) == GL_FALSE) {
      LOGGER.error("{}", glGetProgramInfoLog(programId));
      System.exit(1);
    }
  }

  /**
   * Compiles a shader program and checks for error
   *
   * @param id the id of the program to compile
   */
  private void compile(int id) {
    glCompileShader(id);
    if (glGetShaderi(id, GL_COMPILE_STATUS) == GL_FALSE) {
      LOGGER.error("{}", glGetShaderInfoLog(id));
      System.exit(1);
    }
  }

  /** Bind the shader for further use */
  public void bind() {
    glUseProgram(programId);
  }

  /** Unbind the shader */
  public void unbind() {
    glUseProgram(0);
  }

  /** Cleanup the Shader */
  public void cleanUp() {
    glDeleteShader(vertexShader);
    glDeleteShader(fragmentShader);
    glDeleteProgram(programId);
  }
}
