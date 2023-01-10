/*
 * Copyright (c) 2022-2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering.debug.component;

import rendering.scene.entities.component.Component;

public interface ComponentDebugInterface<T extends Component> {

  Class<T> getComponentClass();

  String getDisplayName();

  boolean draw(Component component);
}
