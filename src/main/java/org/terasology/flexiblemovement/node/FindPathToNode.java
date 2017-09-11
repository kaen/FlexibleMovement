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
import org.terasology.flexiblemovement.FlexibleMovementHelper;
import org.terasology.flexiblemovement.system.PluginSystem;
import org.terasology.flexiblepathfinding.JPSConfig;
import org.terasology.flexiblepathfinding.PathfinderCallback;
import org.terasology.flexiblepathfinding.PathfinderSystem;
import org.terasology.logic.behavior.tree.Node;
import org.terasology.logic.behavior.tree.Status;
import org.terasology.logic.behavior.tree.Task;
import org.terasology.logic.characters.CharacterMovementComponent;
import org.terasology.logic.location.LocationComponent;
import org.terasology.math.geom.Vector3i;
import org.terasology.registry.In;

import java.math.RoundingMode;
import java.util.List;

/**
 * Finds a path to the pathGoalPosition of the Actor, stores it in FlexibileMovementComponent.path
 * SUCCESS: When the pathfinder returns a valid path
 * FAILURE: When the pathfinder returns a failure or invalid path
 */
public class FindPathToNode extends Node {
    @Override
    public FindPathToTask createTask() {
        return new FindPathToTask(this);
    }

    public class FindPathToTask extends Task {
        Status pathStatus = null;
        List<Vector3i> pathResult = null;
        @In
        PathfinderSystem pathfinderSystem;

        @In
        PluginSystem pluginSystem;

        protected FindPathToTask(Node node) {
            super(node);
        }

        @Override
        public Status update(float dt) {
            if(pathStatus == null) {
                pathStatus = Status.RUNNING;
                FlexibleMovementComponent flexibleMovementComponent = actor().getComponent(FlexibleMovementComponent.class);
                CharacterMovementComponent characterMovementComponent = actor().getComponent(CharacterMovementComponent.class);
                Vector3i start = FlexibleMovementHelper.posToBlock(actor().getComponent(LocationComponent.class).getWorldPosition());
                Vector3i goal = actor().getComponent(FlexibleMovementComponent.class).getPathGoal();

                if (start == null || goal == null) {
                    return Status.FAILURE;
                }

                JPSConfig config = new JPSConfig(start, goal);
                config.useLineOfSight = false;
                config.maxTime = 0.25f;
                config.maxDepth = 150;
                config.goalDistance = flexibleMovementComponent.pathGoalDistance;
                config.plugin = pluginSystem.getMovementPlugin(actor().getEntity()).getJpsPlugin(actor().getEntity());

                pathfinderSystem.requestPath(config, new PathfinderCallback() {
                    @Override
                    public void pathReady(List<Vector3i> path, Vector3i target) {
                        if(path == null || path.size() == 0) {
                            pathStatus = Status.FAILURE;
                            return;
                        }
                        pathStatus = Status.SUCCESS;
                        pathResult = path;
                    }
                });
            }

            if(pathStatus == Status.SUCCESS) {
                FlexibleMovementComponent movement = actor().getComponent(FlexibleMovementComponent.class);

                // PF System returns paths including the starting point.
                // Since we don't need to move to where we started, we remove the first point in the path
                pathResult.remove(0);

                movement.setPath(pathResult);
                actor().save(movement);
            }

            return pathStatus;
        }

        @Override
        public void handle(Status result) {

        }
    }
}
