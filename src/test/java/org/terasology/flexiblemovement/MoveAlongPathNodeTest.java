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
import org.junit.Test;
import org.terasology.logic.behavior.tree.Node;
import org.terasology.logic.behavior.tree.Status;
import org.terasology.logic.behavior.tree.Task;
import org.terasology.math.geom.Vector3f;
import org.terasology.math.geom.Vector3i;

public class MoveAlongPathNodeTest extends FlexibleMovementNodeTest {

    @Test
    public void testMoveAlongPathNode() {
        class TestMoveAlongPathNodeChildNode extends Node {
            @Override
            public TestMoveAlongPathNodeChildTask createTask() {
                return new TestMoveAlongPathNodeChildTask(this);
            }

            class TestMoveAlongPathNodeChildTask extends Task {
                protected TestMoveAlongPathNodeChildTask(Node node) {
                    super(node);
                }

                @Override
                public Status update(float dt) {
                    locationComponent.setWorldPosition(flexibleMovementComponent.target.toVector3f());
                    done = true;
                    return Status.SUCCESS;
                }

                @Override
                public void handle(Status result) {

                }
            }
        }

        MoveAlongPathNode moveAlongPathNode = new MoveAlongPathNode();
        moveAlongPathNode.setChild(0, new TestMoveAlongPathNodeChildNode());
        flexibleMovementComponent.path = Lists.newArrayList(new Vector3i(2,0,2), Vector3i.zero());

        done = false;
        Task task = interpreter.start(moveAlongPathNode);
        Assert.assertEquals(2, interpreter.tick(0));
        Assert.assertEquals(Status.RUNNING, task.getStatus());
        Assert.assertEquals(new Vector3i(2,0,2), flexibleMovementComponent.target);
        Assert.assertEquals(2, flexibleMovementComponent.path.size());
        Assert.assertEquals(1, flexibleMovementComponent.pathIndex);
        Assert.assertTrue(done);

        done = false;
        Assert.assertEquals(2, interpreter.tick(0));
        Assert.assertEquals(Status.SUCCESS, task.getStatus());
        Assert.assertEquals(Vector3i.zero(), flexibleMovementComponent.target);
        Assert.assertEquals(null, flexibleMovementComponent.path);
        Assert.assertEquals(0, flexibleMovementComponent.pathIndex);
        Assert.assertTrue(done);
    }
}
