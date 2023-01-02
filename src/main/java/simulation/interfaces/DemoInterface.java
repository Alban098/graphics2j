/*
 * Copyright (c) 2022-2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package simulation.interfaces;

import org.joml.Vector2f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rendering.Window;
import rendering.interfaces.ControllableInterface;
import rendering.interfaces.InterfaceManager;
import rendering.interfaces.element.Button;
import rendering.interfaces.element.Dragger;
import rendering.interfaces.element.Line;
import rendering.interfaces.element.TextLabel;

public class DemoInterface extends ControllableInterface {

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
    TextLabel sampleText =
        new TextLabel(
            "This is a sample text it has exactly 229 characters and it is quite long. It also has auto wrappingand that's pretty amazing ! This feature took me almost 14 hours to develop and optimize ...    but it finally works as intended !");
    sampleText
        .getProperties()
        .setSize(600, 200)
        .setFontSize(16)
        .setPosition(20, 20)
        .setFontColor(0.2f, 0.2f, 0.2f, 1)
        .setFontFamily("Calibri");
    addElement("2_text", sampleText);

    for (int j = 0; j < 4; j++) {
      for (int i = 0; i < 4; i++) {
        Button button = new Button("Button");
        button
            .getProperties()
            .setBackgroundColor(1, 1, 1, 1)
            .setSize(85, 30)
            .setPosition(40 + 160 * i, 85 + 75 * j)
            .setFontColor(1, 0, 0, 1)
            .setBorderWidth(3)
            .setFontSize(24)
            .setCornerRadius(5);
        button.onClickEnd((input) -> LOGGER.info("{} clicked !", button.getText()));
        addElement("1_button_" + j + "_" + i, button);
      }
    }

    for (int i = 0; i < 3; i++) {
      Line line0 = new Line(new Vector2f(82, 100 + 75 * i), new Vector2f(562, 175 + 75 * i));
      Line line1 = new Line(new Vector2f(562, 100 + 75 * i), new Vector2f(82, 175 + 75 * i));

      line0
          .getProperties()
          .setBackgroundColor(
              (float) Math.random(), (float) Math.random(), (float) Math.random(), 1)
          .setLineWidth(5);
      line1
          .getProperties()
          .setBackgroundColor(
              (float) Math.random(), (float) Math.random(), (float) Math.random(), 1)
          .setLineWidth(5);

      addElement("0_line_" + i + "_1", line1);
      addElement("0_line_" + i + "_0", line0);
    }
  }
}
