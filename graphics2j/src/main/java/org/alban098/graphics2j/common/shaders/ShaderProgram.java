/*
 * Copyright (c) 2022-2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package org.alban098.graphics2j.common.shaders;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL32.GL_GEOMETRY_SHADER;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.alban098.common.Cleanable;
import org.alban098.graphics2j.common.shaders.data.ShaderStorageBufferObject;
import org.alban098.graphics2j.common.shaders.data.model.Primitive;
import org.alban098.graphics2j.common.shaders.data.uniform.Uniform;
import org.alban098.graphics2j.common.shaders.data.vao.ArrayObject;
import org.alban098.graphics2j.common.shaders.data.vao.PointArrayObject;
import org.alban098.graphics2j.common.shaders.data.vao.VertexArrayObject;
import org.alban098.graphics2j.common.utils.ResourceLoader;
import org.lwjgl.opengl.GL20;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Represent a Shader program loaded into the GPU */
public final class ShaderProgram implements Cleanable {

  /** Just a Logger to log events */
  private static final Logger LOGGER = LoggerFactory.getLogger(ShaderProgram.class);

  /** The id of the Shader as provided by OpenGL */
  private final int programId;
  /** The id of the Vertex Shader as provided by OpenGL */
  private final int vertexShader;
  /** The path of the Vertex Shader file */
  private String vertexFile;
  /** The id of the Geometry Shader as provided by OpenGL */
  private final int geometryShader;
  /** The path of the Geometry Shader file */
  private String geometryFile;
  /** The id of the Fragment Shader as provided by OpenGL */
  private final int fragmentShader;
  /** The path of the Fragment Shader file */
  private String fragmentFile;
  /** The Shader's name */
  private final String name;

  /** A List of all {@link ShaderAttribute}s declared for this Shader */
  private final List<ShaderAttribute> attributes;
  /** A Map of all {@link Uniform}s declared in this Shader, indexed by their name */
  private final Map<String, Uniform<?>> uniforms;

  private VertexMode mode;

  /**
   * Create a new Shader program
   *
   * @param name a name for the Shader
   * @param vertex the {@link File} of the vertex shader
   * @param fragment the {@link File} of the fragment shader
   * @param attributes an array of all additional {@link ShaderAttribute}s
   * @param uniforms an array of all additional {@link Uniform}s
   */
  public ShaderProgram(
      String name,
      File vertex,
      File fragment,
      ShaderAttribute[] attributes,
      Uniform<?>[] uniforms) {
    this(
        name,
        ResourceLoader.loadFile(vertex),
        null,
        ResourceLoader.loadFile(fragment),
        attributes,
        uniforms);
  }

  /**
   * Create a new Shader program
   *
   * @param name a name for the Shader
   * @param vertex the {@link File} of the vertex shader
   * @param fragment the {@link File} of the fragment shader
   * @param geometry the {@link File} of the geometry shader
   * @param attributes an array of all additional {@link ShaderAttribute}s
   * @param uniforms an array of all additional {@link Uniform}s
   */
  public ShaderProgram(
      String name,
      File vertex,
      File geometry,
      File fragment,
      ShaderAttribute[] attributes,
      Uniform<?>[] uniforms) {
    this(
        name,
        ResourceLoader.loadFile(vertex),
        geometry == null ? null : ResourceLoader.loadFile(geometry),
        ResourceLoader.loadFile(fragment),
        attributes,
        uniforms);
    this.vertexFile = vertex.getAbsolutePath();
    this.geometryFile = geometry != null ? geometry.getAbsolutePath() : null;
    this.fragmentFile = fragment.getAbsolutePath();
    this.mode = geometry == null ? VertexMode.VERTEX : VertexMode.INDEX;
  }

  /**
   * Create a new Shader program
   *
   * @param name a name for the Shader
   * @param vertex the content of the vertex shader
   * @param fragment the content of the fragment shader
   * @param attributes an array of all additional {@link ShaderAttribute}s
   * @param uniforms an array of all additional {@link Uniform}s
   */
  public ShaderProgram(
      String name,
      String vertex,
      String fragment,
      ShaderAttribute[] attributes,
      Uniform<?>[] uniforms) {
    this(name, vertex, null, fragment, attributes, uniforms);
  }

  /**
   * Create a new Shader program
   *
   * @param name a name for the Shader
   * @param vertex the content of the vertex shader
   * @param fragment the content of the fragment shader
   * @param geometry the content of the geometry shader
   * @param attributes an array of all additional {@link ShaderAttribute}s
   * @param uniforms an array of all additional {@link Uniform}s
   */
  public ShaderProgram(
      String name,
      String vertex,
      String geometry,
      String fragment,
      ShaderAttribute[] attributes,
      Uniform<?>[] uniforms) {
    this.programId = glCreateProgram();
    this.uniforms = new HashMap<>();

    this.mode = geometry == null ? VertexMode.VERTEX : VertexMode.INDEX;

    this.vertexShader = glCreateShader(GL_VERTEX_SHADER);
    this.vertexFile = "internal";
    this.name = name;
    GL20.glShaderSource(vertexShader, vertex);
    compile(vertexShader);

    if (geometry != null) {
      this.geometryShader = glCreateShader(GL_GEOMETRY_SHADER);
      this.geometryFile = "internal";
      GL20.glShaderSource(geometryShader, geometry);
      compile(geometryShader);
    } else {
      this.geometryShader = -1;
    }

    this.fragmentShader = glCreateShader(GL_FRAGMENT_SHADER);
    this.fragmentFile = "internal";
    glShaderSource(fragmentShader, fragment);
    compile(fragmentShader);

    glAttachShader(programId, vertexShader);
    if (geometry != null) {
      glAttachShader(programId, geometryShader);
    }
    glAttachShader(programId, fragmentShader);

    if (geometry == null) {
      this.attributes =
          new ArrayList<>(List.of(ShaderAttributes.VERTEX, ShaderAttributes.TRANSFORM_INDEX));
    } else {
      this.attributes = new ArrayList<>(List.of(ShaderAttributes.INDEX));
    }
    this.attributes.addAll(List.of(attributes));

    for (ShaderAttribute attribute : this.attributes) {
      glBindAttribLocation(programId, attribute.getLocation(), attribute.getName());
    }

    glLinkProgram(programId);
    if (glGetProgrami(programId, GL_LINK_STATUS) == GL_FALSE) {
      LOGGER.error(
          "Failed to link ShaderProgram {}, caused by : {}",
          programId,
          glGetProgramInfoLog(programId));
      System.exit(-1);
    }

    storeAllUniformLocations(uniforms);

    glValidateProgram(programId);
    if (glGetProgrami(programId, GL_VALIDATE_STATUS) == GL_FALSE) {
      LOGGER.error(
          "Failed to validate ShaderProgram {}, caused by : {}",
          programId,
          glGetProgramInfoLog(programId));
      System.exit(-1);
    }
    LOGGER.info(
        "Created Shader with id {} with {} attributes and {} uniforms",
        this.programId,
        this.attributes.size(),
        this.uniforms.size());
    initialize();
  }

  /**
   * Allocate the memory on the GPU's RAM for all the Uniforms variables of this shader
   *
   * @param uniforms an array of all {@link Uniform}s to store
   */
  public void storeAllUniformLocations(Uniform<?>[] uniforms) {
    for (Uniform<?> uniform : uniforms) {
      this.uniforms.put(uniform.getName(), uniform);
      uniform.storeUniformLocation(programId);
    }
  }

  /**
   * Retrieves a {@link Uniform} by its name cast to a type, if present in this Shader
   *
   * @param name the name of the {@link Uniform} to retrieve
   * @param type the class type of the {@link Uniform} to retrieve
   * @return The retrieved {@link Uniform} cast, null if not present
   * @param <T> the type of the {@link Uniform} to retrieve
   */
  public <T extends Uniform<?>> T getUniform(String name, Class<T> type) {
    return (T) uniforms.get(name);
  }

  /**
   * Compiles a shader program and checks for error
   *
   * @param id the id of the program to compile
   */
  private void compile(int id) {
    glCompileShader(id);
    if (glGetShaderi(id, GL_COMPILE_STATUS) == GL_FALSE) {
      LOGGER.error("Failed to compile Shader {}, caused by :{}", id, glGetShaderInfoLog(id));
      System.exit(1);
    }
    LOGGER.info("Successfully compiled Shader {}", id);
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
  @Override
  public void cleanUp() {
    glDeleteShader(vertexShader);
    glDeleteShader(geometryShader);
    glDeleteShader(fragmentShader);
    glDeleteProgram(programId);
    LOGGER.info("Shader {} cleaned up", programId);
  }

  /**
   * Create a {@link ArrayObject} that can be used to load objects to this Shader, with all the
   * right data structures initialized (VBOs and SSBOs)
   *
   * @param maxPrimitive the number of quads this VAO must be able to batch
   * @param withSSBO does a Transform {@link ShaderStorageBufferObject} is necessary
   * @return a compatible {@link ArrayObject} fully initialized and usable immediately
   */
  public ArrayObject createCompatibleVao(
      int maxPrimitive, boolean withSSBO, VertexMode mode, Primitive primitive) {
    ArrayObject vao =
        switch (mode) {
          case VERTEX -> new VertexArrayObject(maxPrimitive, withSSBO, primitive);
          case INDEX -> new PointArrayObject(maxPrimitive, withSSBO);
        };
    attributes.forEach(vao::createVBO);
    LOGGER.info("Created VAO for Shader {}", programId);
    return vao;
  }

  /**
   * Returns the id of the Shader as provided by OpenGL
   *
   * @return the id of the Shader as provided by OpenGL
   */
  public int getProgramId() {
    return programId;
  }

  /**
   * Returns the id of the Vertex Shader as provided by OpenGL
   *
   * @return the id of the Vertex Shader as provided by OpenGL
   */
  public int getVertexShader() {
    return vertexShader;
  }

  /**
   * Returns the id of the Geometry Shader as provided by OpenGL
   *
   * @return the id of the Geometry Shader as provided by OpenGL
   */
  public int getGeometryShader() {
    return geometryShader;
  }

  /**
   * Returns the id of the Fragment Shader as provided by OpenGL
   *
   * @return the id of the Fragment Shader as provided by OpenGL
   */
  public int getFragmentShader() {
    return fragmentShader;
  }

  /**
   * Returns the path of the Vertex Shader file
   *
   * @return the path of the Vertex Shader file
   */
  public String getVertexFile() {
    return vertexFile;
  }

  /**
   * Returns the path of the Geometry Shader file
   *
   * @return the path of the Geometry Shader file
   */
  public String getGeometryFile() {
    return geometryFile;
  }

  /**
   * Returns the path of the Fragment Shader file
   *
   * @return the path of the Fragment Shader file
   */
  public String getFragmentFile() {
    return fragmentFile;
  }

  /**
   * Returns a Collection of all {@link ShaderAttribute}s declared in the Shader
   *
   * @return a Collection of all {@link ShaderAttribute}s declared in the Shader
   */
  public List<ShaderAttribute> getAttributes() {
    return attributes;
  }

  /**
   * Returns a Collection of all {@link Uniform}s declared in Shader
   *
   * @return a Collection of all {@link Uniform}s declared in Shader
   */
  public Map<String, Uniform<?>> getUniforms() {
    return uniforms;
  }

  /**
   * Returns the name of the ShaderProgram
   *
   * @return the name of the ShaderProgram
   */
  public String getName() {
    return name;
  }

  @Override
  public int hashCode() {
    return programId;
  }

  @Override
  public String toString() {
    return String.valueOf(name);
  }

  public VertexMode getMode() {
    return mode;
  }
}
