package com.oitsjustjose.geolosys.common.config.dto;

import com.google.gson.annotations.Expose;

import java.util.List;

public class ConfigRoot {
    @Expose
    public List<OreDeposit> ores;
}
