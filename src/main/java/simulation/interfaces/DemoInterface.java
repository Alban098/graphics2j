/*
 * Copyright (c) 2022, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package simulation.interfaces;

import org.joml.Vector4f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rendering.Window;
import rendering.interfaces.InterfaceManager;
import rendering.interfaces.UserInterface;
import rendering.interfaces.element.Button;
import rendering.interfaces.element.CornerProperties;

public class DemoInterface extends UserInterface {

  private static final Logger LOGGER = LoggerFactory.getLogger(DemoInterface.class);

  public DemoInterface(Window window, String name, InterfaceManager manager) {
    super(window, new Vector4f(198 / 255f, 223 / 255f, 250 / 255f, .75f), name, true, manager);
    setCornerProperties(new CornerProperties(10, 10, 10, 10));
    setSize(640, 480);
    createElements(window);
  }

  private void createElements(Window window) {
    for (int j = 0; j < 4; j++) {
      for (int i = 0; i < 4; i++) {
        Button button =
            new Button(
                    window,
                    new Vector4f(
                        (float) Math.random(), (float) Math.random(), (float) Math.random(), 1),
                    "Button_" + j + "_" + i,
                    this)
                .setSize(70, 30)
                .setPosition(45 + 160 * i, 90 + 90 * j)
                .setCornerProperties(new CornerProperties(5, 5, 5, 5));
        button.onClick(() -> LOGGER.info("{} clicked !", button.getText()));
        addElement("Button_" + j + "_" + i, button);
      }
    }
  }
}
