package com.oitsjustjose.geolosys.common.api.world;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

/**
 * Indicates this ore generator needs to determine its own placement rules
 */
public interface IOreWithState extends IOre {
    boolean tryPlace(World world, BlockPos blockpos, IBlockState state, Random rand);
}
