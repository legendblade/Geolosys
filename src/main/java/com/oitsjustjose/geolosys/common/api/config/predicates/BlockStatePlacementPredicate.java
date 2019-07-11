package com.oitsjustjose.geolosys.common.api.config.predicates;

import com.oitsjustjose.geolosys.common.util.Utils;
import net.minecraft.block.state.IBlockState;

public class BlockStatePlacementPredicate implements IPlacementPredicate {
    private final IBlockState state;

    public BlockStatePlacementPredicate(IBlockState state) {
        this.state = state;
    }

    @Override
    public boolean matches(IBlockState block) {
        return Utils.doStatesMatch(block, state);
    }
}
