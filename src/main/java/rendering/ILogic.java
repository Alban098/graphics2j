/*
 * Copyright (c) 2022, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering;

import rendering.scene.Camera;
import rendering.scene.Scene;

/** This interface represents an abstract Logic */
public interface ILogic {

  /**
   * Initialize the Logic by creating the scene, the lights and the skybox
   *
   * @param window the Window to render to
   * @param engine the Engine running the logic
   * @throws Exception thrown if the skybox model or texture couldn't be loaded
   */
  void init(Window window, Engine engine) throws Exception;

  /**
   * Update the Camera movement variables
   *
   * @param window the Window where the scene is renderer to
   * @param mouseInput the MouseInput containing cursor information
   */
  void input(Window window, MouseInput mouseInput);

  /**
   * Update the simulation, called once every (1/TARGET_UPS sec)
   *
   * @param window the Window where the simulation is rendered
   * @param elapsedTime time elapsed since last update in seconds
   */
  void process(Window window, double elapsedTime);

  /**
   * Update the Camera's position and rotation
   *
   * @param window the windows where the scene is rendered to
   * @param mouseInput the MouseInput to use for camera rotation
   */
  void updateCamera(Window window, MouseInput mouseInput);

  /** Pause the Logic */
  void pause();

  /** Resume the Logic */
  void resume();

  /** Clear the memory used by the scene, and it's meshes */
  void cleanUp();

  Scene getScene();

  Camera getCamera();

  void initDebugger();
}
