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
import org.junit.Assert;
import org.junit.Test;
import org.terasology.logic.behavior.tree.Status;
import org.terasology.logic.behavior.tree.Task;
import org.terasology.math.geom.Vector3f;
import org.terasology.math.geom.Vector3i;

import java.util.Arrays;
import java.util.List;

public class ValidatePathNodeTest extends FlexibleMovementNodeTest {
//    @Test
//    public void testPathIsValid() {
//        List<Vector3i> path = Arrays.asList(new Vector3i(0, 0, 0), new Vector3i(1, 0, 1));
//        flexibleMovementComponent.setPath(path);
//        locationComponent.setWorldPosition(new Vector3f(0, 0, 0));
//
//        ValidatePathNode node = new ValidatePathNode();
//        Task task = interpreter.start(node);
//
//        interpreter.tick(0);
//        Assert.assertEquals(Status.SUCCESS, task.getStatus());
//    }
//
//    @Test
//    public void testPathIsInvalid() {
//        // invalid because (1,2,1) is in midair and therefore unreachable
//        List<Vector3i> path = Arrays.asList(new Vector3i(0, 0, 0), new Vector3i(1, 2, 1));
//        flexibleMovementComponent.setPath(path);
//        locationComponent.setWorldPosition(new Vector3f(0, 0, 0));
//
//        ValidatePathNode node = new ValidatePathNode();
//        Task task = interpreter.start(node);
//
//        interpreter.tick(0);
//        Assert.assertEquals(Status.FAILURE, task.getStatus());
//    }
}
