package com.cta.compat;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.Collections;

/**
 * Valkyrien Skies compatibility stub for MC 26.2 (VS is not present).
 */
public class VSCompat {

    public static boolean isVSLoaded() {
        return false;
    }

    @Nullable
    public static Object getShipManagingPos(Level level, BlockPos pos) {
        return null;
    }

    public static boolean isOnShip(Level level, BlockPos pos) {
        return false;
    }

    @Nullable
    public static Object getShipForBlockPos(Level level, BlockPos pos) {
        return null;
    }

    public static Vec3 toWorldCoordinatesRobust(Level level, BlockPos pos) {
        return Vec3.atCenterOf(pos);
    }

    public static boolean isBlockInShipyard(Level level, BlockPos pos) {
        return false;
    }

    public static BlockPos getShipLocalBlockPos(Level level, @Nullable BlockPos placementPos, Vec3 worldPos) {
        return BlockPos.containing(worldPos);
    }

    public static boolean updateEntityPositionOnShip(Entity entity, @Nullable BlockPos shipBlockPos, Vec3 shipLocalPos) {
        return false;
    }

    public static Vec3 getShipVelocity(Level level, @Nullable BlockPos shipBlockPos) {
        return Vec3.ZERO;
    }

    public static Vec3 toWorldCoordinates(Level level, BlockPos shipBlockPos, Vec3 shipLocalPos) {
        return shipLocalPos;
    }

    public static Vec3 transformDirectionToWorld(Level level, BlockPos shipBlockPos, Vec3 shipLocalDirection) {
        return shipLocalDirection;
    }

    public static Vec3 transformDirectionToShip(Level level, BlockPos shipBlockPos, Vec3 worldDirection) {
        return worldDirection;
    }

    public static float transformYawToShip(Level level, BlockPos shipBlockPos, float worldYaw) {
        return worldYaw;
    }

    public static float transformPitchToShip(Level level, BlockPos shipBlockPos, float worldYaw, float worldPitch) {
        return worldPitch;
    }

    public static Vec3 getShipVelocityAtPoint(Level level, BlockPos shipBlockPos, Vec3 worldPos) {
        return Vec3.ZERO;
    }

    public static void applyShipVelocityToEntity(Level level, BlockPos shipBlockPos, Entity entity) {
    }

    public static float transformYawToWorld(Level level, BlockPos shipBlockPos, float localYaw) {
        return localYaw;
    }

    public static float transformPitchToWorld(Level level, BlockPos shipBlockPos, float localYaw, float localPitch) {
        return localPitch;
    }

    public static float transformRollToWorld(Level level, BlockPos shipBlockPos, float localRoll) {
        return localRoll;
    }

    public static float transformRollToShip(Level level, BlockPos shipBlockPos, float worldRoll) {
        return worldRoll;
    }

    public static Iterable<Object> getAllLoadedShips(Level level) {
        return Collections.emptyList();
    }

    public static Vec3 toWorldCoordinatesWithShip(Object ship, Vec3 shipLocalPos) {
        return shipLocalPos;
    }

    public static Vec3 toShipCoordinatesWithShip(Object ship, Vec3 worldPos) {
        return worldPos;
    }
}
