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

import org.terasology.context.Context;
import org.terasology.flexiblemovement.FlexibleMovementComponent;
import org.terasology.flexiblemovement.FlexibleMovementHelper;
import org.terasology.flexiblemovement.system.PluginSystem;
import org.terasology.flexiblepathfinding.JPSConfig;
import org.terasology.flexiblepathfinding.PathfinderCallback;
import org.terasology.flexiblepathfinding.PathfinderSystem;
import org.terasology.logic.behavior.BehaviorAction;
import org.terasology.logic.behavior.core.Actor;
import org.terasology.logic.behavior.core.BaseAction;
import org.terasology.logic.behavior.core.BehaviorState;
import org.terasology.logic.location.LocationComponent;
import org.terasology.math.geom.Vector3i;
import org.terasology.registry.In;

import java.util.List;

/**
 * Finds a path to the pathGoalPosition of the Actor, stores it in FlexibileMovementComponent.path
 * SUCCESS: When the pathfinder returns a valid path
 * FAILURE: When the pathfinder returns a failure or invalid path
 */
@BehaviorAction(name = "find_path_to")
public class FindPathToNode extends BaseAction {
    private BehaviorState pathStatus = null;
    private List<Vector3i> pathResult = null;

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
    public BehaviorState modify(Actor actor, BehaviorState result) {
        if (pathStatus == null) {
            pathStatus = BehaviorState.RUNNING;
            FlexibleMovementComponent flexibleMovementComponent = actor.getComponent(FlexibleMovementComponent.class);
            Vector3i start = FlexibleMovementHelper.posToBlock(actor.getComponent(LocationComponent.class).getWorldPosition());
            Vector3i goal = flexibleMovementComponent.getPathGoal();

            if (start == null || goal == null) {
                return BehaviorState.FAILURE;
            }

            JPSConfig config = new JPSConfig(start, goal);
            config.useLineOfSight = false;
            config.maxTime = 10f;
            config.maxDepth = 150;
            config.goalDistance = flexibleMovementComponent.pathGoalDistance;
            config.plugin = pluginSystem.getMovementPlugin(actor.getEntity()).getJpsPlugin(actor.getEntity());
            config.requester = actor.getEntity();

            pathfinderSystem.requestPath(config, (path, target) -> {
                if (path == null || path.size() == 0) {
                    pathStatus = BehaviorState.FAILURE;
                    return;
                }
                pathStatus = BehaviorState.SUCCESS;
                pathResult = path;
            });
        }

        if (pathStatus == BehaviorState.SUCCESS) {
            FlexibleMovementComponent movement = actor.getComponent(FlexibleMovementComponent.class);

            // PF System returns paths including the starting point.
            // Since we don't need to move to where we started, we remove the first point in the path
            pathResult.remove(0);

            movement.setPath(pathResult);
            actor.save(movement);
        }

        return pathStatus;
    }
}
