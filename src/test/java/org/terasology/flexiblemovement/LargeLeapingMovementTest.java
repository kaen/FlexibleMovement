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
public class LargeLeapingMovementTest extends FlexibleMovementTestingEnvironment {
    @Test
    public void smokeTest() throws InterruptedException {
        runTest(new String[]{
                "XXX XXX|XXX XXX|XXX XXX",
                "XXXXXXX|XXXXXXX|XXXXXXX",
                "XXX XXX|XXX XXX|XXX XXX",
                "XXXXXXX|XXXXXXX|XXXXXXX",
                "XXXXXXX|XXXXXXX|XXXXXXX",
                "XXXXXXX|XXXXXXX|XXXXXXX"
        }, new String[]{
                "       |       |       ",
                "       | ?   ! |       ",
                "       |       |       ",
                "       |       |       ",
                "       |       |       ",
                "       |       |       "
        });
    }

    @Test
    public void steps() {
        runTest(new String[]{
                "XXX      |XXXXX    |XXXXXXX  |XXXXXXXXX",
                "XXX      |XXXXX    |XXXXXXX  |XXXXXXXXX",
                "XXX      |XXXXX    |XXXXXXX  |XXXXXXXXX",
        }, new String[]{
                "         |         |         |         ",
                "         | ?       | 12!     |         ",
                "         |         |         |         ",
        });
    }

    private void runTest(String[] world, String[] path) {
        executeExample(world, path, 2.7f, 1.2f, "walking", "leaping");
    }
}
