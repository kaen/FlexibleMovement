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

import org.junit.Assert;
import org.junit.Test;
import org.terasology.logic.behavior.tree.Status;
import org.terasology.logic.behavior.tree.Task;
import org.terasology.math.geom.Vector3i;
import org.terasology.registry.InjectionHelper;

public class FindDummyPathToNodeTest extends FlexibleMovementNodeTest {
    @Test
    public void testFindDummyPathToNodeDefault() {
        flexibleMovementComponent.setPathGoal(new Vector3i(2,0,2));
        FindDummyPathToNode node = new FindDummyPathToNode();
        Task task = interpreter.start(node);
        InjectionHelper.inject(task);

        Assert.assertEquals(1, interpreter.tick(0));
        while(task.getStatus() == Status.RUNNING) { interpreter.tick(0); }
        Assert.assertEquals(2, flexibleMovementComponent.getPath().size());
        Assert.assertEquals(Status.SUCCESS, task.getStatus());
    }
}