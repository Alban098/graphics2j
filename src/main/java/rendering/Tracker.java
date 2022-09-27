/*
 * Copyright (c) 2022, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering;

import rendering.scene.Scene;

public class Tracker {

  private final Engine engine;
  private final Scene scene;

  public Tracker(Engine engine, Scene scene) {
    this.engine = engine;
    this.scene = scene;
  }

  public Engine getEngine() {
    return engine;
  }

  public Scene getScene() {
    return scene;
  }
}
