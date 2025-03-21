package com.kitisplode.golemdandori2.geckolib.block;

import com.kitisplode.golemdandori2.ExampleModCommon;
import com.kitisplode.golemdandori2.geckolib.block.entity.FertilizerBlockEntity;
import com.kitisplode.golemdandori2.geckolib.client.model.block.FertilizerModel;
import com.kitisplode.golemdandori2.geckolib.client.renderer.block.FertilizerBlockRenderer;
import com.kitisplode.golemdandori2.registry.BlockEntityRegistry;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Example animated block using GeckoLib animations.<br>
 * There's nothing to see here since the {@link Block} class itself has little to do with animations
 *
 * @see FertilizerModel FertilizerModel
 * @see FertilizerBlockEntity
 * @see FertilizerBlockRenderer FertilizerBlockRenderer
 */
public class FertilizerBlock extends DirectionalBlock implements EntityBlock {
	public FertilizerBlock(BlockBehaviour.Properties properties) {
		super(properties);
	}

	@Override
	protected MapCodec<? extends DirectionalBlock> codec() {
		return null;
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return BlockEntityRegistry.FERTILIZER_BLOCK.get().create(pos, state);
	}

	@Override
	public RenderShape getRenderShape(BlockState state) {
		return RenderShape.ENTITYBLOCK_ANIMATED;
	}

	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FACING);
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return defaultBlockState().setValue(FACING, context.getNearestLookingDirection().getOpposite());
	}

	@Override
	public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag tooltipFlag) {
		tooltip.add(Component.translatable("item." + ExampleModCommon.MODID + ".fertilizer.tooltip"));

		super.appendHoverText(stack, context, tooltip, tooltipFlag);
	}
}
