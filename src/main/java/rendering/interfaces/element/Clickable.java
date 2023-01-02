/*
 * Copyright (c) 2022-2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering.interfaces.element;

import java.util.function.Consumer;
import rendering.MouseInput;

public interface Clickable {

  boolean isClicked();

  void onClickEnd(Consumer<MouseInput> callback);

  void onHold(Consumer<MouseInput> callback);

  void onClickStart(Consumer<MouseInput> callback);

  void setClicked(boolean clicked);

  boolean input(MouseInput input);
}
