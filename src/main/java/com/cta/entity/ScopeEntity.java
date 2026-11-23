package com.cta.entity;

import com.cta.compat.CBCCompat;
import com.cta.compat.VSCompat;
import com.cta.network.PacketHandler;
import com.cta.network.StartScopeViewPacket;
import com.cta.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.entity.IEntityWithComplexSpawn;

import javax.annotation.Nullable;
import java.lang.ref.WeakReference;

public class ScopeEntity extends Entity implements IEntityWithComplexSpawn {
    private static final EntityDataAccessor<Float> DATA_YAW = SynchedEntityData.defineId(ScopeEntity.class,
            EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> DATA_PITCH = SynchedEntityData.defineId(ScopeEntity.class,
            EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> DATA_ROLL = SynchedEntityData.defineId(ScopeEntity.class,
            EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> DATA_HEALTH = SynchedEntityData.defineId(ScopeEntity.class,
            EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Boolean> DATA_IS_VIEWING = SynchedEntityData.defineId(ScopeEntity.class,
            EntityDataSerializers.BOOLEAN);

    public ItemStack modelItem = ItemStack.EMPTY;

    @Nullable
    protected BlockPos placementBlockPos = null;
    @Nullable
    protected Vec3 shipLocalPosition = null;
    protected float shipLocalYaw = 0.0f;
    protected float shipLocalPitch = 0.0f;
    protected float shipLocalRoll = 0.0f;

    protected WeakReference<ServerPlayer> currentViewer = new WeakReference<>(null);

    @Nullable
    protected BlockPos boundCannonMount = null;

    public ScopeEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
        this.noPhysics = true;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(DATA_YAW, 0.0f);
        builder.define(DATA_PITCH, 0.0f);
        builder.define(DATA_ROLL, 0.0f);
        builder.define(DATA_HEALTH, 10.0f);
        builder.define(DATA_IS_VIEWING, false);
    }

    public void setStoredRotation(float yaw, float pitch) {
        this.entityData.set(DATA_YAW, yaw);
        this.entityData.set(DATA_PITCH, pitch);
        this.setYRot(yaw);
        this.setXRot(pitch);
    }

    public float getStoredYaw() {
        return this.entityData.get(DATA_YAW);
    }

    public float getStoredPitch() {
        return this.entityData.get(DATA_PITCH);
    }

    public float getStoredRoll() {
        return this.entityData.get(DATA_ROLL);
    }

    public boolean isBeingViewed() {
        return this.entityData.get(DATA_IS_VIEWING);
    }

    public void setPlacementBlockPos(@Nullable BlockPos pos) {
        this.placementBlockPos = pos;
        if (pos != null) {
            this.shipLocalPosition = new Vec3(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
        }
    }

    public void setShipLocalPosition(@Nullable Vec3 pos) {
        this.shipLocalPosition = pos;
    }

    public void setShipLocalRotation(float yaw, float pitch) {
        this.shipLocalYaw = yaw;
        this.shipLocalPitch = pitch;
    }

    public void setShipLocalRoll(float roll) {
        this.shipLocalRoll = roll;
    }

    public void bindToCannonMount(@Nullable BlockPos mountPos) {
        this.boundCannonMount = mountPos;
    }

    @Nullable
    public BlockPos getBoundCannonMount() {
        return this.boundCannonMount;
    }

    @Override
    public void tick() {
        super.tick();

        if (boundCannonMount != null && CBCCompat.isCBCLoaded()) {
            Float cannonYaw = CBCCompat.getCannonMountYaw(this.level(), boundCannonMount);
            Float cannonPitch = CBCCompat.getCannonMountPitch(this.level(), boundCannonMount);

            if (cannonYaw != null && cannonPitch != null) {
                this.shipLocalYaw = cannonYaw;
                this.shipLocalPitch = cannonPitch;

                if (shipLocalPosition == null || placementBlockPos == null) {
                    this.entityData.set(DATA_YAW, cannonYaw);
                    this.entityData.set(DATA_PITCH, cannonPitch);
                    this.setYRot(cannonYaw);
                    this.setXRot(cannonPitch);
                }
            } else {
                boundCannonMount = null;
            }
        }

        if (shipLocalPosition != null && placementBlockPos != null) {
            Vec3 shipVelocity = VSCompat.getShipVelocity(this.level(), placementBlockPos);
            VSCompat.updateEntityPositionOnShip(this, placementBlockPos, shipLocalPosition);
            this.setDeltaMovement(shipVelocity);

            float worldYaw = VSCompat.transformYawToWorld(this.level(), placementBlockPos, shipLocalYaw);
            float worldPitch = VSCompat.transformPitchToWorld(this.level(), placementBlockPos, shipLocalYaw,
                    shipLocalPitch);
            float worldRoll = VSCompat.transformRollToWorld(this.level(), placementBlockPos, shipLocalRoll);
            this.entityData.set(DATA_YAW, worldYaw);
            this.entityData.set(DATA_PITCH, worldPitch);
            this.entityData.set(DATA_ROLL, worldRoll);

            this.setYRot(worldYaw);
            this.setXRot(worldPitch);

            this.yRotO = this.getYRot();
            this.xRotO = this.getXRot();
        }

        if (!this.level().isClientSide()) {
            ServerPlayer viewer = currentViewer.get();
            if (viewer != null) {
                if (viewer.isDeadOrDying() || viewer.level() != this.level() ||
                        viewer.distanceToSqr(this) > 128 * 128) {
                    stopViewing(viewer);
                }
            }
        }
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand hand, Vec3 location) {
        if (!this.level().isClientSide()) {
            ItemStack heldItem = player.getItemInHand(hand);

            if (isCreateWrench(heldItem)) {
                if (player.isShiftKeyDown()) {
                    if (CBCCompat.isCBCLoaded()) {
                        BlockPos found = findNearbyCannonMount(5);
                        if (found != null) {
                            bindToCannonMount(found);
                            player.sendOverlayMessage(Component.literal("Scope bound to cannon mount"));
                            return InteractionResult.SUCCESS;
                        } else if (boundCannonMount != null) {
                            boundCannonMount = null;
                            player.sendOverlayMessage(Component.literal("Scope unbound from cannon"));
                            return InteractionResult.SUCCESS;
                        } else {
                            player.sendOverlayMessage(Component.literal("No cannon mount found nearby"));
                        }
                    }

                    float newPitch = getStoredPitch() + 15;
                    if (newPitch > 90)
                        newPitch = -90 + (newPitch - 90);
                    if (newPitch < -90)
                        newPitch = 90 + (newPitch + 90);
                    setStoredRotation(getStoredYaw(), newPitch);
                    this.shipLocalPitch = newPitch;
                } else {
                    float newYaw = getStoredYaw() + 15;
                    if (newYaw >= 360)
                        newYaw -= 360;
                    if (newYaw < 0)
                        newYaw += 360;
                    setStoredRotation(newYaw, getStoredPitch());
                    this.shipLocalYaw = newYaw;
                }
                return InteractionResult.SUCCESS;
            }

            if (player.isShiftKeyDown()) {
                if (!player.getAbilities().instabuild) {
                    player.getInventory()
                            .add(modelItem.isEmpty() ? new ItemStack(ModItems.PANTHER_SCOPE.get())
                                    : modelItem.copy());
                }
                this.discard();
                return InteractionResult.SUCCESS;
            }

            if (player instanceof ServerPlayer serverPlayer) {
                if (isBeingViewed()) {
                    return InteractionResult.FAIL;
                }
                startViewing(serverPlayer);
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.SUCCESS;
    }

    public void startViewing(ServerPlayer player) {
        this.currentViewer = new WeakReference<>(player);
        this.entityData.set(DATA_IS_VIEWING, true);

        PacketHandler.sendToPlayer(player, new StartScopeViewPacket(this.blockPosition()));
    }

    public void stopViewing(ServerPlayer player) {
        if (this.currentViewer.get() == player) {
            this.currentViewer.clear();
            this.entityData.set(DATA_IS_VIEWING, false);
        }
    }

    protected boolean isCreateWrench(ItemStack stack) {
        if (stack.isEmpty())
            return false;
        Identifier registryName = BuiltInRegistries.ITEM.getKey(stack.getItem());
        return registryName.getNamespace().equals("create") && registryName.getPath().equals("wrench");
    }

    @Nullable
    protected BlockPos findNearbyCannonMount(int radius) {
        if (!CBCCompat.isCBCLoaded())
            return null;

        BlockPos center = this.blockPosition();
        BlockPos closest = null;
        double closestDist = Double.MAX_VALUE;

        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    BlockPos check = center.offset(x, y, z);
                    if (CBCCompat.isCannonMount(this.level(), check)) {
                        double dist = center.distSqr(check);
                        if (dist < closestDist) {
                            closestDist = dist;
                            closest = check;
                        }
                    }
                }
            }
        }

        return closest;
    }

    @Override
    public boolean hurtServer(ServerLevel level, DamageSource source, float amount) {
        if (this.isInvulnerableToBase(source))
            return false;

        float health = this.entityData.get(DATA_HEALTH);
        health -= amount;
        this.entityData.set(DATA_HEALTH, health);

        if (health <= 0) {
            ServerPlayer viewer = currentViewer.get();
            if (viewer != null) {
                stopViewing(viewer);
            }
            this.discard();
            return true;
        }
        return true;
    }

    @Override
    public boolean isPickable() {
        return true;
    }

    @Override
    public boolean isNoGravity() {
        return true;
    }

    @Override
    public EntityDimensions getDimensions(Pose pose) {
        return EntityDimensions.scalable(0.5f, 0.5f).withEyeHeight(0.25f);
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        this.entityData.set(DATA_YAW, input.getFloatOr("StoredYaw", 0.0f));
        this.entityData.set(DATA_PITCH, input.getFloatOr("StoredPitch", 0.0f));
        this.entityData.set(DATA_HEALTH, input.getFloatOr("Health", 10.0f));
        this.modelItem = input.read("ModelItem", ItemStack.OPTIONAL_CODEC).orElse(ItemStack.EMPTY);
        input.getInt("PlacementX").ifPresent(px -> this.placementBlockPos = new BlockPos(
                px, input.getIntOr("PlacementY", 0), input.getIntOr("PlacementZ", 0)));
        if (input.getInt("ShipLocalX").isPresent() || input.getDoubleOr("ShipLocalX", Double.NaN) == input.getDoubleOr("ShipLocalX", Double.NaN)) {
            double sx = input.getDoubleOr("ShipLocalX", Double.NaN);
            if (!Double.isNaN(sx)) {
                this.shipLocalPosition = new Vec3(
                        sx,
                        input.getDoubleOr("ShipLocalY", 0.0),
                        input.getDoubleOr("ShipLocalZ", 0.0));
            }
        }
        this.shipLocalYaw = input.getFloatOr("ShipLocalYaw", 0.0f);
        this.shipLocalPitch = input.getFloatOr("ShipLocalPitch", 0.0f);
        this.shipLocalRoll = input.getFloatOr("ShipLocalRoll", 0.0f);

        input.getInt("BoundCannonX").ifPresent(bx -> this.boundCannonMount = new BlockPos(
                bx, input.getIntOr("BoundCannonY", 0), input.getIntOr("BoundCannonZ", 0)));
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        output.putFloat("StoredYaw", this.entityData.get(DATA_YAW));
        output.putFloat("StoredPitch", this.entityData.get(DATA_PITCH));
        output.putFloat("Health", this.entityData.get(DATA_HEALTH));
        output.store("ModelItem", ItemStack.OPTIONAL_CODEC, this.modelItem);
        if (this.placementBlockPos != null) {
            output.putInt("PlacementX", this.placementBlockPos.getX());
            output.putInt("PlacementY", this.placementBlockPos.getY());
            output.putInt("PlacementZ", this.placementBlockPos.getZ());
        }
        if (this.shipLocalPosition != null) {
            output.putDouble("ShipLocalX", this.shipLocalPosition.x);
            output.putDouble("ShipLocalY", this.shipLocalPosition.y);
            output.putDouble("ShipLocalZ", this.shipLocalPosition.z);
        }
        output.putFloat("ShipLocalYaw", this.shipLocalYaw);
        output.putFloat("ShipLocalPitch", this.shipLocalPitch);
        output.putFloat("ShipLocalRoll", this.shipLocalRoll);

        if (this.boundCannonMount != null) {
            output.putInt("BoundCannonX", this.boundCannonMount.getX());
            output.putInt("BoundCannonY", this.boundCannonMount.getY());
            output.putInt("BoundCannonZ", this.boundCannonMount.getZ());
        }
    }

    @Override
    public void writeSpawnData(RegistryFriendlyByteBuf buffer) {
        buffer.writeFloat(this.entityData.get(DATA_YAW));
        buffer.writeFloat(this.entityData.get(DATA_PITCH));
        ItemStack.OPTIONAL_STREAM_CODEC.encode(buffer, this.modelItem);
    }

    @Override
    public void readSpawnData(RegistryFriendlyByteBuf buffer) {
        this.entityData.set(DATA_YAW, buffer.readFloat());
        this.entityData.set(DATA_PITCH, buffer.readFloat());
        this.modelItem = ItemStack.OPTIONAL_STREAM_CODEC.decode(buffer);
        this.setYRot(getStoredYaw());
        this.setXRot(getStoredPitch());
    }

    public ItemStack getModelItem() {
        return this.modelItem;
    }
}
