// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.flexiblemovement.plugin;

import org.joml.Vector3ic;
import org.terasology.engine.Time;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.flexiblemovement.FlexibleMovementComponent;
import org.terasology.flexiblepathfinding.plugins.JPSPlugin;
import org.terasology.flexiblepathfinding.plugins.basic.ClimbingPlugin;
import org.terasology.logic.characters.CharacterMoveInputEvent;
import org.terasology.logic.characters.CharacterMovementComponent;
import org.terasology.logic.location.LocationComponent;
import org.terasology.math.TeraMath;
import org.terasology.math.geom.Quat4f;
import org.terasology.math.geom.Vector3f;
import org.terasology.math.geom.Vector3i;
import org.terasology.world.WorldProvider;
import org.terasology.world.block.Block;

import java.math.RoundingMode;

public class ClimbingMovementPlugin extends MovementPlugin {
    public ClimbingMovementPlugin(WorldProvider world, Time time) {
        super(world, time);
    }
    public ClimbingMovementPlugin() {
        super();
    }

    @Override
    public JPSPlugin getJpsPlugin(EntityRef entity) {
        CharacterMovementComponent component = entity.getComponent(CharacterMovementComponent.class);
        return new ClimbingPlugin(getWorld(), component.radius * 2.0f, component.height);
    }

    @Override
    public CharacterMoveInputEvent move(EntityRef entity, Vector3f dest, int sequence, long deltaMs) {
        Vector3f delta = getDelta(entity, dest);
        CharacterMovementComponent movement = entity.getComponent(CharacterMovementComponent.class);

        float pitch;
        float yaw;
        Vector3f direction;

        boolean movingVertically = Math.abs(delta.y) > entity.getComponent(FlexibleMovementComponent.class).targetTolerance;
        if (movingVertically) {
            // in KinematicCharacterMover climbing is special. +Z means "up the ladder" and -Z means "down the ladder"
            // see KinematicCharacterMover#climb for info
            pitch = delta.y >= 0 ? 90f : -90f;
        } else {
            pitch = getPitch(delta);
        }

        // looking at the ladder has different mechanics than looking away from it, so always look at it to keep it simple
        Vector3i pos = new Vector3i(entity.getComponent(LocationComponent.class).getWorldPosition(), RoundingMode.HALF_UP);
        Block block = getWorld().getBlock(pos);
        Vector3f ladderDirection = block.getDirection().toDirection().getVector3f();
        Quat4f rotation = new Quat4f(ladderDirection, 0);
        rotation.inverse();
        rotation.rotate(delta);
        yaw = rotation.getYaw() * TeraMath.RAD_TO_DEG;

        return new CharacterMoveInputEvent(sequence, pitch, yaw, delta, false, false, false, deltaMs);
    }
}
