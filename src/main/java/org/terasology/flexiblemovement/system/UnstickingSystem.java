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
package org.terasology.flexiblemovement.system;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.engine.Time;
import org.terasology.entitySystem.entity.EntityManager;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterMode;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.entitySystem.systems.UpdateSubscriberSystem;
import org.terasology.flexiblemovement.FlexibleMovementComponent;
import org.terasology.flexiblemovement.FlexibleMovementHelper;
import org.terasology.logic.characters.CharacterTeleportEvent;
import org.terasology.logic.location.LocationComponent;
import org.terasology.math.geom.Vector3f;
import org.terasology.math.geom.Vector3i;
import org.terasology.registry.In;
import org.terasology.registry.Share;
import org.terasology.world.WorldProvider;

import java.math.RoundingMode;

@Share(UnstickingSystem.class)
@RegisterSystem(RegisterMode.AUTHORITY)
public class UnstickingSystem extends BaseComponentSystem implements UpdateSubscriberSystem {
    private static final float UNSTICK_DELTA = 1.0f;
    private static final float UNSTICK_INTERVAL_MILLIS = 1000;

    @In
    private EntityManager entityManager;

    @In
    private WorldProvider worldProvider;

    @In
    private Time time;

    private float lastUnstick;
    private Logger logger = LoggerFactory.getLogger(UnstickingSystem.class);

    @Override
    public void update(float delta) {
        if (time.getGameTimeInMs() - lastUnstick < UNSTICK_INTERVAL_MILLIS) {
            return;
        }

        lastUnstick = time.getGameTimeInMs();
        for (EntityRef entity : entityManager.getEntitiesWith(FlexibleMovementComponent.class, LocationComponent.class)) {
            LocationComponent locationComponent = entity.getComponent(LocationComponent.class);
            Vector3f pos = locationComponent.getWorldPosition();
            if(!worldProvider.getBlock(FlexibleMovementHelper.posToBlock(pos)).isPenetrable()) {
                pos.setY((float) Math.ceil(pos.y));
//                entity.send(new CharacterTeleportEvent(pos));
                logger.debug("Unsticking: {}", entity);
            }
        }
    }
}
