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
import org.terasology.flexiblepathfinding.plugins.basic.LeapingPlugin;
import org.terasology.flexiblepathfinding.plugins.basic.WalkingPlugin;
import org.terasology.logic.characters.CharacterMoveInputEvent;
import org.terasology.logic.characters.CharacterMovementComponent;
import org.terasology.logic.characters.MovementMode;
import org.terasology.logic.characters.events.SetMovementModeEvent;
import org.terasology.logic.location.LocationComponent;
import org.terasology.math.TeraMath;
import org.terasology.math.geom.Vector3f;
import org.terasology.world.WorldProvider;

public class LeapingMovementPlugin extends MovementPlugin {
    public LeapingMovementPlugin(WorldProvider world, Time time) {
        super(world, time);
    }
    public LeapingMovementPlugin() {
        super();
    }

    @Override
    public JPSPlugin getJpsPlugin(EntityRef entity) {
        CharacterMovementComponent component = entity.getComponent(CharacterMovementComponent.class);
        return new LeapingPlugin(getWorld(), component.radius * 2.0f, component.height);    }

    @Override
    public CharacterMoveInputEvent move(EntityRef entity, Vector3f dest, int sequence) {
        Vector3f delta = getDelta(entity, dest);
        float yaw = getYaw(delta);
        long dt = getTime().getGameDeltaInMs();

        CharacterMovementComponent movement = entity.getComponent(CharacterMovementComponent.class);
        return new CharacterMoveInputEvent(sequence, 0, yaw, delta, false, true, dt);
    }
}
