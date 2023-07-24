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
  private final Collection<Movable> registered = new HashSet<>();

  private final Force staticForce = new Force(0, 0);

  // TODO Create spatial data structure to improve search efficiency

  private long physicsTimeNs = 0;
  private long collisionCandidateChecked = 0;
  private int collisionCount = 0;

  public PhysicsManager(Force... staticForces) {
    for (Force force : staticForces) {
      staticForce.combine(force);
    }
  }

  public void track(Movable movable) {
    registered.add(movable);
  }

  public void unregister(Movable movable) {
    registered.remove(movable);
  }

  public void applyPhysics(double deltaTime) {
    Vector2f buf2d = new Vector2f();
    for (Movable movable : registered) {

      if (movable.isSubjectToStaticForces()) {
        staticForce.applyTo(movable);
      }

      buf2d.set(movable.getAcceleration());
      buf2d.mul((float) deltaTime);
      movable.getVelocity().add(buf2d);
      buf2d.set(movable.getVelocity());
      buf2d.mul((float) deltaTime);
      movable.getTransform().move(buf2d);

      if (movable.isSubjectToStaticForces()) {
        staticForce.stopApplyingTo(movable);
      }

      movable.setAngularVelocity(
          (float) (movable.getAngularVelocity() + movable.getAngularAcceleration() * deltaTime));
      movable.getTransform().move(movable.getVelocity());
      movable.getTransform().rotate(movable.getAngularVelocity());
      movable.getTransform().commit();
    }
  }

  public void processCollision(CollisionResult collision) {
    Vector2f rap = new Vector2f(collision.contactPoint).sub(collision.object1.getCenterOfMass());
    Vector2f rbp = new Vector2f(collision.contactPoint).sub(collision.object2.getCenterOfMass());

    Vector2f va1 = new Vector2f(collision.object1.getVelocity());
    Vector2f vb1 = new Vector2f(collision.object2.getVelocity());

    Vector3f vap1 =
        new Vector3f(rap.x, rap.y, 0)
            .cross(new Vector3f(0, 0, collision.object1.getAngularVelocity()));
    Vector3f vbp1 =
        new Vector3f(rbp.x, rbp.y, 0)
            .cross(new Vector3f(0, 0, collision.object1.getAngularVelocity()));

    Vector3f vp1 = new Vector3f(vap1).sub(vbp1);

    Vector2f n = new Vector2f();

    int elasticity = 1;
    float ia = collision.object1.getMomentOfInertia();
    float ib = collision.object2.getMomentOfInertia();
    Vector3f rapXn = new Vector3f(rap.x, rap.y, 0).cross(n.x, n.y, 0);
    Vector3f rbpXn = new Vector3f(rbp.x, rbp.y, 0).cross(n.x, n.y, 0);
    float j =
        (new Vector2f(vp1.x, vp1.y).mul(1 + elasticity).dot(n))
            / (1 / collision.object1.getMass()
                + 1 / collision.object2.getMass()
                + new Vector3f(rapXn).dot(rapXn) / ia
                + new Vector3f(rbpXn).dot(rbpXn) / ib);

    Vector2f va2 = new Vector2f(va1).add(new Vector2f(n).mul(j).div(collision.object1.getMass()));
    Vector2f vb2 = new Vector2f(vb1).sub(new Vector2f(n).mul(j).div(collision.object2.getMass()));

    float wa2 =
        new Vector3f(0, 0, collision.object1.getAngularVelocity())
            .add(new Vector3f(rap.x, rap.y, 0).cross(new Vector3f(n.x, n.y, 0).mul(j)).div(ia))
            .z;
    float wb2 =
        new Vector3f(0, 0, collision.object2.getAngularVelocity())
            .add(new Vector3f(rbp.x, rbp.y, 0).cross(new Vector3f(n.x, n.y, 0).mul(j)).div(ib))
            .z;
    collision.object1.setVelocity(va2);
    collision.object1.setAngularVelocity(wa2);

    collision.object2.setVelocity(vb2);
    collision.object2.setAngularVelocity(wb2);
  }
}
