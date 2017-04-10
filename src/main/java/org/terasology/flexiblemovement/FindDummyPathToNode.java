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

import com.google.api.client.util.Lists;
import org.terasology.engine.Time;
import org.terasology.flexiblepathfinding.JPSConfig;
import org.terasology.flexiblepathfinding.PathfinderCallback;
import org.terasology.flexiblepathfinding.PathfinderSystem;
import org.terasology.logic.behavior.tree.Node;
import org.terasology.logic.behavior.tree.Status;
import org.terasology.logic.behavior.tree.Task;
import org.terasology.logic.location.LocationComponent;
import org.terasology.math.geom.Vector3i;
import org.terasology.registry.In;
import org.terasology.world.WorldProvider;

import java.util.Arrays;
import java.util.List;

public class FindDummyPathToNode extends Node {
    @Override
    public FindPathToTask createTask() {
        return new FindPathToTask(this);
    }

    public static class FindPathToTask extends Task{
        @In
        PathfinderSystem system;

        @In
        private WorldProvider world;

        protected FindPathToTask(Node node) {
            super(node);
        }

        @Override
        public Status update(float dt) {

            FlexibleMovementComponent movement = actor().getComponent(FlexibleMovementComponent.class);
            Vector3i start = new Vector3i(actor().getComponent(LocationComponent.class).getWorldPosition());
            Vector3i goal = actor().getComponent(FlexibleMovementComponent.class).pathTarget;
            movement.path = Arrays.asList(start, goal);
            movement.pathIndex = 0;
            actor().save(movement);

            return Status.SUCCESS;
        }

        @Override
        public void handle(Status result) {

        }
    }
}
