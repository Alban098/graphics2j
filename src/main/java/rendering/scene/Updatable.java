/*
 * Copyright (c) 2022, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering.scene;

public interface Updatable {
  void updateInternal(double elapsedTime);

  void update(double elapsedTime);
}
