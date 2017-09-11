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

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.engine.SimpleUri;
import org.terasology.engine.Time;
import org.terasology.engine.Uri;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterMode;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.flexiblemovement.FlexibleMovementComponent;
import org.terasology.flexiblemovement.plugin.*;
import org.terasology.registry.In;
import org.terasology.registry.Share;
import org.terasology.world.WorldProvider;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Share(PluginSystem.class)
@RegisterSystem(RegisterMode.AUTHORITY)
public class PluginSystem extends BaseComponentSystem {
    private static final Logger logger = LoggerFactory.getLogger(PluginSystem.class);

    @In
    private WorldProvider worldProvider;

    @In
    private Time time;

    private Map<Uri, Function<EntityRef, MovementPlugin>> registeredPlugins = Maps.newHashMap();

    @Override
    public void initialise() {
        super.initialise();
        registerMovementPlugin("walking", (entity)-> new WalkingMovementPlugin(worldProvider, time));
        registerMovementPlugin("leaping", (entity)-> new LeapingMovementPlugin(worldProvider, time));
        registerMovementPlugin("flying", (entity)-> new FlyingMovementPlugin(worldProvider, time));
        registerMovementPlugin("swimming", (entity)-> new SwimmingMovementPlugin(worldProvider, time));
    }

    public void registerMovementPlugin(String name, Function<EntityRef, MovementPlugin> supplier) {
        SimpleUri uri = new SimpleUri(name);
        if (uri.getModuleName().isEmpty()) {
            uri = new SimpleUri("FlexibleMovement", name);
        }

        if (!uri.isValid()) {
            logger.error("Not registering invalid movement plugin URI: {}", uri);
            return;
        }

        if (registeredPlugins.containsKey(uri)) {
            logger.warn("MovementPlugin {} already registered, overwriting", uri);
        }

        registeredPlugins.put(uri, supplier);
    }

    public MovementPlugin getMovementPlugin(EntityRef entity) {
        List<MovementPlugin> plugins = Lists.newArrayList();
        FlexibleMovementComponent component = entity.getComponent(FlexibleMovementComponent.class);
        for (String movementType : component.movementTypes) {

            // if the module name is omitted, assume it's from the base module
            SimpleUri uri = new SimpleUri(movementType);
            if (uri.getModuleName().isEmpty()) {
                uri = new SimpleUri("FlexibleMovement", movementType);
            }

            if (!uri.isValid() || !registeredPlugins.containsKey(uri)) {
                logger.warn("Unknown or invalid MovementPlugin requested: {}", uri);
            }

            MovementPlugin newPlugin = registeredPlugins.get(uri).apply(entity);
            plugins.add(newPlugin);

        }
        return new CompositeMovementPlugin(worldProvider, time, plugins);
    }
}
