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
package org.terasology.flexiblemovement.action;

import org.terasology.flexiblemovement.FlexibleMovementComponent;
import org.terasology.logic.behavior.BehaviorAction;
import org.terasology.logic.behavior.core.Actor;
import org.terasology.logic.behavior.core.BaseAction;
import org.terasology.logic.behavior.core.BehaviorState;
import org.terasology.logic.characters.CharacterMoveInputEvent;
import org.terasology.logic.location.LocationComponent;
import org.terasology.math.geom.Vector3f;
import org.terasology.math.geom.Vector3i;

/**
 * Performs a child node along the FlexibleMovementComponent.path
 * <p>
 * 1. Sets the FlexibleMovementComponent.target
 * 2. Runs the child node until SUCCESS/FAILURE
 * 3. On child SUCCESS, sets target to next waypoint and starts child again
 * 4. On child FAILURE, returns FAILURE
 * 5. When end of path is reached, returns SUCCESS
 */
@BehaviorAction(name = "flexible_movement_move_along_path", isDecorator = true)
public class MoveAlongPathAction extends BaseAction {

    @Override
    public BehaviorState modify(Actor actor, BehaviorState childResult) {
        FlexibleMovementComponent movement = actor.getComponent(FlexibleMovementComponent.class);
        LocationComponent location = actor.getComponent(LocationComponent.class);
        BehaviorState result = BehaviorState.RUNNING;

        if (movement.getPath() == null) {
            return BehaviorState.FAILURE;
        }

        if (childResult == BehaviorState.SUCCESS) {
            movement.advancePath();
            if (movement.isPathFinished()) {
                movement.resetPath();
                result = BehaviorState.SUCCESS;
            }
        } else if (childResult == BehaviorState.FAILURE) {
            movement.resetPath();
            result = BehaviorState.FAILURE;
        }

        actor.save(movement);
        if (result != BehaviorState.RUNNING) {
            // stop moving if we're done with the path
            actor.getEntity().send(new CharacterMoveInputEvent(movement.sequenceNumber++, 0f, 0f, new Vector3f(0, 0, 0), false, false, false, (long) (actor.getDelta() * 1000)));
        }

        return result;
    }
}
