package com.oitsjustjose.geolosys.common.api.config.predicates;

import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.annotations.JsonAdapter;
import com.oitsjustjose.geolosys.common.util.Utils;
import net.minecraft.block.state.IBlockState;

import java.lang.reflect.Type;

@JsonAdapter(BlockStatePlacementPredicate.Serializer.class)
public class BlockStatePlacementPredicate implements IPlacementPredicate {
    private final IBlockState state;

    public BlockStatePlacementPredicate(IBlockState state) {
        this.state = state;
    }

    @Override
    public boolean matches(IBlockState block) {
        return Utils.doStatesMatch(block, state);
    }

    class Serializer implements JsonSerializer<BlockStatePlacementPredicate> {

        @Override
        public JsonElement serialize(BlockStatePlacementPredicate src, Type typeOfSrc, JsonSerializationContext context) {
            return context.serialize(src.state);
        }
    }
}
