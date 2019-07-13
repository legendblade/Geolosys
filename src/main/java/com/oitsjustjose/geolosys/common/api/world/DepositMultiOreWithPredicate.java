package com.oitsjustjose.geolosys.common.api.world;

import com.google.common.collect.ImmutableList;
import com.oitsjustjose.geolosys.common.api.GeolosysAPI;
import com.oitsjustjose.geolosys.common.api.config.BaseDepositBlock;
import com.oitsjustjose.geolosys.common.api.config.DepositBlock;
import com.oitsjustjose.geolosys.common.api.config.OreDepositConfig;
import com.oitsjustjose.geolosys.common.config.ModConfig;
import com.oitsjustjose.geolosys.common.util.Utils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class DepositMultiOreWithPredicate implements IOreWithState {
    private final String id;

    private final ImmutableList<DepositBlock> ores;
    private final ImmutableList<DepositBlock> samples;

    private final ImmutableList<IBlockState> allPossibleGenerations;
    private final ImmutableList<IBlockState> allPossibleSamples;

    private final int oreWeight;
    private final int sampleWeight;

    private final int yMin;
    private final int yMax;
    private int size;
    private int chance;
    private int[] dimensionBlacklist;
    private List<IBlockState> blockStateMatchers;
    private float density;

    private final String friendlyName;

    public DepositMultiOreWithPredicate(OreDepositConfig config) {
        id = config.id;
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

        // Calculate out our full ore list:
        List<IBlockState> blocks = new ArrayList<>();
        for (DepositBlock block : config.blocks.blocks) {
            blocks.add(block.block);
            for (BaseDepositBlock alt : block.alternatives) {
                blocks.add(alt.block);
            }
        }

        allPossibleGenerations = ImmutableList.copyOf(blocks.stream().distinct().collect(Collectors.toList()));

        // And our full list of samples
        blocks.clear();
        for (DepositBlock block : config.samples.blocks) {
            blocks.add(block.block);
            for (BaseDepositBlock alt : block.alternatives) {
                blocks.add(alt.block);
            }
        }

        allPossibleSamples = ImmutableList.copyOf(blocks.stream().distinct().collect(Collectors.toList()));

        // Lastly, our name:
        if (config.name != null && !config.name.isEmpty()) {
            friendlyName = config.name;
        } else {
            friendlyName = String.join(
                    " & ",
                    allPossibleGenerations
                            .stream()
                            .filter((o) -> !Blocks.AIR.equals(o.getBlock()))
                            .map((o) -> Utils.blockStateToStack(o).getDisplayName())
                            .distinct()
                            .sorted()
                            .limit(5)
                            .collect(Collectors.toList())
            );
        }
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public IBlockState getOre() {
        DepositBlock block = getRandomBlock(ores, oreWeight, new Random());
        return block != null ? block.block : Blocks.AIR.getDefaultState();
    }

    @Override
    public IBlockState getSample() {
        DepositBlock block = getRandomBlock(samples, sampleWeight, new Random());
        return block != null ? block.block : Blocks.AIR.getDefaultState();
    }

    @Override
    public String getFriendlyName() {
        return friendlyName;
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

            for (BaseDepositBlock alt : s.alternatives) {
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

        // Check if there are any predicates on the base block:
        if(randomBlock == null || !randomBlock.canReplace(blockpos, state)) return false;

        IBlockState replacement = null;
        if (randomBlock.alternatives != null) {
            for (BaseDepositBlock alt : randomBlock.alternatives) {
                if (!alt.canReplace(blockpos, state)) continue;

                replacement = alt.block;
                break;
            }
        }

        if (replacement == null) replacement = randomBlock.block;
        if (Blocks.AIR.equals(replacement.getBlock())) return false;

        world.setBlockState(blockpos, replacement);
        return true;
    }

    @Override
    public boolean prospectThisOre(ModConfig.Prospecting.SURFACE_PROSPECTING_TYPE searchType, IBlockState state) {
        for (IBlockState block : searchType == ModConfig.Prospecting.SURFACE_PROSPECTING_TYPE.OREBLOCKS ? allPossibleGenerations : allPossibleSamples) {
            if (Utils.doStatesMatch(state, block)) return true;
        }

        return false;
    }

    /**
     * Gets a block from the set
     * @param set       The set to search
     * @param total     The total weight to search through
     * @param random    The random instance to use
     * @return  The randomly selected block
     */
    private static DepositBlock getRandomBlock(List<DepositBlock> set, int total, Random random) {
        if (total <= 0) return null;

        int r = random.nextInt(total);

        for (DepositBlock block : set) {
            r -= block.chance;
            if (r <= 1) return block;
        }

        // If we failed to find a block somehow, return the first one:
        return set.get(0);
    }
}
