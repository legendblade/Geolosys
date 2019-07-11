package com.oitsjustjose.geolosys.common.api.world;

import java.util.HashMap;
import java.util.List;

import net.minecraft.block.state.IBlockState;
import net.minecraft.world.biome.Biome;

public class DepositMultiOreBiomeRestricted extends DepositMultiOre implements IBiomeRestrictedOreGen
{
    private List<Biome> biomes;
    private boolean useWhitelist;

    public DepositMultiOreBiomeRestricted(HashMap<IBlockState, Integer> oreBlocks,
            HashMap<IBlockState, Integer> sampleBlocks, int yMin, int yMax, int size, int chance,
            int[] dimensionBlacklist, List<IBlockState> blockStateMatchers, List<Biome> biomes, boolean useWhitelist,
            float density)
    {
        super(oreBlocks, sampleBlocks, yMin, yMax, size, chance, dimensionBlacklist, blockStateMatchers, density);
        this.biomes = biomes;
        this.useWhitelist = useWhitelist;
    }

    public boolean canPlaceInBiome(Biome biome)
    {
        // Manually check this since it's always a fucking pain
        for (Biome b : this.biomes)
        {
            if (b == biome)
            {
                return true;
            }
        }
        return false;
    }

    public boolean useWhitelist()
    {
        return this.useWhitelist;
    }

    public boolean useBlacklist()
    {
        return !this.useWhitelist;
    }

    public List<Biome> getBiomeList()
    {
        return this.biomes;
    }
}