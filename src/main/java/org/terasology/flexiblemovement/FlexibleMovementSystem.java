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

import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.systems.*;
import org.terasology.logic.characters.CharacterMoveInputEvent;
import org.terasology.registry.Share;

import java.util.Map;
import java.util.Queue;

@Share(FlexibleMovementSystem.class)
@RegisterSystem(RegisterMode.AUTHORITY)
public class FlexibleMovementSystem extends BaseComponentSystem implements UpdateSubscriberSystem {
    Map<EntityRef, CharacterMoveInputEvent> eventQueue = Maps.newHashMap();

    @Override
    public void update(float delta) {
        for(Map.Entry<EntityRef, CharacterMoveInputEvent> entry : eventQueue.entrySet()) {
            if(entry.getKey() != null && entry.getKey().exists()) {
                entry.getKey().send(entry.getValue());
            }
        }
        eventQueue.clear();
    }

    public void enqueue(EntityRef entity, CharacterMoveInputEvent event) {
        eventQueue.put(entity, event);
    }
}
