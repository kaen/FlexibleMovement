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

import org.terasology.engine.Time;
import org.terasology.logic.behavior.tree.Node;
import org.terasology.logic.behavior.tree.Status;
import org.terasology.logic.behavior.tree.Task;
import org.terasology.logic.characters.CharacterMoveInputEvent;
import org.terasology.logic.characters.CharacterMovementComponent;
import org.terasology.logic.characters.MovementMode;
import org.terasology.logic.location.LocationComponent;
import org.terasology.math.TeraMath;
import org.terasology.math.geom.Vector3f;
import org.terasology.math.geom.Vector3i;
import org.terasology.registry.In;
import org.terasology.world.WorldProvider;

public class MoveToNode extends Node{
    @Override
    public MoveToNodeTask createTask() {
        return new MoveToNodeTask(this);
    }

    private class MoveToNodeTask extends Task {
        protected MoveToNodeTask(Node node) {
            super(node);
        }
        private int sequenceNumber = 0;
        private Vector3f lastPos = new Vector3f();

        @In
        Time time;
        @In
        WorldProvider world;
        @In
        FlexibleMovementSystem flexibleMovementSystem;

        @Override
        public Status update(float dt) {
            LocationComponent location = actor().getComponent(LocationComponent.class);
            CharacterMovementComponent movement = actor().getComponent(CharacterMovementComponent.class);
            FlexibleMovementComponent flexibleMovementComponent = actor().getComponent(FlexibleMovementComponent.class);
            Vector3f moveTarget = flexibleMovementComponent.target.toVector3f().add(0.5f, 0.5f, 0.5f);

            if(location.getWorldPosition().distance(moveTarget) <= 0.5) {
                return Status.SUCCESS;
            }

            sequenceNumber++;
            CharacterMoveInputEvent result = flexibleMovementComponent.getMovementPlugin(world, time).move(actor().getEntity(), moveTarget, sequenceNumber);
            if(result != null) {
                flexibleMovementSystem.enqueue(actor().getEntity(), result);
            }

            flexibleMovementComponent.collidedHorizontally = false;
            actor().getEntity().saveComponent(flexibleMovementComponent);

            return result == null ? Status.FAILURE : Status.RUNNING;
        }

        @Override
        public void handle(Status result) {

        }
    }
}
