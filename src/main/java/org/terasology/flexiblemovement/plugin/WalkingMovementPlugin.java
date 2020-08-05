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
import org.terasology.flexiblemovement.FlexibleMovementComponent;
import org.terasology.flexiblepathfinding.plugins.JPSPlugin;
import org.terasology.flexiblepathfinding.plugins.basic.FlyingPlugin;
import org.terasology.flexiblepathfinding.plugins.basic.WalkingPlugin;
import org.terasology.logic.characters.CharacterMoveInputEvent;
import org.terasology.logic.characters.CharacterMovementComponent;
import org.terasology.logic.characters.MovementMode;
import org.terasology.logic.characters.events.SetMovementModeEvent;
import org.terasology.logic.location.LocationComponent;
import org.terasology.math.TeraMath;
import org.terasology.math.geom.Quat4f;
import org.terasology.math.geom.Vector3f;
import org.terasology.math.geom.Vector3i;
import org.terasology.world.WorldProvider;
import org.terasology.world.block.Block;

import java.math.RoundingMode;

public class WalkingMovementPlugin extends MovementPlugin {
    public WalkingMovementPlugin(WorldProvider world, Time time) {
        super(world, time);
    }
    public WalkingMovementPlugin() {
        super();
    }

    @Override
    public JPSPlugin getJpsPlugin(EntityRef entity) {
        CharacterMovementComponent component = entity.getComponent(CharacterMovementComponent.class);
        return new WalkingPlugin(getWorld(), component.radius * 2.0f, component.height);
    }

    @Override
    public CharacterMoveInputEvent move(EntityRef entity, Vector3f dest, int sequence, long deltaMs) {
        CharacterMovementComponent movement = entity.getComponent(CharacterMovementComponent.class);
        Vector3f delta = getDelta(entity, dest);
        float yaw = getYaw(delta);

        // walking is horizontal-only
        delta.y = 0;
        delta.normalize();

        // In the climbing state movement is relative to the direction of the ladder
        if (movement.mode == MovementMode.CLIMBING) {
            Vector3i pos = new Vector3i(entity.getComponent(LocationComponent.class).getWorldPosition(), RoundingMode.HALF_UP);
            Block block = getWorld().getBlock(pos);
            Vector3f ladderDirection = block.getDirection().toDirection().getVector3f();
            Quat4f rotation = new Quat4f(delta, 0);
            rotation.inverse();
            rotation.rotate(delta);
            yaw = rotation.getYaw() * TeraMath.RAD_TO_DEG;
        }

        return new CharacterMoveInputEvent(sequence, 0, yaw, delta, false, false, false, deltaMs);
    }
}
