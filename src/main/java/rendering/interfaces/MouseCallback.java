/*
 * Copyright (c) 2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering.interfaces;

import java.util.function.Consumer;
import rendering.MouseInput;

public class MouseCallback {

  private Consumer<MouseInput> consumer;

  public void setCallback(Consumer<MouseInput> consumer) {
    this.consumer = consumer;
  }

  public void accept(MouseInput input) {
    if (consumer != null) {
      consumer.accept(input);
    }
  }
}
