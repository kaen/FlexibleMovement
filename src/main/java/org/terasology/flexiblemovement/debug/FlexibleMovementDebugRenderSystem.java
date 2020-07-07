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
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterMode;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.entitySystem.systems.RenderSystem;
import org.terasology.flexiblemovement.FlexibleMovementComponent;
import org.terasology.math.geom.Vector3i;
import org.terasology.registry.In;
import org.terasology.registry.Share;
import org.terasology.rendering.world.selection.BlockSelectionRenderer;
import org.terasology.utilities.Assets;

@RegisterSystem(RegisterMode.CLIENT)
@Share(FlexibleMovementDebugRenderSystem.class)
public class FlexibleMovementDebugRenderSystem extends BaseComponentSystem implements RenderSystem {
    private BlockSelectionRenderer selectionRenderer;
    private boolean enabled = true;

    @In
    private EntityManager entityManager;

    @Override
    public void initialise() {
        selectionRenderer = new BlockSelectionRenderer(Assets.getTexture("engine:selection").get());
    }

    @Override
    public void renderOverlay() {
        if (!enabled) {
            return;
        }

        selectionRenderer.beginRenderOverlay();
        for (EntityRef entity : entityManager.getEntitiesWith(FlexibleMovementComponent.class)) {
            FlexibleMovementComponent flexibleMovementComponent = entity.getComponent(FlexibleMovementComponent.class);
            for (Vector3i pos : flexibleMovementComponent.getPath()) {
                selectionRenderer.renderMark2(pos);
            }
        }
        selectionRenderer.endRenderOverlay();
    }

    @ReceiveEvent
    public void onToggleRender(ToggleRenderingEvent event, EntityRef entity) {
        event.consume();
        if (event.isDown()) {
            enabled = !enabled;
        }
    }

    @Override
    public void renderOpaque() {

    }

    @Override
    public void renderAlphaBlend() {

    }

    @Override
    public void renderShadows() {

    }
}
