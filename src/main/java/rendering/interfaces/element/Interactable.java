/*
 * Copyright (c) 2022, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering.interfaces.element;

import rendering.MouseInput;

public interface Interactable {

  boolean input(MouseInput input);
}
