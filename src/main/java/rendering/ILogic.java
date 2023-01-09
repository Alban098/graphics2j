/*
 * Copyright (c) 2022-2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering;

import rendering.scene.Camera;
import rendering.scene.Scene;

/** This interface represents an abstract Logic */
public interface ILogic {

  /**
   * Initialize the Logic
   *
   * @param window the Window to render to
   * @param engine the Engine running the logic
   * @param mouseInput the mouse input state to link
   */
  void init(Window window, Engine engine, MouseInput mouseInput);

  /**
   * Update the Camera movement variables
   *
   * @param window the Window where the scene is renderer to
   */
  void input(Window window);

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

  /**
   * Returns the {@link Scene} of the Logic
   *
   * @return the {@link Scene} of the Logic
   */
  Scene getScene();

  /**
   * Returns the {@link Camera} of the Logic
   *
   * @return the {@link Camera} of the Logic
   */
  Camera getCamera();

  /** Initializes the debugger if debug is enabled */
  void initDebugger();
}
