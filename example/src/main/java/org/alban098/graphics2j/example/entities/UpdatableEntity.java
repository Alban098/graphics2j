package org.alban098.graphics2j.example.entities;

import org.alban098.graphics2j.entities.Entity;

public interface UpdatableEntity extends Entity {

    void update(double elapsedTime);
}
