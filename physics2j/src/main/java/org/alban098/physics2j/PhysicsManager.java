/*
 * Copyright (c) 2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package org.alban098.physics2j;

import java.util.Collection;
import java.util.HashSet;
import org.joml.Vector2f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PhysicsManager {

  /** Just a Logger to log events */
  private static final Logger LOGGER = LoggerFactory.getLogger(PhysicsManager.class);

  private static final int TPS = 50;

  /** A Collection of all Movable physics should be applied to */
  private final Collection<Physical> registered = new HashSet<>();

  private final QuadTree<Physical> quadTree;

  private final Force staticForce = new Force(0, 0);

  private double subTickAccumulator = 0;

  public PhysicsManager(Force... staticForces) {
    for (Force force : staticForces) {
      staticForce.combine(force);
    }
    quadTree = new QuadTree<>(new Vector2f(5000f, 5000f));
  }

  public void track(Physical physical) {
    if (registered.add(physical)) {
      quadTree.add(physical);
      physical.getPhysicsComponent().applyForce(staticForce, new Vector2f());
    }
  }

  public void unregister(Physical physical) {
    if (registered.remove(physical)) {
      quadTree.remove(physical);
      physical.getPhysicsComponent().clearForces();
      physical.getPhysicsComponent().clearTorques();
    }
  }

  public void applyPhysics(double elapsedTime) {

    // Add the time that has not been computed during the last frame
    elapsedTime += subTickAccumulator;

    // Execute as many ticks as necessary to cover the elapsed time
    while (elapsedTime > 1f / TPS) {
      elapsedTime -= 1f / TPS;
      quadTree.clear();
      quadTree.addAll(registered);
      for (Physical physical : registered) {
        PhysicsComponent component = physical.getPhysicsComponent();

        component.resolveForces();
        component.update(1f / TPS);

        physical.getTransform().move(component.getVelocity());
        physical.getTransform().rotate(component.getAngularVelocity());
        physical.getTransform().commit();
      }

      // TODO Process collisions
    }

    // Keep track of the time that is shorter than a tick to process it next call
    subTickAccumulator = elapsedTime;
  }

  public QuadTree<Physical> getQuadTree() {
    return quadTree;
  }
}
