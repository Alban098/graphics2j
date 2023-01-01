/*
 * Copyright (c) 2022-2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering.shaders.uniform;

import static org.lwjgl.opengl.GL20.glGetUniformLocation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class Uniform<T> {

  private static final int NOT_FOUND = -1;
  private static final Logger LOGGER = LoggerFactory.getLogger(Uniform.class);

  private final String name;
  private int location;

  protected T currentValue;

  /**
   * Create a new Uniform
   *
   * @param name name of the uniform, must be the same as in the Shader program
   */
  Uniform(String name) {
    this.name = name;
  }

  /**
   * Allocate GPU RAM for this Uniform to the shader
   *
   * @param programID shader ID
   */
  public void storeUniformLocation(int programID) {
    location = glGetUniformLocation(programID, name);
    LOGGER.debug(
        "Created uniform at location {} form shader {} with name \"{}\"",
        location,
        programID,
        name);
    if (location == NOT_FOUND) {
      LOGGER.error("Uniform {} not found for shader : {}", name, programID);
    }
  }

  /**
   * Return the location of the Uniform
   *
   * @return uniform location
   */
  public int getLocation() {
    return location;
  }

  public T getValue() {
    return currentValue;
  }

  public abstract void load(T value);

  public abstract Object getDefault();

  public abstract void loadDefault();

  public abstract int getDimension();

  public abstract String getType();

  public String getName() {
    return name;
  }
}
