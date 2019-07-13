package com.oitsjustjose.geolosys.common.api.config;

import com.google.gson.*;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.JsonAdapter;
import com.oitsjustjose.geolosys.common.api.config.predicates.BlockStatePlacementPredicate;
import com.oitsjustjose.geolosys.common.api.config.predicates.OreDictPlacementPredicate;
import com.oitsjustjose.geolosys.common.util.Utils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

@JsonAdapter(BaseDepositBlock.Deserializer.class)
public class BaseDepositBlock {
    @Expose
    public IBlockState block;

    @Expose
    protected List<Predicate<IBlockState>> predicates;

    /**
     * Checks if the block state at the given position can be replaced
     * with this alternative block
     * @param pos   The position to replace at
     * @param state The block state to replace
     * @return  True if it can, false otherwise.
     */
    public boolean canReplace(BlockPos pos, IBlockState state) {
        if (predicates == null || predicates.size() <= 0) return true;

        for (Predicate<IBlockState> p : predicates) {
            if (p.test(state)) return true;
        }
        return false;
    }

    /**
     * Translates the input string into a placement predicate
     * @param string    The string to parse
     * @return          The parsed predicate
     * @throws JsonParseException   If the predicate could not be matched
     */
    protected static Predicate<IBlockState> deserializePredicate(String string) throws JsonParseException {
        if (string.startsWith("ore:")) {
            return new OreDictPlacementPredicate(string);
        }

        IBlockState b = Utils.getBlockStateFromString(string);
        if (b == null) throw new JsonParseException("Unknown predicate block " + string);
        return new BlockStatePlacementPredicate(b);
    }

    /**
     * Adds predicates (if any) to the block
     * @param obj       The input object to read
     * @param output    The block to add predicates to
     */
    protected static void deserializePredicates(JsonObject obj, BaseDepositBlock output) throws JsonParseException {
        if (!obj.has("predicate")) return;

        output.predicates = new ArrayList<>();
        JsonElement predicateElement = obj.get("predicate");

        if (predicateElement.isJsonPrimitive()) {
            output.predicates.add(deserializePredicate(predicateElement.getAsString()));
        } else if (predicateElement.isJsonArray()) {
            predicateElement.getAsJsonArray().forEach((predicate) -> {
                output.predicates.add(deserializePredicate(predicate.getAsString()));
            });
        } else {
            throw new JsonParseException("Unable to parse predicate; it must be either an array or a single block.");
        }
    }

    class Deserializer implements JsonDeserializer<BaseDepositBlock> {
        @Override
        public BaseDepositBlock deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            if (!json.isJsonObject()) throw new JsonParseException("Alternative block must be an object");

            JsonObject obj = json.getAsJsonObject();
            if (!obj.has("block") || !obj.has("predicate")) throw new JsonParseException("Alternative block must have both 'block' and 'predicate'");

            BaseDepositBlock output = new BaseDepositBlock();

            output.block = Utils.getBlockStateFromString(obj.get("block").getAsString());
            if (output.block == null) throw new JsonParseException("Unable to get block for alternate.");

            BaseDepositBlock.deserializePredicates(obj, output);

            return output;
        }
    }
}
