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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.engine.Time;
import org.terasology.flexiblemovement.FlexibleMovementComponent;
import org.terasology.flexiblemovement.plugin.WalkingMovementPlugin;
import org.terasology.flexiblemovement.system.FlexibleMovementSystem;
import org.terasology.flexiblemovement.system.PluginSystem;
import org.terasology.flexiblemovement.plugin.MovementPlugin;
import org.terasology.logic.behavior.tree.Node;
import org.terasology.logic.behavior.tree.Status;
import org.terasology.logic.behavior.tree.Task;
import org.terasology.logic.characters.CharacterMoveInputEvent;
import org.terasology.logic.characters.CharacterMovementComponent;
import org.terasology.logic.location.LocationComponent;
import org.terasology.math.geom.Vector3f;
import org.terasology.math.geom.Vector3i;
import org.terasology.registry.In;
import org.terasology.world.WorldProvider;

/**
 * Uses an actor's MovementPlugin to move it to FlexibleMovementComponent.target
 * SUCCESS: When the actor reaches FlexibleMovementComponent.target
 * FAILURE: When the actor believes it is unable to reach its immediate target
 */
public class MoveToNode extends Node {
    static final private Logger logger = LoggerFactory.getLogger(MoveToNodeTask.class);

    @Override
    public MoveToNodeTask createTask() {
        return new MoveToNodeTask(this);
    }

    public class MoveToNodeTask extends Task {
        private static final float UPDATE_INTERVAL_MILLIS = 100;

        @In private Time time;
        @In private WorldProvider world;
        @In private FlexibleMovementSystem flexibleMovementSystem;
        @In private PluginSystem pluginSystem;

        public MoveToNodeTask(Node node) {
            super(node);
        }

        @Override
        public Status update(float dt) {
            LocationComponent location = actor().getComponent(LocationComponent.class);
            FlexibleMovementComponent flexibleMovementComponent = actor().getComponent(FlexibleMovementComponent.class);
            CharacterMovementComponent characterMovementComponent = actor().getComponent(CharacterMovementComponent.class);

            // TODO: why radius instead of height?
            Vector3f adjustedMoveTarget = flexibleMovementComponent.target.toVector3f();
//            adjustedMoveTarget.addY(characterMovementComponent.radius);

            Vector3f position = location.getWorldPosition();
            if (position.distance(adjustedMoveTarget) < 0.1f &&
                    new Vector3i(position).distance(flexibleMovementComponent.target) == 0) {
                return Status.SUCCESS;
            }

            if (time.getGameTimeInMs() < flexibleMovementComponent.lastInput + UPDATE_INTERVAL_MILLIS) {
                return Status.RUNNING;
            }

            flexibleMovementComponent.sequenceNumber++;
            MovementPlugin plugin = pluginSystem.getMovementPlugin(actor().getEntity());
            CharacterMoveInputEvent result = plugin.move(
                    actor().getEntity(),
                    adjustedMoveTarget,
                    flexibleMovementComponent.sequenceNumber
            );

            if (result == null) {
                // this is ugly, but due to unknown idiosyncrasies in the engine character movement code, characters
                // sometimes sink into solid blocks below them. This causes reachability checks to fail intermittently,
                // especially when characters stop moving. In an ideal world, we'd exit failure here to indicate our
                // path is no longer valid. However, we instead fall back to a default movement plugin in the hopes
                // that a gentle nudge in a probably-correct direction will at least make the physics reconcile the
                // intersection, and hopefully return to properly penetrable blocks.
                logger.debug("Movement plugin returned null");
                MovementPlugin fallbackPlugin = new WalkingMovementPlugin(world, time);
                result = fallbackPlugin.move(
                        actor().getEntity(),
                        adjustedMoveTarget,
                        flexibleMovementComponent.sequenceNumber
                );
            }

            flexibleMovementSystem.enqueue(actor().getEntity(), result);
            flexibleMovementComponent.lastInput = time.getGameTimeInMs();
            flexibleMovementComponent.collidedHorizontally = false;
            actor().save(flexibleMovementComponent);

            return Status.RUNNING;
        }

        @Override
        public void handle(Status result) {

        }
    }
}
