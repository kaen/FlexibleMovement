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

import org.terasology.entitySystem.entity.EntityManager;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.systems.RegisterMode;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.flexiblemovement.FlexibleMovementComponent;
import org.terasology.logic.location.LocationComponent;
import org.terasology.math.Region3i;
import org.terasology.math.geom.*;
import org.terasology.registry.CoreRegistry;
import org.terasology.registry.Share;
import org.terasology.rendering.nui.Canvas;
import org.terasology.rendering.nui.layouts.FlowLayout;
import org.terasology.rendering.world.WorldRenderer;

public class FlexibleMovementDebugLayout extends FlowLayout {
    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//        for(EntityRef entity : CoreRegistry.get(EntityManager.class).getEntitiesWith(FlexibleMovementComponent.class)) {
//            Matrix4f matrix = new Matrix4f();
//            Vector3f position = entity.getComponent(LocationComponent.class).getWorldPosition();
//            matrix.set(new Quat4d(position.x, position.y, position.z, 1.0));
//            matrix.mul(CoreRegistry.get(WorldRenderer.class).getActiveCamera().getViewProjectionMatrix());
//
//            Vector2i screenPos = new Vector2i(matrix.getM00(), matrix.getM01());
//
//            Rect2i region = Rect2i.createFromMinAndSize(screenPos.x, screenPos.y, 100, 100);
//            canvas.drawText(entity.getParentPrefab().getName(), region);
//        }
    }
}
