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

  void setClicked(boolean clicked);

  void onClickEnd(Consumer<MouseInput> callback);

  void onClickStart(Consumer<MouseInput> callback);

  void onHold(Consumer<MouseInput> callback);

  void onClickEnd(MouseInput input);

  void onClickStart(MouseInput input);

  void onHold(MouseInput input);

  default void clickRoutine(MouseInput input, boolean inside) {
    if (isClicked() && !input.isLeftButtonPressed()) {
      onClickEnd(input);
      setClicked(false);
    }
    if (isClicked() && input.isLeftButtonPressed()) {
      onHold(input);
    }
    if (!isClicked() && inside && input.isLeftButtonPressed()) {
      onClickStart(input);
      setClicked(true);
    }
  }
}
