/*
 * Copyright (c) 2022, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering.renderers;

import java.util.Collection;
import rendering.entities.component.Component;
import rendering.scene.Updatable;

public interface Componentable extends Updatable {

  Componentable addComponent(Component component);

  <T extends Component> T getComponent(Class<T> type);

  boolean hasComponent(Class<? extends Component> type);

  void cleanUpInternal();

  String getName();

  Collection<Component> getComponents();
}
