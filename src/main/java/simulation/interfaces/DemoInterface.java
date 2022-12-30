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
import rendering.interfaces.ControlableInterface;
import rendering.interfaces.InterfaceManager;
import rendering.interfaces.element.Button;
import rendering.interfaces.element.Dragger;

public class DemoInterface extends ControlableInterface {

  private static final Logger LOGGER = LoggerFactory.getLogger(DemoInterface.class);

  public DemoInterface(Window window, String name, InterfaceManager manager) {
    super(window, name, manager);
    getProperties()
        .setBackgroundColor(198 / 255f, 223 / 255f, 250 / 255f, 0.75f)
        .setCornerRadius(10)
        .setSize(640, 480)
        .setPosition(50, 100);
    createElements();
  }

  private void createElements() {
    Dragger dragger = new Dragger();
    dragger
        .getProperties()
        .setBackgroundColor(1, 0, 0, 0.75f)
        .setSize(50, 50)
        .setPosition(295, 370)
        .setCornerRadius(25);
    addElement("db", dragger);

    for (int j = 0; j < 4; j++) {
      for (int i = 0; i < 4; i++) {
        Button button = new Button("Button_" + j + "_" + i, new Vector4f());
        button
            .getProperties()
            .setBackgroundColor(
                (float) Math.random(), (float) Math.random(), (float) Math.random(), 1)
            .setSize(70, 30)
            .setPosition(45 + 160 * i, 10 + 90 * j)
            .setCornerRadius(5);
        button.onClick(() -> LOGGER.info("{} clicked !", button.getText()));
        addElement("Button_" + j + "_" + i, button);
      }
    }
  }
}
