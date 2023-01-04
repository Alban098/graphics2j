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

  Consumer<MouseInput> onClickEnd();

  Consumer<MouseInput> onClickStart();

  Consumer<MouseInput> onHold();

  default void clickRoutine(MouseInput input, boolean inside) {
    if (isClicked() && !input.isLeftButtonPressed()) {
      onClickEnd().accept(input);
      setClicked(false);
    }
    if (isClicked() && input.isLeftButtonPressed()) {
      onHold().accept(input);
    }
    if (!isClicked() && inside && input.isLeftButtonPressed()) {
      onClickStart().accept(input);
      setClicked(true);
    }
  }
}
