/*
 * Copyright 2016 MovingBlocks
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

import org.terasology.logic.behavior.tree.DecoratorNode;
import org.terasology.logic.behavior.tree.Node;
import org.terasology.logic.behavior.tree.Status;
import org.terasology.logic.behavior.tree.Task;
import org.terasology.logic.location.LocationComponent;
import org.terasology.math.geom.Vector3i;

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
            FlexibleMovementComponent movement = actor().getComponent(FlexibleMovementComponent.class);
            if(movement.path == null || movement.pathIndex >= movement.path.size()) {
                return Status.FAILURE;
            }
            movement.target.set(movement.path.get(movement.pathIndex));
            actor().save(movement);
            return Status.RUNNING;
        }

        @Override
        public void handle(Status result) {
            FlexibleMovementComponent movement = actor().getComponent(FlexibleMovementComponent.class);
            if(result == Status.SUCCESS) {
                movement.pathIndex++;
                if(movement.pathIndex >= movement.path.size()) {
                    clearMovement();
                    stop(Status.SUCCESS);
                    return;
                }
                start(getChild());
            }

            if(result == Status.FAILURE) {
                clearMovement();
                stop(Status.FAILURE);
            }
        }

        private void clearMovement() {
            FlexibleMovementComponent movement = actor().getComponent(FlexibleMovementComponent.class);
            movement.path = null;
            movement.target.set(Vector3i.zero());
            movement.pathIndex = 0;
            movement.pathTarget = null;
            actor().save(movement);
        }
    }
}
