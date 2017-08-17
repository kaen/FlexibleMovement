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

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.entitySystem.entity.EntityManager;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.prefab.PrefabManager;
import org.terasology.logic.characters.CharacterTeleportEvent;
import org.terasology.logic.location.LocationComponent;
import org.terasology.math.Region3i;
import org.terasology.math.geom.Vector3f;
import org.terasology.math.geom.Vector3i;
import org.terasology.moduletestingenvironment.ModuleTestingEnvironment;
import org.terasology.world.WorldProvider;
import org.terasology.world.block.Block;
import org.terasology.world.block.BlockManager;
import org.terasology.world.generation.World;
import org.terasology.world.generator.WorldGenerator;

import java.util.Set;

public class FlexibleMovementTestingEnvironment extends ModuleTestingEnvironment {
    private static final Logger logger = LoggerFactory.getLogger(FlexibleMovementTestingEnvironment.class);
    @Override
    public Set<String> getDependencies() {
        return Sets.newHashSet("FlexibleMovement");
    }

    @Override
    public String getWorldGeneratorUri() {
        return "core:flat";
    }

    public void executeFailingExample(String[] world, String[] path) {
        // do nothing
    }

    public void executeExample(String[] world, String[] path) {
        int airHeight = 41;
        forceAndWaitForGeneration(new Vector3i(0, airHeight, 0));
        WorldProvider worldProvider = getHostContext().get(WorldProvider.class);
        Block air = getHostContext().get(BlockManager.class).getBlock("engine:air");
        Block dirt = getHostContext().get(BlockManager.class).getBlock("core:dirt");
        Region3i extents = Region3i.createFromCenterExtents(new Vector3i(0, airHeight, 0), 0);

        // set blocks from world data
        for (int z = 0; z < world.length; z++) {
            int y = airHeight;
            String row = world[z];
            int x = 0;
            for (char c : row.toCharArray()) {
                extents.expandToContain(new Vector3i(x, y, z));
                switch (c) {
                    case 'X':
                        worldProvider.setBlock(new Vector3i(x, y, z), air);
                        x += 1;
                        break;
                    case ' ':
                        worldProvider.setBlock(new Vector3i(x, y, z), dirt);
                        x += 1;
                        break;
                    case '|':
                        y += 1;
                        x = 0;
                        break;
                    default:
                        x += 1;
                        break;
                }
            }
        }

        // find start and goal positions from path data
        Vector3i start = Vector3i.zero();
        Vector3i stop = Vector3i.zero();
        for (int z = 0; z < path.length; z++) {
            int y = airHeight;
            String row = path[z];
            int x = 0;
            for (char c : row.toCharArray()) {
                switch (c) {
                    case '?':
                        start.set(x, y, z);
                        x += 1;
                        break;
                    case '!':
                        stop.set(x, y, z);
                        x += 1;
                        break;
                    case '|':
                        y += 1;
                        x = 0;
                        break;
                    default:
                        x += 1;
                        break;
                }
            }
        }

        EntityRef entity = getHostContext().get(EntityManager.class).create("flexiblemovement:testcharacter");
        entity.send(new CharacterTeleportEvent(start.toVector3f().addY(0.5f)));
        entity.getComponent(FlexibleMovementComponent.class).setPathGoal(stop);

        runUntil(()-> {
            return new Vector3i(entity.getComponent(LocationComponent.class).getWorldPosition()).distance(start) == 0;
        });

        runWhile(()-> {
            Vector3f pos = entity.getComponent(LocationComponent.class).getWorldPosition();
            logger.warn("pos: {}", pos);
            return new Vector3i(pos).distance(stop) > 0;
        });
    }
}
