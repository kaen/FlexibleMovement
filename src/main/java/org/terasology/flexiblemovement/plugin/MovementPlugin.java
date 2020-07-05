/*
 * Copyright 2016 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.terasology.flexiblemovement.plugin;

import org.terasology.engine.Time;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.flexiblepathfinding.plugins.JPSPlugin;
import org.terasology.logic.characters.CharacterMoveInputEvent;
import org.terasology.logic.characters.CharacterMovementComponent;
import org.terasology.logic.location.LocationComponent;
import org.terasology.math.TeraMath;
import org.terasology.math.geom.Vector3f;
import org.terasology.world.WorldProvider;

public abstract class MovementPlugin {
    private WorldProvider world;
    private Time time;

    public MovementPlugin(WorldProvider world, Time time) {
        this.time = time;
        this.world = world;
    }

    // needed to instantiate plugins by name in the movement system
    public MovementPlugin() {
        //do nothing
    }

    public static String getName() {
        return "movement";
    }

    public abstract JPSPlugin getJpsPlugin(EntityRef entity);
    public abstract CharacterMoveInputEvent move(EntityRef entity, Vector3f dest, int sequence);

    public WorldProvider getWorld() {
        return world;
    }

    public void setWorld(WorldProvider world) {
        this.world = world;
    }

    public Time getTime() {
        return time;
    }

    public void setTime(Time time) {
        this.time = time;
    }

    public float getYaw(Vector3f delta) {
        return ((float) Math.atan2(delta.x, delta.z)) * TeraMath.RAD_TO_DEG + 180.0f;
    }

    public Vector3f getDelta(EntityRef entity, Vector3f dest) {
        if (getTime().getGameDelta() == 0.0f) {
            return Vector3f.zero();
        }

        LocationComponent location = entity.getComponent(LocationComponent.class);
        CharacterMovementComponent movement = entity.getComponent(CharacterMovementComponent.class);

        Vector3f delta = new Vector3f(dest).sub(location.getWorldPosition());
        delta.div(movement.speedMultiplier).div(getTime().getGameDelta());
        return delta;
    }
}
