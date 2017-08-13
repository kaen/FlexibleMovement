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
import org.terasology.entitySystem.entity.EntityManager;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.logic.characters.CharacterTeleportEvent;
import org.terasology.logic.location.LocationComponent;
import org.terasology.math.geom.Vector3f;
import org.terasology.math.geom.Vector3i;
import org.terasology.world.WorldProvider;
import org.terasology.world.block.BlockManager;

public class UnstickingSystemTest extends FlexibleMovementTestingEnvironment {
    @Test
    public void testUnstickingSystem() {
        forceAndWaitForGeneration(Vector3i.zero());

        EntityManager entityManager = getHostContext().get(EntityManager.class);
        WorldProvider worldProvider = getHostContext().get(WorldProvider.class);
        BlockManager blockManager = getHostContext().get(BlockManager.class);

        EntityRef entity = entityManager.create("flexiblemovement:testcharacter");
        LocationComponent locationComponent = entity.getComponent(LocationComponent.class);
        entity.send(new CharacterTeleportEvent(Vector3f.zero()));

        worldProvider.setBlock(Vector3i.zero(), blockManager.getBlock("engine:unloaded"));
        worldProvider.setBlock(Vector3i.up(), blockManager.getBlock("engine:air"));
        runUntil(()-> worldProvider.getBlock(Vector3i.up()).isPenetrable());

        Assert.assertTrue(!worldProvider.getBlock(locationComponent.getWorldPosition()).isPenetrable());
        runWhile(()-> !worldProvider.getBlock(locationComponent.getWorldPosition()).isPenetrable());
    }
}
