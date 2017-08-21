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
package org.terasology.flexiblemovement.debug;

import org.terasology.assets.management.AssetManager;
import org.terasology.entitySystem.entity.EntityManager;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterMode;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.flexiblemovement.FlexibleMovementComponent;
import org.terasology.logic.behavior.BehaviorComponent;
import org.terasology.logic.behavior.asset.BehaviorTree;
import org.terasology.logic.common.ActivateEvent;
import org.terasology.math.geom.Vector3i;
import org.terasology.registry.In;
import org.terasology.registry.Share;
import org.terasology.utilities.Assets;

import java.math.RoundingMode;

@Share(DebugMoveToSystem.class)
@RegisterSystem(RegisterMode.AUTHORITY)
public class DebugMoveToSystem extends BaseComponentSystem {
    @In private EntityManager entityManager;
    @In private AssetManager assetManager;

    @ReceiveEvent(components = {DebugMoveToComponent.class})
    public void onDebugMoveToActivated(ActivateEvent event, EntityRef item) {
        for (EntityRef entity : entityManager.getEntitiesWith(FlexibleMovementComponent.class, BehaviorComponent.class)) {
            FlexibleMovementComponent component = entity.getComponent(FlexibleMovementComponent.class);
            BehaviorComponent behaviorComponent = entity.getComponent(BehaviorComponent.class);
            behaviorComponent.tree = assetManager.getAsset("FlexibleMovement:reliableMoveTo", BehaviorTree.class).get();

            component.setPathGoal(new Vector3i(event.getHitPosition(), RoundingMode.HALF_UP));
            component.resetPath();
            entity.saveComponent(component);
            entity.saveComponent(behaviorComponent);
        }
    }
}
