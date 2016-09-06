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

import org.junit.Assert;
import org.junit.Test;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.event.internal.EventReceiver;
import org.terasology.entitySystem.event.internal.EventSystem;
import org.terasology.flexiblepathfinding.PathfinderSystem;
import org.terasology.logic.behavior.tree.Status;
import org.terasology.logic.behavior.tree.Task;
import org.terasology.logic.characters.CharacterMoveInputEvent;
import org.terasology.logic.characters.MovementMode;
import org.terasology.math.geom.Vector3f;
import org.terasology.math.geom.Vector3i;
import org.terasology.registry.CoreRegistry;
import org.terasology.registry.InjectionHelper;

public class FindPathToNodeTest extends FlexibleMovementNodeTest {
    @Test
    public void testFindPathToNodeDefault() {
        flexibleMovementComponent.pathTarget = new Vector3i(2,0,2);
        CoreRegistry.put(PathfinderSystem.class, new PathfinderSystem());
        FindPathToNode node = new FindPathToNode();
        Task task = interpreter.start(node);
        InjectionHelper.inject(task);

        Assert.assertEquals(1, interpreter.tick(0));
        while(task.getStatus() == Status.RUNNING) { interpreter.tick(0); }
        Assert.assertEquals(2, flexibleMovementComponent.path.size());
        Assert.assertEquals(Status.SUCCESS, task.getStatus());
    }

    @Test
    public void testFindPathToNodeFailure() {
        flexibleMovementComponent.pathTarget = new Vector3i(-1,-1,-1);
        CoreRegistry.put(PathfinderSystem.class, new PathfinderSystem());
        FindPathToNode node = new FindPathToNode();
        Task task = interpreter.start(node);
        InjectionHelper.inject(task);

        Assert.assertEquals(1, interpreter.tick(0));
        while(task.getStatus() == Status.RUNNING) { interpreter.tick(0); }
        Assert.assertEquals(0, flexibleMovementComponent.path.size());
        Assert.assertEquals(Status.FAILURE, task.getStatus());
    }

    @Test
    public void testFindPathToNodeFlying() {
        flexibleMovementComponent.movementTypes.clear();
        flexibleMovementComponent.movementTypes.add("flying");
        flexibleMovementComponent.pathTarget = new Vector3i(3,3,3);

        CoreRegistry.put(PathfinderSystem.class, new PathfinderSystem());
        FindPathToNode node = new FindPathToNode();
        Task task = interpreter.start(node);
        InjectionHelper.inject(task);

        Assert.assertEquals(1, interpreter.tick(0));
        while(task.getStatus() == Status.RUNNING) { interpreter.tick(0); }
        Assert.assertEquals(Status.SUCCESS, task.getStatus());
        Assert.assertEquals(2, flexibleMovementComponent.path.size());
    }
}
