// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.flexiblemovement;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.terasology.moduletestingenvironment.MTEExtension;
import org.terasology.moduletestingenvironment.ModuleTestingHelper;
import org.terasology.moduletestingenvironment.extension.Dependencies;
import org.terasology.moduletestingenvironment.extension.UseWorldGenerator;
import org.terasology.registry.In;

@ExtendWith(MTEExtension.class)
@Dependencies("FlexibleMovement")
@UseWorldGenerator("ModuleTestingEnvironment:dummy")
public class FlexibleMovementMTETest extends FlexibleMovementTestingEnvironment {
    @In
    ModuleTestingHelper helper;

    @Nested
    class FlyingMovementTest extends FlexibleMovementTestingEnvironment {
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

    @Nested
    class LargeLeapingMovementTest extends FlexibleMovementTestingEnvironment {
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

    @Nested
    class SwimmingMovementTest extends FlexibleMovementTestingEnvironment {
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

    @Nested
    class SwimmingWalkingLeapingMovementTest extends FlexibleMovementTestingEnvironment {
        @Test
        public void simpleLeap() throws InterruptedException {
            runTest(new String[]{
                    "~  |   ",
                    "~  |XXX"
            }, new String[]{
                    "?  |   ",
                    "1  |23!"
            });
        }

        @Test
        public void threeDimensionalMoves() throws InterruptedException {
            runTest(new String[]{
                    "~~~|~~ |   |   ",
                    "~ ~|~~~| XX| XX",
                    "~~~| ~ | XX| XX"
            }, new String[]{
                    "?  |   |   |   ",
                    "   | 1 |   |   ",
                    "   |   |  !|   "
            });
        }

        private void runTest(String[] world, String[] path) {
            executeExample(world, path, "walking", "leaping", "swimming");
        }
    }

    @Nested
    class WalkingLeapingMovementTest extends FlexibleMovementTestingEnvironment {
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
                    "XXX|XXX|XXX|XXX",
                    "X X|XXX|XXX|XXX",
                    "XXX|XX |XXX|XXX"
            }, new String[]{
                    "?  |   |   |   ",
                    "   | 1 |   |   ",
                    "   |   |  !|   "
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

        @Test
        public void jumpPillars() throws InterruptedException {
            runTest(new String[]{
                    "    X X X |    X X X |XXXXXXXXXX|XXXXXXXXXX|XXXXXXXXXX"
            }, new String[]{
                    "          |          |?        !|          |          "
            });
        }

        private void runTest(String[] world, String[] path) {
            executeExample(world, path, "walking", "leaping");
        }
    }
}
