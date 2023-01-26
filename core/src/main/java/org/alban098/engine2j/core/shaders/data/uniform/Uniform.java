/*
 * Copyright (c) 2022-2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package org.alban098.engine2j.core.shaders.data.uniform;

import static org.lwjgl.opengl.GL20.glGetUniformLocation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represent a Uniform that can be bound to a {@link org.alban098.engine2j.core.shaders.ShaderProgram}
 *
 * @param <T> the type of data stored by the {@link Uniform}
 */
public abstract class Uniform<T> {

  private static final int NOT_FOUND = -1;
  protected static final Logger LOGGER = LoggerFactory.getLogger(Uniform.class);

  /** The name of the Uniform */
  private final String name;
  /** The location of the Uniform as provided by OpenGL */
  private int location;
  /** The current value of the Uniform */
  protected T currentValue;
  /** The default value of the Uniform */
  protected T defaultValue;

  /**
   * Create a new Uniform
   *
   * @param name name of the uniform, must be the same as in the Shader program
   */
  Uniform(String name, T defaultValue) {
    this.name = name;
    this.defaultValue = defaultValue;
  }

  /**
   * Allocate GPU RAM for this Uniform to the shader
   *
   * @param programID shader ID
   */
  public void storeUniformLocation(int programID) {
    location = glGetUniformLocation(programID, name);
    LOGGER.info(
        "Created uniform at location {} form Shader {} with name \"{}\"",
        location,
        programID,
        name);
    if (location == NOT_FOUND) {
      LOGGER.warn("Uniform {} not found for Shader : {}", name, programID);
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

  /**
   * Returns the current value of the Uniform
   *
   * @return the current value of the Uniform
   */
  public T getValue() {
    return currentValue;
  }

  /**
   * Loads a value into the Uniform
   *
   * @param value the value to load
   */
  public abstract void load(T value);

  /**
   * Returns the default value of the Uniform (into RAM and VRAM)
   *
   * @return the default value of the Uniform
   */
  public T getDefault() {
    return defaultValue;
  }

  /** Load the default value into RAM and VRAM shortcut for load(getDefault()); */
  public void loadDefault() {
    load(defaultValue);
  }

  /**
   * Returns the dimension of the data stored in this Uniform
   *
   * @return the dimension of the data stored in this Uniform
   */
  public abstract int getDimension();

  /**
   * Gets the formatted type of the data stored in this Uniform
   *
   * @return the formatted type of the data stored in this Uniform
   */
  public abstract String getType();

  /**
   * Returns the name of the Uniform
   *
   * @return the name of the Uniform
   */
  public String getName() {
    return name;
  }
}
