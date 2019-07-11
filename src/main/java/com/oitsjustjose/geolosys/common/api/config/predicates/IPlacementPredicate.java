package com.oitsjustjose.geolosys.common.api.config.predicates;

import net.minecraft.block.state.IBlockState;

public interface IPlacementPredicate {
    boolean matches(IBlockState block);
}
