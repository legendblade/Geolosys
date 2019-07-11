package com.oitsjustjose.geolosys.common.config;

import com.google.gson.annotations.Expose;
import com.oitsjustjose.geolosys.common.api.config.OreDepositConfig;
import com.oitsjustjose.geolosys.common.api.config.StoneDepositConfig;

import java.util.List;

public class ConfigRoot {
    @Expose
    public List<OreDepositConfig> ores;

    @Expose
    public List<StoneDepositConfig> stones;
}
