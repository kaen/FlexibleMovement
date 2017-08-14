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
import org.junit.Assert;
import org.junit.Test;
import org.terasology.entitySystem.entity.EntityManager;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.entity.internal.PojoEntityManager;
import org.terasology.flexiblemovement.plugin.CompositeMovementPlugin;
import org.terasology.flexiblemovement.plugin.LeapingMovementPlugin;
import org.terasology.flexiblemovement.plugin.MovementPlugin;
import org.terasology.flexiblemovement.plugin.WalkingMovementPlugin;
import org.terasology.flexiblepathfinding.plugins.JPSPlugin;
import org.terasology.flexiblepathfinding.plugins.basic.CompositePlugin;
import org.terasology.flexiblepathfinding.plugins.basic.LeapingPlugin;
import org.terasology.flexiblepathfinding.plugins.basic.WalkingPlugin;

import java.util.List;

public class PluginSystemTest {
    @Test public void testPluginSystem() {
        // setup
        PluginSystem system = new PluginSystem();
        system.initialise();
        EntityManager entityManager = new PojoEntityManager();
        FlexibleMovementComponent component = new FlexibleMovementComponent();
        component.movementTypes = Lists.newArrayList("walking", "leaping");
        EntityRef entity = entityManager.create(component);

        // assert that we get a composite movement plugin with walking and leaping
        MovementPlugin plugin = system.getMovementPlugin(entity);
        List<MovementPlugin> baseMovementPlugins = ((CompositeMovementPlugin) plugin).getPlugins();
        Assert.assertTrue(plugin instanceof CompositeMovementPlugin);
        Assert.assertTrue(baseMovementPlugins.get(0) instanceof WalkingMovementPlugin);
        Assert.assertTrue(baseMovementPlugins.get(1) instanceof LeapingMovementPlugin);

        // assert that we get a composite pathfinding plugin with walking and leaping
        List<JPSPlugin> subPlugins = ((CompositePlugin) plugin.getJpsPlugin(entity)).getPlugins();
        Assert.assertTrue(subPlugins.size() == 2);
        Assert.assertTrue(subPlugins.get(0) instanceof WalkingPlugin);
        Assert.assertTrue(subPlugins.get(1) instanceof LeapingPlugin);
    }
}
