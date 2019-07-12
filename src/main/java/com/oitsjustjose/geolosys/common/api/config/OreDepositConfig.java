package com.oitsjustjose.geolosys.common.api.config;

import com.google.gson.annotations.Expose;
import net.minecraft.block.state.IBlockState;

import java.util.ArrayList;
import java.util.List;

public class OreDepositConfig extends BaseDeposit {
    @Expose
    public String name;

    @Expose
    public List<IBlockState> blockStateMatchers;

    @Expose
    public DepositBlockSet blocks;

    @Expose
    public DepositBlockSet samples;

    @Expose
    public float density;

    @Expose
    public List<String> biomes;

    @Expose
    public boolean isWhitelist;

    public OreDepositConfig() {
        super();
        density = 1.0f;
        blockStateMatchers = new ArrayList<>();
        biomes = new ArrayList<>();
        isWhitelist = true;
    }
}
