/*
 * Copyright (c) 2022-2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package org.alban098.graphics2j.example.interfaces;

import org.alban098.graphics2j.common.Window;
import org.alban098.graphics2j.interfaces.components.Button;
import org.alban098.graphics2j.interfaces.components.Dragger;
import org.alban098.graphics2j.interfaces.components.Line;
import org.alban098.graphics2j.interfaces.components.property.Properties;
import org.alban098.graphics2j.interfaces.components.text.TextLabel;
import org.alban098.graphics2j.interfaces.windows.DecoratedUI;
import org.alban098.graphics2j.interfaces.windows.Modal;
import org.joml.Vector2f;
import org.joml.Vector4f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExampleInterface extends DecoratedUI {

  private static final Logger LOGGER = LoggerFactory.getLogger(ExampleInterface.class);

  public ExampleInterface(Window window, String name) {
    super(window, name);
    getProperties()
        .set(Properties.BACKGROUND_COLOR, new Vector4f(198 / 255f, 223 / 255f, 250 / 255f, 0.75f))
        .set(Properties.CORNER_RADIUS, 10f)
        .set(Properties.SIZE, new Vector2f(640, 480))
        .set(Properties.POSITION, new Vector2f(50, 100));
    createElements();
  }

  private void createElements() {
    Dragger dragger = new Dragger();
    dragger
        .getProperties()
        .set(Properties.BACKGROUND_COLOR, new Vector4f(1, 0, 0, 0.75f))
        .set(Properties.SIZE, new Vector2f(50, 50))
        .set(Properties.POSITION, new Vector2f(295, 370))
        .set(Properties.CORNER_RADIUS, 25f);
    addElement("db", dragger);
    TextLabel sampleText =
        new TextLabel(
            "This is a sample text it has exactly 229 characters and it is quite long. It also has auto wrapping and that's pretty amazing !\nThis feature took me almost 14 hours to develop and optimize ...\nbut it finally works as intended !");
    sampleText
        .getProperties()
        .set(Properties.SIZE, new Vector2f(550, 200))
        .set(Properties.FONT_SIZE, 16f)
        .set(Properties.POSITION, new Vector2f(20, 20))
        .set(Properties.FONT_COLOR, new Vector4f(0.2f, 0.2f, 0.2f, 1))
        .set(Properties.FONT_FAMILY, "Calibri");
    addElement("2_text", sampleText);

    for (int j = 0; j < 4; j++) {
      for (int i = 0; i < 4; i++) {
        Button button = new Button("Button");
        button
            .getProperties()
            .set(Properties.BACKGROUND_COLOR, new Vector4f(1, 1, 1, 1))
            .set(Properties.SIZE, new Vector2f(85, 30))
            .set(Properties.POSITION, new Vector2f(40 + 160 * i, 85 + 75 * j))
            .set(Properties.FONT_COLOR, new Vector4f(1, 0, 0, 1))
            .set(Properties.BORDER_WIDTH, 3f)
            .set(Properties.FONT_SIZE, 24f)
            .set(Properties.CORNER_RADIUS, 5f);
        button.onClickEnd((input) -> LOGGER.info("{} clicked !", button.getText()));
        addElement("1_button_" + j + "_" + i, button);
      }
    }

    for (int i = 0; i < 3; i++) {
      Line line0 = new Line(new Vector2f(82, 100 + 75 * i), new Vector2f(562, 175 + 75 * i));
      Line line1 = new Line(new Vector2f(562, 100 + 75 * i), new Vector2f(82, 175 + 75 * i));

      line0
          .getProperties()
          .set(
              Properties.BACKGROUND_COLOR,
              new Vector4f((float) Math.random(), (float) Math.random(), (float) Math.random(), 1))
          .set(Properties.LINE_WIDTH, 5f);
      line1
          .getProperties()
          .set(
              Properties.BACKGROUND_COLOR,
              new Vector4f((float) Math.random(), (float) Math.random(), (float) Math.random(), 1))
          .set(Properties.LINE_WIDTH, 5f);

      Modal modal =
          new Modal(getWindow(), "modal") {
            @Override
            public void update(double elapsedTime) {
              // DO NOTHING
            }
          };
      modal.setManager(manager);
      modal
          .getProperties()
          .set(Properties.SIZE, new Vector2f(200, 100))
          .set(Properties.BACKGROUND_COLOR, new Vector4f(0.3f, 0.3f, 0.3f, 0.75f))
          .set(Properties.CORNER_RADIUS, 5f);
      TextLabel txt = new TextLabel("Modal test !");
      txt.getProperties()
          .set(Properties.SIZE, new Vector2f(180, 80))
          .set(Properties.FONT_SIZE, 16f)
          .set(Properties.POSITION, new Vector2f(10, 10))
          .set(Properties.FONT_COLOR, new Vector4f(1f, 0f, 0f, 1))
          .set(Properties.FONT_FAMILY, "Calibri");
      modal.addElement("text", txt);
      line0.setModal(modal);

      addElement("0_line_" + i + "_1", line1);
      addElement("0_line_" + i + "_0", line0);
    }
  }
}
