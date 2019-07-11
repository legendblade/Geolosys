package com.oitsjustjose.geolosys.common.api.world;

import com.google.common.collect.ImmutableList;
import com.oitsjustjose.geolosys.common.api.GeolosysAPI;
import com.oitsjustjose.geolosys.common.api.config.AlternateDepositBlock;
import com.oitsjustjose.geolosys.common.api.config.DepositBlock;
import com.oitsjustjose.geolosys.common.api.config.OreDepositConfig;
import com.oitsjustjose.geolosys.common.util.Utils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class DepositMultiOreWithPredicate implements IOreWithState {
    private final ImmutableList<DepositBlock> ores;
    private final ImmutableList<DepositBlock> samples;

    private final int oreWeight;
    private final int sampleWeight;

    private final int yMin;
    private final int yMax;
    private int size;
    private int chance;
    private int[] dimensionBlacklist;
    private List<IBlockState> blockStateMatchers;
    private float density;

    public DepositMultiOreWithPredicate(OreDepositConfig config) {
        yMin = config.yMin;
        yMax = config.yMax;
        size = config.size;
        chance = config.chance;
        dimensionBlacklist = config.dimBlacklist.stream().mapToInt((i) -> i).toArray();
        blockStateMatchers = config.blockStateMatchers;
        density = config.density;

        ores = ImmutableList.copyOf(config.blocks.blocks);
        samples = ImmutableList.copyOf(config.samples.blocks);

        int weight = 0;
        for (DepositBlock o : ores) {
            weight += o.chance;
        }
        oreWeight = weight;

        weight = 0;
        for (DepositBlock o : samples) {
            weight += o.chance;
        }
        sampleWeight = weight;
    }

    @Override
    public IBlockState getOre() {
        return getRandomBlock(ores, oreWeight, new Random()).block;
    }

    @Override
    public IBlockState getSample() {
        return getRandomBlock(samples, sampleWeight, new Random()).block;
    }

    @Override
    public String getFriendlyName() {
        return String.join(
                " & ",
                ores
                    .stream()
                    .filter((o) -> !Blocks.AIR.equals(o.block.getBlock()))
                    .map((o) -> Utils.blockStateToStack(o.block).getDisplayName())
                    .collect(Collectors.toList())
        );
    }

    @Override
    public int getYMin() {
        return yMin;
    }

    @Override
    public int getYMax() {
        return yMax;
    }

    @Override
    public int getChance() {
        return chance;
    }

    @Override
    public int getSize() {
        return size;
    }

    @Override
    public int[] getDimensionBlacklist() {
        return dimensionBlacklist;
    }

    @Override
    public boolean canReplace(IBlockState state) {
        if (this.blockStateMatchers == null)
        {
            return true;
        }
        for (IBlockState s : this.blockStateMatchers)
        {
            if (s == state)
            {
                return true;
            }
        }
        return this.blockStateMatchers.contains(state);
    }

    @Override
    public boolean oreMatches(IBlockState other) {
        for (DepositBlock s : this.ores)
        {
            if (Utils.doStatesMatch(s.block, other))
            {
                return true;
            }

            for (AlternateDepositBlock alt : s.alternatives) {
                if (Utils.doStatesMatch(alt.block, other))
                {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean sampleMatches(IBlockState other) {
        for (DepositBlock s : this.samples) {
            if (Utils.doStatesMatch(s.block, other)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public List<IBlockState> getBlockStateMatchers() {
        return blockStateMatchers;
    }

    @Override
    public float getDensity() {
        return density;
    }

    @Override
    public boolean tryPlace(World world, BlockPos blockpos, IBlockState state, Random rand) {
        boolean foundMatch = false;
        for (IBlockState blockState : blockStateMatchers.size() <= 0 ? GeolosysAPI.replacementMats : blockStateMatchers) {
            if (!Utils.doStatesMatch(blockState, state)) continue;

            foundMatch = true;
            break;
        }

        if (!foundMatch) return false; // We do not belong here.

        DepositBlock randomBlock = getRandomBlock(ores, oreWeight, rand);

        IBlockState replacement = null;
        for (AlternateDepositBlock alt : randomBlock.alternatives) {
            if (!alt.canReplace(blockpos, state)) continue;

            replacement = alt.block;
            break;
        }

        if (replacement == null) replacement = randomBlock.block;
        if (Blocks.AIR.equals(replacement.getBlock())) return false;

        world.setBlockState(blockpos, replacement);
        return true;
    }

    /**
     * Gets a block from the set
     * @param set       The set to search
     * @param total     The total weight to search through
     * @param random    The random instance to use
     * @return  The randomly selected block
     */
    private static DepositBlock getRandomBlock(List<DepositBlock> set, int total, Random random) {
        int r = random.nextInt(total);

        for (DepositBlock block : set) {
            r -= block.chance;
            if (r <= 1) return block;
        }

        // If we failed to find a block somehow, return the first one:
        return set.get(0);
    }
}
