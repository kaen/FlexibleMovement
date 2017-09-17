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

import org.junit.Test;

public class WalkingLeapingMovementTest extends FlexibleMovementTestingEnvironment {
    @Test
    public void simpleStraight() throws InterruptedException {
        runTest(new String[]{
                "X  ",
                "X  ",
                "XXX",
        }, new String[]{
                "?  ",
                "1  ",
                "!  "
        });
    }

    @Test
    public void simpleLeap() throws InterruptedException {
        runTest(new String[]{
                "X  |XXX|XXX|XXX",
                "X  |XXX|XXX|XXX",
        }, new String[]{
                "?  |123|XXX|XXX",
                "   |  !|XXX|XXX",
        });
    }

    @Test
    public void simpleDiagonal() throws InterruptedException {
        runTest(new String[]{
                "X  |X  ",
                "X  |X  ",
                "XXX|XXX"
        }, new String[]{
                "?  |   ",
                "1  |   ",
                "23!|   "
        });
    }

    @Test
    public void corridor() throws InterruptedException {
        runTest(new String[]{
                "XXXXXXXXXXXXXXX",
                "X            XX",
                "X XXXXXXXXXXXXX",
                "XXX            ",
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
                "XXX|XX |   ",
                "X X|XXX| XX",
                "XXX| X | XX"
        }, new String[]{
                "?  |   |   ",
                "   | 1 |   ",
                "   |   |  !"
        });
    }

    @Test
    public void jumpOver() throws InterruptedException {
        runTest(new String[]{
                "X X|XXX|XXX|XXX"
        }, new String[]{
                "? !|123|   |   "
        });
    }

    private void runTest(String[] world, String[] path) {
        executeExample(world, path, "walking", "leaping");
    }
}
