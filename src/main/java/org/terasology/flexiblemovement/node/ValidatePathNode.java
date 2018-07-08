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
import org.terasology.flexiblemovement.system.FlexibleMovementSystem;
import org.terasology.flexiblemovement.system.PluginSystem;
import org.terasology.flexiblepathfinding.PathfinderSystem;
import org.terasology.flexiblepathfinding.plugins.JPSPlugin;
import org.terasology.logic.behavior.BehaviorAction;
import org.terasology.logic.behavior.core.Actor;
import org.terasology.logic.behavior.core.BaseAction;
import org.terasology.logic.behavior.core.BehaviorState;
import org.terasology.registry.In;

/**
 * Validates the entity's current path for walkability (according to the pathfinding plugin its using)
 * SUCCESS: when there are no unwalkable waypoints
 * FAILURE: otherwise
 */
@BehaviorAction(name = "validate_path")
public class ValidatePathNode extends BaseAction {
    @In
    private PathfinderSystem system;
    @In
    private PluginSystem pluginSystem;
    @In
    private FlexibleMovementSystem flexibleMovementSystem;

    @Override
    public BehaviorState modify(Actor actor, BehaviorState result) {

        FlexibleMovementComponent flexibleMovementComponent = actor.getComponent(FlexibleMovementComponent.class);
        JPSPlugin pathfindingPlugin = pluginSystem.getMovementPlugin(actor.getEntity()).getJpsPlugin(actor.getEntity());
        if (flexibleMovementComponent == null || pathfindingPlugin == null) {
            return BehaviorState.FAILURE;
        }

//            for(Vector3i pos : actor().getComponent(FlexibleMovementComponent.class).getPath()) {
//                if(!pathfindingPlugin.isWalkable(pos)) {
//                    return Status.FAILURE;
//                }
//            }
        return BehaviorState.SUCCESS;
    }
}
