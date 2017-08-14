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
import org.terasology.logic.behavior.tree.Node;
import org.terasology.logic.behavior.tree.Status;
import org.terasology.logic.behavior.tree.Task;
import org.terasology.math.geom.Vector3i;
import org.terasology.registry.In;

import java.util.List;

/**
 * Validates the entity's current path for walkability (according to the pathfinding plugin its using)
 * SUCCESS: when there are no unwalkable waypoints
 * FAILURE: otherwise
 */
public class ValidatePathNode extends Node {

    @Override
    public ValidatePathNode.ValidatePathTask createTask() {
        return new ValidatePathNode.ValidatePathTask(this);
    }

    public class ValidatePathTask extends Task{
        Status pathStatus = null;
        List<Vector3i> pathResult = null;
        @In private PathfinderSystem system;
        @In private PluginSystem pluginSystem;
        @In private FlexibleMovementSystem flexibleMovementSystem;

        protected ValidatePathTask(Node node) {
            super(node);
        }

        @Override
        public Status update(float dt) {
            FlexibleMovementComponent flexibleMovementComponent = actor().getComponent(FlexibleMovementComponent.class);
            JPSPlugin pathfindingPlugin = pluginSystem.getMovementPlugin(actor().getEntity()).getJpsPlugin(actor().getEntity());
            if(flexibleMovementComponent == null || pathfindingPlugin == null) {
                return Status.FAILURE;
            }

            for(Vector3i pos : actor().getComponent(FlexibleMovementComponent.class).getPath()) {
                if(!pathfindingPlugin.isWalkable(pos)) {
                    return Status.FAILURE;
                }
            }
            return Status.SUCCESS;
        }

        @Override
        public void handle(Status result) {

        }
    }
}
