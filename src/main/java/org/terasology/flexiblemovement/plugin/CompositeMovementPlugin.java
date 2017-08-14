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
package org.terasology.flexiblemovement.plugin;

import com.google.common.collect.Lists;
import org.terasology.engine.Time;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.flexiblepathfinding.plugins.JPSPlugin;
import org.terasology.flexiblepathfinding.plugins.basic.CompositePlugin;
import org.terasology.logic.characters.CharacterMoveInputEvent;
import org.terasology.logic.location.LocationComponent;
import org.terasology.math.geom.Vector3f;
import org.terasology.math.geom.Vector3i;
import org.terasology.world.WorldProvider;

import java.util.Collection;
import java.util.List;

public class CompositeMovementPlugin extends MovementPlugin {
    public List<MovementPlugin> getPlugins() {
        return plugins;
    }

    List<MovementPlugin> plugins = Lists.newArrayList();
    public CompositeMovementPlugin(WorldProvider world, Time time, MovementPlugin ... plugins) {
        super(world, time);
        this.plugins = Lists.newArrayList(plugins);
    }

    public CompositeMovementPlugin(WorldProvider worldProvider, Time time, Collection<MovementPlugin> plugins) {
        super(worldProvider, time);
        this.plugins = Lists.newArrayList(plugins);
    }

    @Override
    public JPSPlugin getJpsPlugin(EntityRef entity) {
        CompositePlugin jpsPlugin = new CompositePlugin();
        for (MovementPlugin plugin : plugins) {
            jpsPlugin.addPlugin(plugin.getJpsPlugin(entity));
        }
        return jpsPlugin;
    }

    @Override
    public CharacterMoveInputEvent move(EntityRef entity, Vector3f dest, int sequence) {
        Vector3i from = new Vector3i(entity.getComponent(LocationComponent.class).getWorldPosition());
        Vector3i to = new Vector3i(dest);
        for (MovementPlugin plugin : plugins) {
            if (plugin.getJpsPlugin(entity).isReachable(to, from)) {
                return plugin.move(entity, dest, sequence);
            }
        }
        return null;
    }
}
