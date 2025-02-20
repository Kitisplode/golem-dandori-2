package com.kitisplode.golemdandori2.mixin.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CarvedPumpkinBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(value = CarvedPumpkinBlock.class)
public abstract class MixinCarvedPumpkinBlock extends HorizontalDirectionalBlock
{

    protected MixinCarvedPumpkinBlock(Properties properties)
    {
        super(properties);
    }

    @Override
    @Overwrite
    public void onPlace(BlockState state, Level level, BlockPos bp, BlockState bs, boolean isMoving)
    {
        // Do nothing, to prevent normal golem construction.
    }
}
