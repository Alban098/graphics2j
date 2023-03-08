/*
 * Copyright (c) 2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package org.alban098.engine2j.interfaces.components.text;

/**
 * Interface making an Object containing text, a Textable must precompute its text models when
 * possible
 */
public interface Textable {

  /**
   * Returns the text of this TextLabel
   *
   * @return the text of this TextLabel
   */
  String getText();

  /**
   * Sets the text of this TextLabel and recompute its model
   *
   * @param text the new text to set
   */
  void setText(String text);

  /** Computes the models used to render the text on the screen */
  void precomputeModels();
}
