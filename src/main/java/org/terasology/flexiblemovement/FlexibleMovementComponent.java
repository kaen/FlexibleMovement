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

import com.google.common.collect.Lists;
import org.terasology.engine.Time;
import org.terasology.entitySystem.Component;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.flexiblemovement.plugin.MovementPlugin;
import org.terasology.flexiblemovement.plugin.FlyingMovementPlugin;
import org.terasology.flexiblemovement.plugin.WalkingMovementPlugin;
import org.terasology.logic.location.LocationComponent;
import org.terasology.math.geom.Vector3f;
import org.terasology.math.geom.Vector3i;
import org.terasology.world.WorldProvider;

import java.util.List;
import java.util.Vector;

public final class FlexibleMovementComponent implements Component {
    // immediate movement target
    public Vector3i target = Vector3i.zero();

    /**
     * The maximum distance from a target before it is considered to be reached
     */
    public float targetTolerance = 0.5f;

    public PathStatus pathStatus;

    // an entity to take the goal position from
    private EntityRef pathGoalEntity = null;

    // last known goal position
    private Vector3i pathGoalPosition = Vector3i.zero();

    // acceptable distance from goal for completion
    public double pathGoalDistance = 0;

    // generated path to goal
    private List<Vector3i> path = Lists.newArrayList();

    // current index along path above
    private int pathIndex = 0;

    public List<String> movementTypes = Lists.newArrayList("walking", "leaping");
    public boolean collidedHorizontally;
    public float lastInput;
    public int sequenceNumber;

    public void setPathGoal(EntityRef entity) {
        pathGoalEntity = entity;
        resetPath();
    }

    public void setPathGoal(Vector3i pos) {
        pathGoalPosition.set(pos);
        pathGoalEntity = null;
        resetPath();
    }

    public Vector3i getPathGoal() {
        if(pathGoalEntity != null && pathGoalEntity.getComponent(LocationComponent.class) != null) {
            Vector3f worldPosition = pathGoalEntity.getComponent(LocationComponent.class).getWorldPosition();
            Vector3i pos = FlexibleMovementHelper.posToBlock(worldPosition);
            pathGoalPosition.set(pos);
        }
        return pathGoalPosition;
    }

    public void resetPath() {
        path = Lists.newArrayList();
        pathIndex = 0;
        pathStatus = null;
    }

    public void advancePath() {
        pathIndex += 1;
        if(pathIndex < path.size()) {
            target = path.get(pathIndex);
        }
    }

    public boolean isPathFinished() {
        return pathIndex >= path.size();
    }

    public void setPath(List<Vector3i> path) {
        resetPath();
        this.pathStatus = PathStatus.SUCCESS;
        this.path.addAll(path);
        if(pathIndex < path.size()) {
            target = path.get(pathIndex);
        }
    }

    public List<Vector3i> getPath() {
        return path;
    }

    public int getPathIndex() {
        return pathIndex;
    }
}
