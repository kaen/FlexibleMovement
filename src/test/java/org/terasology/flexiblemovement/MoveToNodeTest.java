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

import com.google.common.collect.Lists;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.terasology.assets.AssetFactory;
import org.terasology.engine.SimpleUri;
import org.terasology.entitySystem.Component;
import org.terasology.entitySystem.entity.EntityManager;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.entity.internal.PojoEntityManager;
import org.terasology.entitySystem.event.Event;
import org.terasology.entitySystem.event.internal.EventReceiver;
import org.terasology.entitySystem.event.internal.EventSystem;
import org.terasology.entitySystem.event.internal.EventSystemImpl;
import org.terasology.entitySystem.systems.ComponentSystem;
import org.terasology.flexiblepathfinding.WorldProvidingHeadlessEnvironment;
import org.terasology.logic.behavior.asset.BehaviorTree;
import org.terasology.logic.behavior.asset.BehaviorTreeData;
import org.terasology.logic.behavior.tree.*;
import org.terasology.logic.characters.CharacterMoveInputEvent;
import org.terasology.logic.characters.CharacterMovementComponent;
import org.terasology.logic.characters.MovementMode;
import org.terasology.logic.location.LocationComponent;
import org.terasology.math.geom.Vector3f;
import org.terasology.math.geom.Vector3i;
import org.terasology.registry.CoreRegistry;

public class MoveToNodeTest extends FlexibleMovementNodeTest {

    @Test
    public void testMoveToNodeDefault() {
        flexibleMovementComponent.target.set(0,0,1);

        MoveToNode moveToNode = new MoveToNode();
        EventReceiver testEventReceiver = new EventReceiver<CharacterMoveInputEvent>() {
            @Override
            public void onEvent(CharacterMoveInputEvent event, EntityRef entity) {
                Assert.assertTrue(new Vector3f(0,0,1).sub(event.getMovementDirection()).length() < 1);
                done = true;
            }
        };
        EventSystem eventSystem = CoreRegistry.get(EventSystem.class);
        eventSystem.registerEventReceiver(testEventReceiver, CharacterMoveInputEvent.class);

        // ensure we move in the right direction
        done = false;
        Task task = interpreter.start(moveToNode);
        Assert.assertEquals(Status.RUNNING, task.update(0));
        flexibleMovementSystem.update(0);
        Assert.assertEquals(MovementMode.WALKING, characterMovementComponent.mode);
        Assert.assertTrue(done);

        // now move to the target and ensure completion
        done = false;
        locationComponent.setWorldPosition(new Vector3f(0.5f,0.5f,1.5f));
        Assert.assertEquals(Status.SUCCESS, task.update(0));
        flexibleMovementSystem.update(0);
        Assert.assertFalse(done);
    }

    @Test
    public void testMoveToNodeFlying() {
        flexibleMovementComponent.target.set(3,3,3);
        flexibleMovementComponent.movementTypes.clear();
        flexibleMovementComponent.movementTypes.add("flying");
        MoveToNode moveToNode = new MoveToNode();
        EventReceiver testEventReceiver = new EventReceiver<CharacterMoveInputEvent>() {
            @Override
            public void onEvent(CharacterMoveInputEvent event, EntityRef entity) {
                Vector3f realTarget = flexibleMovementComponent.target.toVector3f().add(0.5f,0.5f,0.5f);
                float beforeDistance = realTarget.distance(locationComponent.getWorldPosition());
                float afterDistance = realTarget.distance(locationComponent.getWorldPosition().add(event.getMovementDirection()));
                Assert.assertTrue(afterDistance < beforeDistance);
                locationComponent.setWorldPosition(locationComponent.getWorldPosition().add(event.getMovementDirection()));
                done = true;
            }
        };
        EventSystem eventSystem = CoreRegistry.get(EventSystem.class);
        eventSystem.registerEventReceiver(testEventReceiver, CharacterMoveInputEvent.class);

        // ensure we move in the right direction
        done = true;
        Task task = interpreter.start(moveToNode);
        do {
            Assert.assertTrue(done);
            done = false;
            interpreter.tick(0);
            flexibleMovementSystem.update(0);
            Assert.assertEquals(MovementMode.FLYING, characterMovementComponent.mode);
        } while(Status.RUNNING == task.getStatus());

        Assert.assertEquals(Status.SUCCESS, task.getStatus());
        Assert.assertTrue(flexibleMovementComponent.target.toVector3f().add(0.5f,0.5f,0.5f).distance(locationComponent.getWorldPosition()) < 0.5);
    }
}
