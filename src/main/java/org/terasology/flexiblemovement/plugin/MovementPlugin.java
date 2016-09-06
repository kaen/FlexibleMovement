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
package org.terasology.flexiblemovement.plugin;

import org.terasology.engine.Time;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.flexiblepathfinding.plugins.StandardPlugin;
import org.terasology.logic.characters.CharacterMoveInputEvent;
import org.terasology.math.geom.Vector3f;
import org.terasology.world.WorldProvider;

public abstract class MovementPlugin {
    protected final WorldProvider world;
    protected final Time time;

    public MovementPlugin(WorldProvider world, Time time) {
        this.world = world;
        this.time = time;
    };

    public abstract StandardPlugin getPathfindingPlugin(EntityRef entity);
    public abstract CharacterMoveInputEvent move(EntityRef entity, Vector3f dest, int sequence);
}
