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
package org.terasology.flexiblemovement.action;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.context.Context;
import org.terasology.engine.Time;
import org.terasology.flexiblemovement.FlexibleMovementComponent;
import org.terasology.flexiblemovement.system.FlexibleMovementSystem;
import org.terasology.flexiblemovement.system.PluginSystem;
import org.terasology.flexiblemovement.plugin.MovementPlugin;
import org.terasology.logic.behavior.BehaviorAction;
import org.terasology.logic.behavior.core.Actor;
import org.terasology.logic.behavior.core.BaseAction;
import org.terasology.logic.behavior.core.BehaviorState;
import org.terasology.logic.characters.CharacterMoveInputEvent;
import org.terasology.logic.characters.ServerCharacterPredictionSystem;
import org.terasology.logic.location.LocationComponent;
import org.terasology.math.geom.Vector3f;
import org.terasology.registry.In;
import org.terasology.world.WorldProvider;

/**
 * Uses an actor's MovementPlugin to move it to FlexibleMovementComponent.target
 * SUCCESS: When the actor reaches FlexibleMovementComponent.target
 * FAILURE: When the actor believes it is unable to reach its immediate target
 */
@BehaviorAction(name = "flexible_movement_move_to_target")
public class MoveToTargetAction extends BaseAction {
    public static final float INPUT_COOLDOWN_SECONDS = (float) (ServerCharacterPredictionSystem.MAX_INPUT_UNDERFLOW) / 2.0f / 1000.0f;
    private static final Logger logger = LoggerFactory.getLogger(MoveToTargetAction.class);

    @In
    private Context context;

    private Time time;
    private WorldProvider world;
    private FlexibleMovementSystem flexibleMovementSystem;
    private PluginSystem pluginSystem;

    @Override
    public void construct(Actor actor) {
        time = context.get(Time.class);
        world = context.get(WorldProvider.class);
        flexibleMovementSystem = context.get(FlexibleMovementSystem.class);
        pluginSystem = context.get(PluginSystem.class);
    }

    @Override
    public BehaviorState modify(Actor actor, BehaviorState result) {
        LocationComponent location = actor.getComponent(LocationComponent.class);
        FlexibleMovementComponent component = actor.getComponent(FlexibleMovementComponent.class);

        if (component == null || component.target == null) {
            return BehaviorState.FAILURE;
        }

        // TODO: some times we skip a few steps in the path (while jumping).
        // TODO: Technically MoveTo shouldn't know about path
        component.sequenceNumber++;
        int skippedTargets = 0;
        int stepsLeft = component.getPath().size() - component.getPathIndex();
        CharacterMoveInputEvent inputEvent = null;
        MovementPlugin plugin = pluginSystem.getMovementPlugin(actor.getEntity());
        for (int i = 0; inputEvent == null && i < stepsLeft && i <= component.pathSkippingLookahead; i++) {
            Vector3f target = component.getPath().get(component.getPathIndex() + i).toVector3f();
            if (location.getWorldPosition().distance(target) <= component.targetTolerance) {
                skippedTargets = i;
            }

            Vector3f position = location.getWorldPosition();
            if (position.distance(target) <= component.targetTolerance) {
                stopMoving(actor, component);
                return BehaviorState.SUCCESS;
            }

            inputEvent = plugin.move(
                    actor.getEntity(),
                    target,
                    component.sequenceNumber,
                    (int) (actor.getDelta() * 1000.0f)
            );
        }

        if (inputEvent == null) {
            float now = time.getGameTime();
            if (component.lastCantMoveTime == 0) {
                component.lastCantMoveTime = now;
            }

            if (component.lastInputEvent == null || component.lastCantMoveTime - now > component.cantMoveToleranceSeconds) {
                stopMoving(actor, component);
                return BehaviorState.FAILURE;
            }

            actor.getEntity().send(component.lastInputEvent);
            actor.save(component);
            return BehaviorState.RUNNING;
        }

        actor.getEntity().send(inputEvent);
        // logger.warn("{} {}ms", inputEvent.getMovementDirection(), inputEvent.getDeltaMs());
        component.lastCantMoveTime = 0;
        component.lastInputTimeSeconds = time.getGameTimeInMs();
        component.collidedHorizontally = false;
        component.lastInputEvent = inputEvent;
        actor.save(component);

        // when we've skipped ahead in the path we need to  return SUCCESS to let the parent node know to advance
        return skippedTargets > 0 ? BehaviorState.SUCCESS : BehaviorState.RUNNING;
    }

    private void stopMoving(Actor actor, FlexibleMovementComponent flexibleMovementComponent) {
        actor.getEntity().send(new CharacterMoveInputEvent(flexibleMovementComponent.sequenceNumber++, 0f, 0f, org.terasology.math.geom.Vector3f.zero(), false, false, false, (int) (actor.getDelta() * 1000.0f)));
        flexibleMovementComponent.lastInputEvent = null;
        flexibleMovementComponent.lastCantMoveTime = 0;
        actor.getEntity().saveComponent(flexibleMovementComponent);
    }
}
