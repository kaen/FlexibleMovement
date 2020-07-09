/*
 * Copyright 2017 MovingBlocks
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
import org.terasology.flexiblepathfinding.plugins.basic.FlyingPlugin;
import org.terasology.flexiblepathfinding.plugins.basic.SwimmingPlugin;
import org.terasology.logic.characters.CharacterMoveInputEvent;
import org.terasology.logic.characters.CharacterMovementComponent;
import org.terasology.logic.characters.MovementMode;
import org.terasology.logic.characters.events.SetMovementModeEvent;
import org.terasology.math.TeraMath;
import org.terasology.math.geom.Vector3f;
import org.terasology.world.WorldProvider;

public class SwimmingMovementPlugin extends MovementPlugin {
    public SwimmingMovementPlugin(WorldProvider world, Time time) {
        super(world, time);
    }
    public SwimmingMovementPlugin() {
        super();
    }

    @Override
    public JPSPlugin getJpsPlugin(EntityRef entity) {
        CharacterMovementComponent component = entity.getComponent(CharacterMovementComponent.class);
        return new SwimmingPlugin(getWorld(), component.radius * 2.0f, component.height);
    }

    @Override
    public CharacterMoveInputEvent move(EntityRef entity, Vector3f dest, int sequence, long deltaMs) {
        Vector3f delta = getDelta(entity, dest);
        float yaw = getYaw(delta);
        float pitch = getPitch(delta);

        CharacterMovementComponent movement = entity.getComponent(CharacterMovementComponent.class);
        return new CharacterMoveInputEvent(sequence, pitch, yaw, delta, false, false, movement.grounded, deltaMs);
    }

    private float getPitch(Vector3f delta) {
        return ((float) Math.atan2(delta.y, Math.hypot(delta.x, delta.z))) * TeraMath.RAD_TO_DEG + 180;
    }
}
