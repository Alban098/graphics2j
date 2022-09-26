/*
 * Copyright (c) 2022, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering.shaders;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL32.GL_GEOMETRY_SHADER;

import java.util.*;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL20;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rendering.ResourceLoader;
import rendering.data.Vao;
import rendering.shaders.uniform.Uniform;
import rendering.shaders.uniform.UniformMat4;
import rendering.shaders.uniform.Uniforms;

/** Represent a Shader program loaded into the GPU */
public class ShaderProgram {

  private static final Logger LOGGER = LoggerFactory.getLogger(ShaderProgram.class);

  private final int programId;
  private final int vertexShader;
  private final int geometryShader;
  private final int fragmentShader;

  private final List<ShaderAttribute> attributes;
  private final Map<String, Uniform> uniforms;

  /**
   * Create a new Shader program
   *
   * @param vertex path of the vertex shader
   * @param fragment path of the fragment shader
   */
  public ShaderProgram(
      String vertex,
      String geometry,
      String fragment,
      ShaderAttribute[] attributes,
      Uniform[] uniforms) {
    programId = glCreateProgram();
    this.uniforms = new HashMap<>();

    vertexShader = glCreateShader(GL_VERTEX_SHADER);
    GL20.glShaderSource(vertexShader, ResourceLoader.loadFile(vertex));
    compile(vertexShader);

    geometryShader = glCreateShader(GL_GEOMETRY_SHADER);
    GL20.glShaderSource(geometryShader, ResourceLoader.loadFile(geometry));
    compile(geometryShader);

    fragmentShader = glCreateShader(GL_FRAGMENT_SHADER);
    glShaderSource(fragmentShader, ResourceLoader.loadFile(fragment));
    compile(fragmentShader);

    glAttachShader(programId, vertexShader);
    glAttachShader(programId, geometryShader);
    glAttachShader(programId, fragmentShader);

    this.attributes =
        new ArrayList<>(
            List.of(ShaderAttributes.POSITION, ShaderAttributes.ROTATION, ShaderAttributes.SCALE));
    this.attributes.addAll(List.of(attributes));

    for (ShaderAttribute attribute : this.attributes) {
      glBindAttribLocation(programId, attribute.getLocation(), attribute.getName());
    }

    glLinkProgram(programId);
    if (glGetProgrami(programId, GL_LINK_STATUS) == GL_FALSE) {
      LOGGER.error("{}", glGetProgramInfoLog(programId));
      System.exit(1);
    }

    storeAllUniformLocations(uniforms);

    glValidateProgram(programId);
    if (glGetProgrami(programId, GL_VALIDATE_STATUS) == GL_FALSE) {
      LOGGER.error("{}", glGetProgramInfoLog(programId));
      System.exit(1);
    }
  }

  /** Allocate the memory on the GPU's RAM for all the Uniforms variables of this shader */
  public void storeAllUniformLocations(Uniform[] uniforms) {
    Uniform uniform0 = new UniformMat4("viewMatrix", new Matrix4f().identity());
    Uniform uniform1 = new UniformMat4("projectionMatrix", new Matrix4f().identity());
    uniform0.storeUniformLocation(programId);
    uniform1.storeUniformLocation(programId);

    this.uniforms.put(Uniforms.VIEW_MATRIX.getName(), uniform0);
    this.uniforms.put(Uniforms.PROJECTION_MATRIX.getName(), uniform1);

    for (Uniform uniform : uniforms) {
      this.uniforms.put(uniform.getName(), uniform);
      uniform.storeUniformLocation(programId);
    }
  }

  public Uniform getUniform(Uniforms uniform) {
    return uniforms.get(uniform.getName());
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
    glDeleteShader(geometryShader);
    glDeleteShader(fragmentShader);
    glDeleteProgram(programId);
  }

  public Vao createCompatibleVao(int maxQuadCapacity) {
    Vao vao = new Vao(maxQuadCapacity);
    attributes.forEach(vao::linkVbo);
    return vao;
  }
}
