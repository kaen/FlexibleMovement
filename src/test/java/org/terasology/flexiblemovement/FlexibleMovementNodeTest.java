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

import org.junit.Before;
import org.terasology.entitySystem.entity.EntityManager;
import org.terasology.flexiblepathfinding.WorldProvidingHeadlessEnvironment;
import org.terasology.logic.behavior.tree.Actor;
import org.terasology.logic.behavior.tree.Interpreter;
import org.terasology.logic.characters.CharacterMovementComponent;
import org.terasology.logic.location.LocationComponent;

public class FlexibleMovementNodeTest {
    protected boolean done;
    protected LocationComponent locationComponent;
    protected CharacterMovementComponent characterMovementComponent;
    protected FlexibleMovementComponent flexibleMovementComponent;
    protected Interpreter interpreter;
    private WorldProvidingHeadlessEnvironment env;
    private Actor actor;
    private EntityManager entityManager;

    @Before
    public void setup() {
        env = TestHelper.createEnvironment(new String[]{
                "XXXXX|XXXXX|XXXXX|XXXXX",
                "XXXXX|XXXXX|XXXXX|XXXXX",
                "XXXXX|XXXXX|XXXXX|XXXXX",
                "XXXXX|XXXXX|XXXXX|XXXXX",
                "XXXXX|XXXXX|XXXXX|XXXXX"
        }, new String[0]);
        locationComponent = new LocationComponent();
        characterMovementComponent = new CharacterMovementComponent();
        flexibleMovementComponent = new FlexibleMovementComponent();
        entityManager = env.getContext().get(EntityManager.class);
        actor = new Actor(entityManager.create(locationComponent, characterMovementComponent, flexibleMovementComponent));
        interpreter = new Interpreter(actor);
    }
}
