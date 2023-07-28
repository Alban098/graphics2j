/*
 * Copyright (c) 2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package org.alban098.physics2j;

import java.util.HashMap;
import java.util.Map;
import org.alban098.common.Component;
import org.joml.Vector2f;

public class PhysicsComponent extends Component {

  private final Map<Vector2f, Force> forces = new HashMap<>();
  private final Torque torque = new Torque(0);
  private final Vector2f velocity = new Vector2f();
  private final Vector2f acceleration = new Vector2f();
  private float angularVelocity = 0;
  private float angularAcceleration = 0;
  private float mass;
  private final Polygon hitbox;

  public PhysicsComponent(float mass, Vector2f scale) {
    super();
    this.mass = mass;
    this.hitbox = new Polygon();
    hitbox.addPoint(-scale.x / 2, -scale.y / 2);
    hitbox.addPoint(scale.x / 2, -scale.y / 2);
    hitbox.addPoint(-scale.x / 2, scale.y / 2);
    hitbox.addPoint(scale.x / 2, scale.y / 2);
  }

  public void update(double deltaTime) {
    this.velocity.add(
        (float) (this.acceleration.x * deltaTime), (float) (this.acceleration.y * deltaTime));
    this.angularVelocity += this.angularAcceleration * deltaTime;
  }

  public void resolveForces() {
    // Compute the effect of all forces, and resulting torques
    this.acceleration.set(0);
    for (Map.Entry<Vector2f, Force> force : forces.entrySet()) {
      applyTorque(force.getValue().applyTo(this, force.getKey()));
    }

    // Applying the effect of all torques
    this.angularAcceleration = 0;
    torque.applyTo(this);
  }

  public void applyForce(Force force, Vector2f offset) {
    if (forces.containsKey(offset)) {
      forces.get(offset).combine(force);
    } else {
      this.forces.put(offset, force);
    }
  }

  public void applyTorque(float torque) {
    this.torque.add(torque);
  }

  public void clearForces() {
    forces.clear();
  }

  public void clearTorques() {
    torque.set(0);
  }

  public Vector2f getVelocity() {
    return velocity;
  }

  public void setVelocity(float x, float y) {
    this.velocity.set(x, y);
  }

  public Vector2f getAcceleration() {
    return acceleration;
  }

  public void setAcceleration(float x, float y) {
    this.acceleration.set(x, y);
  }

  public float getAngularVelocity() {
    return angularVelocity;
  }

  public void setAngularVelocity(float angularVelocity) {
    this.angularVelocity = angularVelocity;
  }

  public float getAngularAcceleration() {
    return angularAcceleration;
  }

  public void setAngularAcceleration(float angularAcceleration) {
    this.angularAcceleration = angularAcceleration;
  }

  public float getMass() {
    return mass;
  }

  public void setMass(float mass) {
    this.mass = mass;
  }

  public Polygon getHitbox() {
    return hitbox;
  }

  public Vector2f getCenterOfMass() {
    return getHitbox().getCenter();
  }

  public float getMomentOfInertia() {
    return getHitbox().getMomentOfInertia(getMass());
  }

  public boolean isSubjectToStaticForces() {
    return true;
  }
}
