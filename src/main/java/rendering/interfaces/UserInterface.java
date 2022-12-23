/*
 * Copyright (c) 2022, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering.interfaces;

import java.util.HashMap;
import java.util.Map;
import rendering.interfaces.components.ComponentInstance;

public class UserInterface {

  private final Map<String, ComponentInstance> components = new HashMap<>();
}
