package com.oitsjustjose.geolosys.common.api.world;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

/**
 * Indicates this ore generator needs to determine its own placement rules
 */
public interface IOreWithState extends IOre {
    /**
     * Try to place a block from this ore in the given world
     * @param world     The world to place in
     * @param blockpos  The block position to place at
     * @param state     The existing block in this position
     * @param rand      The random generator to use
     * @return          True if a block was placed, false otherwise
     */
    boolean tryPlace(World world, BlockPos blockpos, IBlockState state, Random rand);

    /**
     * Used by the prospecting pick to determine if this ore matches
     * @param isOreSearchMode   True if the pick is looking for ores, false if it's looking for samples
     * @param state             The block state currently being checked
     * @return                  True if this ore matches, false otherwise
     */
    boolean getProspectingResults(boolean isOreSearchMode, IBlockState state);
}
