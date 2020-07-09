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

import org.terasology.context.Context;
import org.terasology.flexiblemovement.FlexibleMovementComponent;
import org.terasology.flexiblemovement.FlexibleMovementHelper;
import org.terasology.flexiblemovement.PathStatus;
import org.terasology.flexiblemovement.system.PluginSystem;
import org.terasology.flexiblepathfinding.JPSConfig;
import org.terasology.flexiblepathfinding.PathfinderSystem;
import org.terasology.logic.behavior.BehaviorAction;
import org.terasology.logic.behavior.core.Actor;
import org.terasology.logic.behavior.core.BaseAction;
import org.terasology.logic.behavior.core.BehaviorState;
import org.terasology.logic.location.LocationComponent;
import org.terasology.math.geom.Vector3i;
import org.terasology.registry.In;

/**
 * Finds a path to the pathGoalPosition of the Actor, stores it in FlexibileMovementComponent.path
 * SUCCESS: When the pathfinder returns a valid path
 * FAILURE: When the pathfinder returns a failure or invalid path
 */
@BehaviorAction(name = "flexible_movement_find_path_to")
public class FindPathAction extends BaseAction {
    @In
    private Context context;

    private PathfinderSystem pathfinderSystem;
    private PluginSystem pluginSystem;

    @Override
    public void construct(Actor actor) {
        pluginSystem = context.get(PluginSystem.class);
        pathfinderSystem = context.get(PathfinderSystem.class);
    }

    @Override
    public BehaviorState modify(Actor actor, BehaviorState unusedState) {
        FlexibleMovementComponent flexibleMovementComponent = actor.getComponent(FlexibleMovementComponent.class);
        if (flexibleMovementComponent == null) {
            return BehaviorState.FAILURE;
        }

        // handle non-idle path finding statuses
        switch (flexibleMovementComponent.pathStatus) {
            case SUCCESS:
                flexibleMovementComponent.pathStatus = PathStatus.IDLE;
                actor.save(flexibleMovementComponent);
                return BehaviorState.SUCCESS;
            case FAILURE:
                flexibleMovementComponent.pathStatus = PathStatus.IDLE;
                actor.save(flexibleMovementComponent);
                return BehaviorState.FAILURE;
            case RUNNING:
                return BehaviorState.RUNNING;
        }

        // path status was idle, request a new path
        Vector3i start = FlexibleMovementHelper.posToBlock(actor.getComponent(LocationComponent.class).getWorldPosition());
        Vector3i goal = flexibleMovementComponent.getPathGoal();

        if (goal == start) {
            flexibleMovementComponent.resetPath();
            actor.getEntity().saveComponent(flexibleMovementComponent);
            return BehaviorState.SUCCESS;
        }

        if (goal == null) {
            return BehaviorState.FAILURE;
        }

        flexibleMovementComponent.pathStatus = PathStatus.RUNNING;
        actor.save(flexibleMovementComponent);

        JPSConfig config = new JPSConfig(start, goal);
        config.useLineOfSight = false;
        config.maxTime = 0.250f;
        config.maxDepth = 150;
        config.goalDistance = flexibleMovementComponent.pathGoalDistance;
        config.plugin = pluginSystem.getMovementPlugin(actor.getEntity()).getJpsPlugin(actor.getEntity());
        config.requester = actor.getEntity();

        pathfinderSystem.requestPath(config, (path, target) -> {
            if (path == null || path.size() == 0) {
                flexibleMovementComponent.resetPath();
                flexibleMovementComponent.pathStatus = PathStatus.FAILURE;
                actor.save(flexibleMovementComponent);
                return;
            }

            flexibleMovementComponent.setPath(path);
            flexibleMovementComponent.pathStatus = PathStatus.SUCCESS;

            // PF System returns paths including the starting point.
            // Since we don't need to move to where we started, we remove the first point in the path
            path.remove(0);

            actor.save(flexibleMovementComponent);
        });

        actor.save(flexibleMovementComponent);
        return BehaviorState.RUNNING;
    }
}
