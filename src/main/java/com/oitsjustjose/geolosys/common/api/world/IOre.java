package com.oitsjustjose.geolosys.common.api.world;

import java.util.List;

import com.oitsjustjose.geolosys.common.util.Utils;
import net.minecraft.block.state.IBlockState;

public interface IOre
{
    public IBlockState getOre();

    public IBlockState getSample();

    public String getFriendlyName();

    public int getYMin();

    public int getYMax();

    public int getChance();

    public int getSize();

    public int[] getDimensionBlacklist();

    public boolean canReplace(IBlockState state);

    public boolean oreMatches(IBlockState other);

    public boolean sampleMatches(IBlockState other);

    public List<IBlockState> getBlockStateMatchers();

    public float getDensity();

    /**
     * Gets a unique identifier for this ore deposit; the identifier should not change between loads for the same deposit.
     *
     * Mod makers are suggested to prefix their IDs with the name of their mod.
     * @return  A unique string for the vein.
     */
    default String getId() {
        return Utils.getDefaultIdForOre(this);
    }
}