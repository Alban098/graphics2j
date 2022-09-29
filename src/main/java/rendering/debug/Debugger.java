/*
 * Copyright (c) 2022, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering.debug;

import java.util.HashMap;
import java.util.Map;
import rendering.Engine;
import rendering.entities.Entity;

public class Debugger {

  private static final EntityDebugGUI DEFAULT_GUI = new DefaultEntityDebugGUI();
  private final Engine engine;
  private final Map<Class<? extends Entity>, EntityDebugGUI> registeredEntityDebugGUIs;

  public Debugger(Engine engine) {
    this.engine = engine;
    this.registeredEntityDebugGUIs = new HashMap<>();
  }

  public Engine getEngine() {
    return engine;
  }

  public <T extends Entity> void registerEntityDebugGUI(
      Class<T> type, EntityDebugGUI debugSection) {
    registeredEntityDebugGUIs.put(type, debugSection);
  }

  public <T extends Entity> EntityDebugGUI getDebugGUI(Class<T> type) {
    return registeredEntityDebugGUIs.getOrDefault(type, DEFAULT_GUI);
  }
}
