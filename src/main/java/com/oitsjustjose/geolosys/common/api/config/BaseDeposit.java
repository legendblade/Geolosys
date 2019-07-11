package com.oitsjustjose.geolosys.common.api.config;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.List;

public class BaseDeposit {
    @Expose
    public int yMin;

    @Expose
    public int yMax;

    @Expose
    public int chance;

    @Expose
    public int size;

    @Expose
    public List<Integer> dimBlacklist;

    public BaseDeposit() {
        chance = -1;
        dimBlacklist = new ArrayList<>();
        yMin = -1;
        yMax = -1;
        size = -1;
    }
}

