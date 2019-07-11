package com.oitsjustjose.geolosys.common.config.dto;

import com.google.gson.annotations.Expose;
import net.minecraft.block.state.IBlockState;

import java.util.List;

public class OreDeposit {
    @Expose
    public List<IBlockState> blockStateMatchers;

    @Expose
    public DepositBlockSet blocks;

    @Expose
    public DepositBlockSet samples;

    @Expose
    public int yMin;

    @Expose
    public int yMax;

    @Expose
    public int chance;

    @Expose
    public int size;

    @Expose
    public float density;

    @Expose
    public List<Integer> dimBlacklist;

    @Expose
    public List<String> biomes;

    @Expose
    public boolean isWhitelist;
}
