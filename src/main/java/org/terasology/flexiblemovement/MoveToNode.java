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
import org.terasology.logic.characters.CharacterStateEvent;
import org.terasology.logic.characters.MovementMode;
import org.terasology.logic.location.LocationComponent;
import org.terasology.math.TeraMath;
import org.terasology.math.geom.Vector3f;
import org.terasology.math.geom.Vector3i;
import org.terasology.physics.engine.PhysicsEngine;
import org.terasology.registry.In;
import org.terasology.utilities.random.FastRandom;
import org.terasology.utilities.random.Random;
import org.terasology.world.WorldProvider;

/**
 * Uses an actor's MovementPlugin to move it to FlexibleMovementComponent.target
 * SUCCESS: When the actor reaches FlexibleMovementComponent.target
 * FAILURE: When the actor believes it is unable to reach its immediate target
 */
public class MoveToNode extends Node {

    @Override
    public MoveToNodeTask createTask() {
        return new MoveToNodeTask(this);
    }

    public class MoveToNodeTask extends Task {
        private float STUCK_MOVESPEED_PROPORTION = 0.2f;

        public MoveToNodeTask(Node node) {
            super(node);
        }

        private final float UPDATE_INTERVAL_MILLIS = 100;
        @In
        private Time time;
        @In
        private WorldProvider world;
        @In
        private FlexibleMovementSystem flexibleMovementSystem;

        private Vector3f lastPos;

        @Override
        public Status update(float dt) {
            LocationComponent location = actor().getComponent(LocationComponent.class);
            FlexibleMovementComponent flexibleMovementComponent = actor().getComponent(FlexibleMovementComponent.class);
            CharacterMovementComponent characterMovementComponent = actor().getComponent(CharacterMovementComponent.class);
            Vector3f moveTarget = flexibleMovementComponent.target.toVector3f();

            if(lastPos != null && location.getWorldPosition().distance(lastPos) < (UPDATE_INTERVAL_MILLIS / 1000.0f) * STUCK_MOVESPEED_PROPORTION * characterMovementComponent.speedMultiplier) {
                return Status.FAILURE;
            }

            lastPos = location.getWorldPosition();

            if(location.getWorldPosition().distance(moveTarget) <= 0.5) {
                return Status.SUCCESS;
            }

            if(time.getGameTimeInMs() < flexibleMovementComponent.lastInput + UPDATE_INTERVAL_MILLIS) {
                return Status.RUNNING;
            }

            flexibleMovementComponent.sequenceNumber++;

            CharacterMoveInputEvent result = flexibleMovementComponent.getMovementPlugin(world, time).move(actor().getEntity(), moveTarget, flexibleMovementComponent.sequenceNumber);
            if(result != null) {
                flexibleMovementSystem.enqueue(actor().getEntity(), result);
                flexibleMovementComponent.lastInput = time.getGameTimeInMs();
            }

            flexibleMovementComponent.collidedHorizontally = false;
            actor().save(flexibleMovementComponent);

            return result == null ? Status.FAILURE : Status.RUNNING;
        }

        @Override
        public void handle(Status result) {

        }
    }
}
