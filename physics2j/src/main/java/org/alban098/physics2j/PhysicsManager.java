/*
 * Copyright (c) 2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package org.alban098.physics2j;

import java.util.Collection;
import java.util.HashSet;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PhysicsManager {

  /** Just a Logger to log events */
  private static final Logger LOGGER = LoggerFactory.getLogger(PhysicsManager.class);

  /** A Collection of all Movable physics should be applied to */
  private final Collection<Physical> registered = new HashSet<>();

  private final QuadTree<Physical> quadTree;

  private final Force staticForce = new Force(0, 0);

  // TODO Create spatial data structure to improve search efficiency

  private long physicsTimeNs = 0;
  private long collisionCandidateChecked = 0;
  private int collisionCount = 0;

  public PhysicsManager(Force... staticForces) {
    for (Force force : staticForces) {
      staticForce.combine(force);
    }
    quadTree = new QuadTree<>(new Vector2f(500f, 500f));
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

  public void applyPhysics(double deltaTime) {
    physicsTimeNs = System.nanoTime();
    quadTree.clear();
    quadTree.addAll(registered);
    for (Physical physical : registered) {
      PhysicsComponent component = physical.getPhysicsComponent();

      component.resolveForces();
      component.update(deltaTime);

      physical.getTransform().move(component.getVelocity());
      physical.getTransform().rotate(component.getAngularVelocity());
      physical.getTransform().commit();
    }

    // TODO Process collisions

    physicsTimeNs = System.nanoTime() - physicsTimeNs;
    // quadTree.size();
  }

  public void processCollision(CollisionResult collision) {
    PhysicsComponent componentA = collision.objectA.getPhysicsComponent();
    PhysicsComponent componentB = collision.objectB.getPhysicsComponent();

    Vector2f rap = new Vector2f(collision.contactPoint).sub(componentA.getCenterOfMass());
    Vector2f rbp = new Vector2f(collision.contactPoint).sub(componentB.getCenterOfMass());

    Vector2f va1 = new Vector2f(componentA.getVelocity());
    Vector2f vb1 = new Vector2f(componentB.getVelocity());

    Vector3f vap1 =
        new Vector3f(rap.x, rap.y, 0).cross(new Vector3f(0, 0, componentA.getAngularVelocity()));
    Vector3f vbp1 =
        new Vector3f(rbp.x, rbp.y, 0).cross(new Vector3f(0, 0, componentA.getAngularVelocity()));

    Vector3f vp1 = new Vector3f(vap1).sub(vbp1);

    Vector2f n = new Vector2f();

    int elasticity = 1;
    float ia = componentA.getMomentOfInertia();
    float ib = componentB.getMomentOfInertia();
    Vector3f rapXn = new Vector3f(rap.x, rap.y, 0).cross(n.x, n.y, 0);
    Vector3f rbpXn = new Vector3f(rbp.x, rbp.y, 0).cross(n.x, n.y, 0);
    float j =
        (new Vector2f(vp1.x, vp1.y).mul(1 + elasticity).dot(n))
            / (1 / componentA.getMass()
                + 1 / componentB.getMass()
                + new Vector3f(rapXn).dot(rapXn) / ia
                + new Vector3f(rbpXn).dot(rbpXn) / ib);

    Vector2f va2 = new Vector2f(va1).add(new Vector2f(n).mul(j).div(componentA.getMass()));
    Vector2f vb2 = new Vector2f(vb1).sub(new Vector2f(n).mul(j).div(componentB.getMass()));

    float wa2 =
        new Vector3f(0, 0, componentA.getAngularVelocity())
            .add(new Vector3f(rap.x, rap.y, 0).cross(new Vector3f(n.x, n.y, 0).mul(j)).div(ia))
            .z;
    float wb2 =
        new Vector3f(0, 0, componentB.getAngularVelocity())
            .add(new Vector3f(rbp.x, rbp.y, 0).cross(new Vector3f(n.x, n.y, 0).mul(j)).div(ib))
            .z;
    componentA.setVelocity(va2.x, va2.y);
    componentA.setAngularVelocity(wa2);

    componentB.setVelocity(vb2.x, vb2.y);
    componentB.setAngularVelocity(wb2);

    collision.objectA.collisionCallback(collision.objectB, collision.contactPoint);
    collision.objectB.collisionCallback(collision.objectA, collision.contactPoint);
  }

  public QuadTree<Physical> getQuadTree() {
    return quadTree;
  }
}
