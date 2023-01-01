/*
 * Copyright (c) 2022-2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering.interfaces.element;

public interface Clickable {

  boolean isClicked();

  void onClick(Runnable callback);

  void setClicked(boolean clicked);
}
