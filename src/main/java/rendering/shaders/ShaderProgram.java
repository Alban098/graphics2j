/*
 * Copyright (c) 2022, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering.shaders;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL32.GL_GEOMETRY_SHADER;

import java.util.*;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL20;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rendering.ResourceLoader;
import rendering.data.VertexArrayObject;
import rendering.shaders.uniform.*;

/** Represent a Shader program loaded into the GPU */
public class ShaderProgram {

  private static final Logger LOGGER = LoggerFactory.getLogger(ShaderProgram.class);

  private final int programId;
  private final int vertexShader;
  private final String vertexFile;
  private final int geometryShader;
  private final String geometryFile;
  private final int fragmentShader;
  private final String fragmentFile;

  private final List<ShaderAttribute> attributes;
  private final Map<String, Uniform<?>> uniforms;

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
      Uniform<?>[] uniforms) {
    this.programId = glCreateProgram();
    this.uniforms = new HashMap<>();

    this.vertexShader = glCreateShader(GL_VERTEX_SHADER);
    this.vertexFile = vertex;
    GL20.glShaderSource(vertexShader, ResourceLoader.loadFile(vertex));
    compile(vertexShader);

    this.geometryShader = glCreateShader(GL_GEOMETRY_SHADER);
    this.geometryFile = geometry;
    GL20.glShaderSource(geometryShader, ResourceLoader.loadFile(geometry));
    compile(geometryShader);

    this.fragmentShader = glCreateShader(GL_FRAGMENT_SHADER);
    this.fragmentFile = fragment;
    glShaderSource(fragmentShader, ResourceLoader.loadFile(fragment));
    compile(fragmentShader);

    glAttachShader(programId, vertexShader);
    if (geometry != null) {
      glAttachShader(programId, geometryShader);
    }
    glAttachShader(programId, fragmentShader);

    this.attributes = new ArrayList<>(List.of(ShaderAttributes.INDEX));
    this.attributes.addAll(List.of(attributes));

    for (ShaderAttribute attribute : this.attributes) {
      glBindAttribLocation(programId, attribute.getLocation(), attribute.getName());
    }

    glLinkProgram(programId);
    if (glGetProgrami(programId, GL_LINK_STATUS) == GL_FALSE) {
      LOGGER.error("{}", glGetProgramInfoLog(programId));
      System.exit(-1);
    }

    storeAllUniformLocations(uniforms);

    glValidateProgram(programId);
    if (glGetProgrami(programId, GL_VALIDATE_STATUS) == GL_FALSE) {
      LOGGER.error("{}", glGetProgramInfoLog(programId));
      System.exit(-1);
    }
    LOGGER.debug(
        "Created Shader with id {} with {} attributes and {} uniforms",
        this.programId,
        this.attributes.size(),
        this.uniforms.size());
  }

  /** Allocate the memory on the GPU's RAM for all the Uniforms variables of this shader */
  public void storeAllUniformLocations(Uniform<?>[] uniforms) {
    Uniform<Boolean> uniform0 = new UniformBoolean(Uniforms.WIREFRAME.getName(), false);
    Uniform<Vector4f> uniform1 =
        new UniformVec4(Uniforms.WIREFRAME_COLOR.getName(), new Vector4f(1, 1, 1, 1));
    uniform0.storeUniformLocation(programId);
    uniform1.storeUniformLocation(programId);

    this.uniforms.put(Uniforms.WIREFRAME.getName(), uniform0);
    this.uniforms.put(Uniforms.WIREFRAME_COLOR.getName(), uniform1);

    for (Uniform<?> uniform : uniforms) {
      this.uniforms.put(uniform.getName(), uniform);
      uniform.storeUniformLocation(programId);
    }
  }

  public <T extends Uniform> T getUniform(Uniforms uniform, Class<T> type) {
    return (T) uniforms.get(uniform.getName());
  }

  public <T extends Uniform> T getUniform(String uniform, Class<T> type) {
    return (T) uniforms.get(uniform);
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
    LOGGER.debug("Shader {} cleaned up", programId);
  }

  public VertexArrayObject createCompatibleVao(int maxQuadCapacity) {
    VertexArrayObject vao = new VertexArrayObject(maxQuadCapacity);
    attributes.forEach(vao::linkVbo);
    return vao;
  }

  public int getProgramId() {
    return programId;
  }

  public int getVertexShader() {
    return vertexShader;
  }

  public int getGeometryShader() {
    return geometryShader;
  }

  public int getFragmentShader() {
    return fragmentShader;
  }

  public String getVertexFile() {
    return vertexFile;
  }

  public String getGeometryFile() {
    return geometryFile;
  }

  public String getFragmentFile() {
    return fragmentFile;
  }

  public List<ShaderAttribute> getAttributes() {
    return attributes;
  }

  public Map<String, Uniform<?>> getUniforms() {
    return uniforms;
  }
}
