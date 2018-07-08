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
package org.terasology.flexiblemovement.node;

import org.terasology.flexiblemovement.FlexibleMovementComponent;
import org.terasology.logic.behavior.BehaviorAction;
import org.terasology.logic.behavior.core.Actor;
import org.terasology.logic.behavior.core.BaseAction;
import org.terasology.logic.behavior.core.BehaviorState;

/**
 * Performs a child node along the FlexibleMovementComponent.path
 * <p>
 * 1. Sets the FlexibleMovementComponent.target
 * 2. Runs the child node until SUCCESS/FAILURE
 * 3. On child SUCCESS, sets target to next waypoint and starts child again
 * 4. On child FAILURE, returns FAILURE
 * 5. When end of path is reached, returns SUCCESS
 */
@BehaviorAction(name = "move_along_path")
public class MoveAlongPathNode extends BaseAction {

    @Override
    public BehaviorState modify(Actor actor, BehaviorState result) {
        FlexibleMovementComponent movement = actor.getComponent(FlexibleMovementComponent.class);
        if (result == BehaviorState.SUCCESS) {
            movement.advancePath();
            if (movement.isPathFinished()) {
                movement.resetPath();
                return BehaviorState.SUCCESS;
            }
        }

        actor.save(movement);

        return BehaviorState.RUNNING;
    }
}
