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

import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.entitySystem.systems.*;
import org.terasology.flexiblemovement.FlexibleMovementComponent;
import org.terasology.logic.characters.CharacterMoveInputEvent;
import org.terasology.logic.characters.events.HorizontalCollisionEvent;
import org.terasology.registry.Share;

import java.util.Map;


@Share(FlexibleMovementSystem.class)
@RegisterSystem(RegisterMode.AUTHORITY)
public class FlexibleMovementSystem extends BaseComponentSystem implements UpdateSubscriberSystem {
    private Map<EntityRef, CharacterMoveInputEvent> eventQueue = Maps.newHashMap();
    private Logger logger = LoggerFactory.getLogger(FlexibleMovementSystem.class);

    @Override
    public void update(float delta) {
        for(Map.Entry<EntityRef, CharacterMoveInputEvent> entry : eventQueue.entrySet()) {
            if(entry.getKey() != null && entry.getKey().exists()) {
                entry.getKey().send(entry.getValue());
            }
        }
        eventQueue.clear();
    }

    @ReceiveEvent
    public void markHorizontalCollision(HorizontalCollisionEvent event, EntityRef entity, FlexibleMovementComponent flexibleMovementComponent) {
        if(flexibleMovementComponent == null) {
            return;
        }

        flexibleMovementComponent.collidedHorizontally = true;
    }

    public void enqueue(EntityRef entity, CharacterMoveInputEvent event) {
        eventQueue.put(entity, event);
    }
}
