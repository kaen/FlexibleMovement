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
package org.terasology.flexiblemovement;

import org.terasology.engine.Time;
import org.terasology.flexiblepathfinding.JPSConfig;
import org.terasology.flexiblepathfinding.PathfinderCallback;
import org.terasology.flexiblepathfinding.PathfinderSystem;
import org.terasology.flexiblepathfinding.plugins.StandardPlugin;
import org.terasology.logic.behavior.tree.Node;
import org.terasology.logic.behavior.tree.Status;
import org.terasology.logic.behavior.tree.Task;
import org.terasology.logic.location.LocationComponent;
import org.terasology.math.geom.Vector3i;
import org.terasology.registry.In;
import org.terasology.world.WorldProvider;

import java.util.List;

public class ValidatePathNode extends Node {

    @Override
    public ValidatePathNode.ValidatePathTask createTask() {
        return new ValidatePathNode.ValidatePathTask(this);
    }

    public class ValidatePathTask extends Task{
        Status pathStatus = null;
        List<Vector3i> pathResult = null;
        @In
        PathfinderSystem system;

        @In
        private WorldProvider world;

        @In
        private Time time;

        protected ValidatePathTask(Node node) {
            super(node);
        }

        @Override
        public Status update(float dt) {
            FlexibleMovementComponent flexibleMovementComponent = actor().getComponent(FlexibleMovementComponent.class);
            StandardPlugin pathfindingPlugin = flexibleMovementComponent.getMovementPlugin(world, time).getPathfindingPlugin(actor().getEntity());
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
