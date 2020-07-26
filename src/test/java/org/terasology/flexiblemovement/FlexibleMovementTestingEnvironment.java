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
import org.junit.Rule;
import org.junit.rules.Timeout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.engine.EngineTime;
import org.terasology.engine.Time;
import org.terasology.engine.subsystem.common.TimeSubsystem;
import org.terasology.engine.subsystem.headless.device.TimeSystem;
import org.terasology.entitySystem.entity.EntityManager;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.logic.characters.CharacterMovementComponent;
import org.terasology.logic.characters.CharacterTeleportEvent;
import org.terasology.logic.location.LocationComponent;
import org.terasology.math.Region3i;
import org.terasology.math.geom.Vector3f;
import org.terasology.math.geom.Vector3i;
import org.terasology.moduletestingenvironment.ModuleTestingEnvironment;
import org.terasology.moduletestingenvironment.ModuleTestingHelper;
import org.terasology.physics.engine.PhysicsEngine;
import org.terasology.registry.In;
import org.terasology.world.WorldProvider;
import org.terasology.world.block.Block;
import org.terasology.world.block.BlockManager;

import java.math.RoundingMode;
import java.util.List;
import java.util.Set;
import java.util.Timer;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class FlexibleMovementTestingEnvironment {
    private static final Logger logger = LoggerFactory.getLogger(FlexibleMovementTestingEnvironment.class);
    private final int timeoutMs = 30000;

    @In
    private ModuleTestingHelper helper;

    public void executeExample(String[] world, String[] path, String ... movementTypes) {
        executeExample(world, path, 0.9f, 0.3f, movementTypes);

    }

    public void executeExample(String[] world, String[] path, float height, float radius, String ... movementTypes) {
        int airHeight = 0;

        WorldProvider worldProvider = helper.getHostContext().get(WorldProvider.class);
        Block air = helper.getHostContext().get(BlockManager.class).getBlock("engine:air");
        Block dirt = helper.getHostContext().get(BlockManager.class).getBlock("coreassets:dirt");
        Block water = helper.getHostContext().get(BlockManager.class).getBlock("coreassets:water");

        Region3i extents = getPaddedExtents(world, airHeight);

        for (Vector3i pos : extents) {
            helper.forceAndWaitForGeneration(pos);
        }

        for (Vector3i pos : extents) {
            worldProvider.setBlock(pos, dirt);
        }

        // set blocks from world data
        for (int z = 0; z < world.length; z++) {
            int y = airHeight;
            String row = world[z];
            int x = 0;
            for (char c : row.toCharArray()) {
                switch (c) {
                    case 'X':
                        worldProvider.setBlock(new Vector3i(x, y, z), air);
                        x += 1;
                        break;
                    case ' ':
                        worldProvider.setBlock(new Vector3i(x, y, z), dirt);
                        x += 1;
                        break;
                    case '~':
                        worldProvider.setBlock(new Vector3i(x, y, z), water);
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

        EntityRef entity = helper.getHostContext().get(EntityManager.class).create("flexiblemovement:testcharacter");
        entity.send(new CharacterTeleportEvent(start.toVector3f()));
        entity.getComponent(FlexibleMovementComponent.class).setPathGoal(stop);
        entity.getComponent(FlexibleMovementComponent.class).movementTypes.clear();
        entity.getComponent(FlexibleMovementComponent.class).movementTypes.addAll(Sets.newHashSet(movementTypes));

        entity.getComponent(CharacterMovementComponent.class).height = height;
        entity.getComponent(CharacterMovementComponent.class).radius = radius;

        // after updating character collision stuff we have to remake the collider, based on the playerHeight command
        // TODO there should probably be a helper for this instead
        helper.getHostContext().get(PhysicsEngine.class).removeCharacterCollider(entity);
        helper.getHostContext().get(PhysicsEngine.class).getCharacterCollider(entity);

        helper.runUntil(()-> FlexibleMovementHelper.posToBlock(entity.getComponent(LocationComponent.class).getWorldPosition()).distance(start) == 0);

        List<Vector3i> traveledPath = Lists.newArrayList();
        final Vector3f lastPosition = new Vector3f(entity.getComponent(LocationComponent.class).getWorldPosition());
        float delta = 0.5f;
        boolean timedOut = helper.runWhile(timeoutMs, ()-> {
            Vector3f pos = entity.getComponent(LocationComponent.class).getWorldPosition();
            Vector3i block = new Vector3i(pos, RoundingMode.HALF_UP);
            if (traveledPath.isEmpty() || !traveledPath.get(traveledPath.size() - 1).equals(block)) {
                traveledPath.add(block);
            }
            float distance = pos.distance(stop.toVector3f());
            logger.warn("distance: {}, pos: {}", distance, pos);
            logger.warn("last movement: {}", new Vector3f(pos).sub(lastPosition));
            lastPosition.set(pos);
            return distance > delta;
        });

        float distance = entity.getComponent(LocationComponent.class).getWorldPosition().distance(stop.toVector3f());

        // clean up
        entity.destroy();

        logger.debug("Goal was {}, distance {}", stop, distance);
        logger.debug("Path traveled: {}", traveledPath);
        assertEquals(0f, distance, delta);
        assertFalse(timedOut);
    }

    private Region3i getPaddedExtents(String[] world, int airHeight) {
        Region3i extents = Region3i.createFromCenterExtents(new Vector3i(0, airHeight, 0), 0);
        for (int z = 0; z < world.length; z++) {
            int y = airHeight;
            String row = world[z];
            int x = 0;
            for (char c : row.toCharArray()) {
                extents = extents.expandToContain(new Vector3i(x, y, z));
                switch (c) {
                    case 'X':
                        x += 1;
                        break;
                    case ' ':
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
        extents = extents.expand(1);
        return extents;
    }
}
