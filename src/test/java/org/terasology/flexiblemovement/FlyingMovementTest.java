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

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.terasology.moduletestingenvironment.MTEExtension;
import org.terasology.moduletestingenvironment.extension.Dependencies;
import org.terasology.moduletestingenvironment.extension.UseWorldGenerator;

@ExtendWith(MTEExtension.class)
@Dependencies("FlexibleMovement")
@UseWorldGenerator("ModuleTestingEnvironment:dummy")
public class FlyingMovementTest extends FlexibleMovementTestingEnvironment {
    @Test
    public void simpleWall() throws InterruptedException {
        runTest(new String[]{
                "XXX|XXX|XXX",
                "   |   |XXX",
                "XXX|XXX|XXX",
        }, new String[]{
                "?  |   |   ",
                "   |   |   ",
                "  !|   |   "
        });
    }

    private void runTest(String[] world, String[] path) {
        executeExample(world, path, "flying");
    }
}
