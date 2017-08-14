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
import org.terasology.logic.behavior.tree.DecoratorNode;
import org.terasology.logic.behavior.tree.Node;
import org.terasology.logic.behavior.tree.Status;
import org.terasology.logic.behavior.tree.Task;
import org.terasology.math.geom.Vector3i;

/**
 * Performs a child node along the FlexibleMovementComponent.path
 *
 * 1. Sets the FlexibleMovementComponent.target
 * 2. Runs the child node until SUCCESS/FAILURE
 * 3. On child SUCCESS, sets target to next waypoint and starts child again
 * 4. On child FAILURE, returns FAILURE
 * 5. When end of path is reached, returns SUCCESS
 */
public class MoveAlongPathNode extends DecoratorNode {
    @Override
    public MoveAlongPathTask createTask() {
        return new MoveAlongPathTask(this);
    }

    public class MoveAlongPathTask extends Task {
        protected MoveAlongPathTask(Node node) {
            super(node);
        }

        @Override
        public void onInitialize() {
            start(getChild());
        }

        @Override
        public Status update(float dt) {
            return Status.RUNNING;
        }

        @Override
        public void handle(Status result) {
            FlexibleMovementComponent movement = actor().getComponent(FlexibleMovementComponent.class);
            if(result == Status.SUCCESS) {
                movement.advancePath();
                if(movement.isPathFinished()) {
                    movement.resetPath();
                    stop(Status.SUCCESS);
                } else {
                    start(getChild());
                }
            }

            if(result == Status.FAILURE) {
                stop(Status.FAILURE);
            }

            actor().save(movement);
        }
    }
}
