/*
 * Copyright (c) 2022, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering;

/** This interface represents an abstract Logic */
public interface ILogic {

  /**
   * Initialize the Logic by creating the scene, the lights and the skybox
   *
   * @param window the Window to render to
   * @throws Exception thrown if the skybox model or texture couldn't be loaded
   */
  void init(Window window) throws Exception;

  /**
   * Update the Camera movement variables
   *
   * @param window the Window where the scene is renderer to
   * @param mouseInput the MouseInput containing cursor information
   */
  void input(Window window, MouseInput mouseInput);

  /**
   * Update the simulation, called once every (1/TARGET_UPS sec, see settings package)
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

  /**
   * Render the scene to the screen, called once every frame
   *
   * @param window the Window ro render to
   */
  void render(Window window);

  /** Pause the Logic */
  void pause();

  /** Resume the Logic */
  void resume();

  /** Clear the memory used by the scene, and it's meshes */
  void cleanup();
}
