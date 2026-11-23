package com.cta.block;

import com.cta.registry.ModBlockEntities;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;

public class ScopeBlock extends BaseEntityBlock {

    public static final MapCodec<ScopeBlock> CODEC = simpleCodec(ScopeBlock::new);
    public static final EnumProperty<Direction> FACING = BlockStateProperties.FACING;

    private static final VoxelShape SHAPE = Block.box(5, 5, 5, 11, 11, 11);

    public ScopeBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getNearestLookingDirection().getOpposite());
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected InteractionResult useItemOn(ItemStack heldItem, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        BlockEntity be = level.getBlockEntity(pos);
        if (!(be instanceof ScopeBlockEntity scopeBE)) {
            return InteractionResult.FAIL;
        }

        // 1. Handle Wrench interactions first
        if (isCreateWrench(heldItem)) {
            if (level.isClientSide()) {
                return InteractionResult.SUCCESS;
            }
            return handleWrenchInteraction(state, level, pos, player, scopeBE);
        }

        // 2. Prevent the scope from opening if the player is sneaking
        if (player.isShiftKeyDown()) {
            return InteractionResult.PASS;
        }

        // 3. Open the Scope Viewer
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        if (player instanceof ServerPlayer serverPlayer) {
            if (scopeBE.isBeingViewed()) {
                return InteractionResult.FAIL;
            }
            scopeBE.startViewing(serverPlayer);
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        return useItemOn(ItemStack.EMPTY, state, level, pos, player, InteractionHand.MAIN_HAND, hit);
    }

    protected InteractionResult handleWrenchInteraction(BlockState state, Level level, BlockPos pos, Player player, ScopeBlockEntity scopeBE) {
        if (player.isShiftKeyDown()) {
            if (scopeBE.getBoundCannonMount() != null) {
                scopeBE.bindToCannonMount(null);
                player.sendOverlayMessage(Component.translatable("message.cta.scope.unbound"));
                return InteractionResult.SUCCESS;
            }
            float newPitch = scopeBE.getScopePitch() + 15;
            if (newPitch > 90) newPitch = -90 + (newPitch - 90);
            if (newPitch < -90) newPitch = 90 + (newPitch + 90);
            scopeBE.setScopeRotation(scopeBE.getScopeYaw(), newPitch);
        } else {
            float newYaw = scopeBE.getScopeYaw() + 15;
            if (newYaw >= 360) newYaw -= 360;
            if (newYaw < 0) newYaw += 360;
            scopeBE.setScopeRotation(newYaw, scopeBE.getScopePitch());
        }
        return InteractionResult.SUCCESS;
    }

    protected boolean isCreateWrench(ItemStack stack) {
        if (stack.isEmpty()) return false;
        Identifier registryName = BuiltInRegistries.ITEM.getKey(stack.getItem());
        return registryName.getNamespace().equals("create") && registryName.getPath().equals("wrench");
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ScopeBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (!level.isClientSide()) {
            return createTickerHelper(type, ModBlockEntities.SCOPE_BE.get(), ScopeBlockEntity::serverTick);
        }
        return null;
    }
}
