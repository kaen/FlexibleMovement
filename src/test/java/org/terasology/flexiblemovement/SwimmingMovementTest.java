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
public class SwimmingMovementTest extends FlexibleMovementTestingEnvironment {
    @Test
    public void simpleStraight() throws InterruptedException {
        runTest(new String[]{
                "~  ",
                "~  ",
                "~~~",
        }, new String[]{
                "?  ",
                "1  ",
                "!  "
        });
    }

    @Test
    public void simpleLeap() throws InterruptedException {
        runTest(new String[]{
                "~  |~~~|~~~",
                "~  |~~~|~~~",
                "~~~|~~~|~~~",
        }, new String[]{
                "?  |123|~~~",
                "   |  !|~~~",
                "   |   |~~~"
        });
    }

    @Test
    public void simpleDiagonal() throws InterruptedException {
        runTest(new String[]{
                "~  |~  ",
                "~  |~  ",
                "~~~|~~~"
        }, new String[]{
                "?  |   ",
                "1  |   ",
                "23!|   "
        });
    }

    @Test
    public void corridor() throws InterruptedException {
        runTest(new String[]{
                "~~~~~~~~~~~~~~~",
                "~            ~~",
                "~ ~~~~~~~~~~~~~",
                "~~~            ",
                "               ",
        }, new String[]{
                "?123456789abcd ",
                "             e ",
                "  qponmlkjihgf ",
                "  !            ",
                "               ",
        });

    }

    @Test
    public void threeDimensionalMoves() throws InterruptedException {
        runTest(new String[]{
                "~~~|~~ |   ",
                "~ ~|~~~| ~~",
                "~~~| ~ | ~~"
        }, new String[]{
                "?  |   |   ",
                "   | 1 |   ",
                "   |   |  !"
        });
    }

    private void runTest(String[] world, String[] path) {
        executeExample(world, path, "swimming");
    }
}
