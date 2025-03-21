package com.kitisplode.golemdandori2.util.golempatterns;

import com.kitisplode.golemdandori2.entity.interfaces.IEntityDandoriPik;
import com.kitisplode.golemdandori2.util.ExtraMath;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.block.state.pattern.BlockPattern;

//import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.function.Predicate;

public abstract class AbstractGolemPattern
{
    protected ArrayList<BlockPattern> patternList = new ArrayList<>();
    protected Predicate<BlockState> spawnBlockPredicate;

    protected Vec3i spawnPositionOffset = new Vec3i(0,0,0);
    public AbstractGolemPattern(Predicate<BlockState> pPredicate)
    {
        spawnBlockPredicate = pPredicate;
        // Override to add patterns.
    }

    // Checks to see if the pattern is matched, and returns null if not, a PatternMatch otherwise.
//    @Nullable
    public BlockPattern.BlockPatternMatch CheckForPatternMatch(Level pLevel, BlockPos pPos)
    {
        // Check to see if any of the patterns match.
        for (int i = 0; i < patternList.size(); i++)
        {
            BlockPattern currentPattern = patternList.get(i);
            // If somehow the current pattern is null, skip to the next pattern.
            if (currentPattern == null) continue;
            // Actually check the pattern now.
            BlockPattern.BlockPatternMatch match = currentPattern.find(pLevel, pPos);
            // If we got a match, return it.
            if (match != null) return match;
        }
        // If we didn't get any matches, return null.
        return null;
    }

    public ArrayList<Entity> SpawnGolem(Level pLevel, BlockPattern.BlockPatternMatch pPatternMatch, BlockPos pPos, Entity pPlayer)
    {
        clearPatternBlocks(pLevel, pPatternMatch);

        // Spawn the golems.
        ArrayList<Entity> pGolems = SpawnGolemForReal(pLevel, pPatternMatch, pPos);
        for (Entity pGolem : pGolems)
        {
            BlockPos spawnPosition = pPatternMatch.getBlock(spawnPositionOffset.getX(),
                            spawnPositionOffset.getY(),
                            spawnPositionOffset.getZ())
                    .getPos();
            positionGolem(pLevel,
                    spawnPosition,
                    (float) ExtraMath.getYawBetweenPoints(spawnPosition.getCenter(), pPlayer.position()) * Mth.DEG_TO_RAD,
                    pGolem);

            if (pGolem instanceof IEntityDandoriPik dandoriFollower && pPlayer instanceof LivingEntity player)
            {
//                if (pGolem instanceof EntityPawn pawn) pawn.setOwnerType(EntityPawn.OWNER_TYPES.PLAYER.ordinal());
                dandoriFollower.setOwner(player);
            }
        }

        updatePatternBlocks(pLevel, pPatternMatch);
        return pGolems;
    }

    // Intended to be overridden to actually spawn the golem.
    protected abstract ArrayList<Entity> SpawnGolemForReal(Level pLevel, BlockPattern.BlockPatternMatch pPatternMatch, BlockPos pPos);

    protected void positionGolem(Level pLevel, BlockPos pPos, float pYaw, Entity pGolem)
    {
        if (pGolem == null) return;
        pGolem.moveTo((double)pPos.getX() + 0.5D, (double)pPos.getY() + 0.05D, (double)pPos.getZ() + 0.5D, pYaw * Mth.RAD_TO_DEG, 0.0F);
        pLevel.addFreshEntity(pGolem);

        for(ServerPlayer serverplayer : pLevel.getEntitiesOfClass(ServerPlayer.class, pGolem.getBoundingBox().inflate(5.0D))) {
            CriteriaTriggers.SUMMONED_ENTITY.trigger(serverplayer, pGolem);
        }
    }

    private void clearPatternBlocks(Level pLevel, BlockPattern.BlockPatternMatch pPatternMatch)
    {
        for(int i = 0; i < pPatternMatch.getWidth(); ++i) {
            for(int j = 0; j < pPatternMatch.getHeight(); ++j) {
                for(int k = 0; k < pPatternMatch.getDepth(); ++k)
                {
                    BlockInWorld blockinworld = pPatternMatch.getBlock(i, j, k);
                    pLevel.setBlock(blockinworld.getPos(), Blocks.AIR.defaultBlockState(), 2);
                    pLevel.levelEvent(2001, blockinworld.getPos(), Block.getId(blockinworld.getState()));
                }
            }
        }
    }

    private void updatePatternBlocks(Level pLevel, BlockPattern.BlockPatternMatch pPatternMatch)
    {
        for(int i = 0; i < pPatternMatch.getWidth(); ++i) {
            for(int j = 0; j < pPatternMatch.getHeight(); ++j) {
                for(int k = 0; k < pPatternMatch.getDepth(); ++k) {
                    BlockInWorld blockinworld = pPatternMatch.getBlock(i, j, k);
                    pLevel.blockUpdated(blockinworld.getPos(), Blocks.AIR);
                }
            }
        }
    }
}
