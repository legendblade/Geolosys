package com.oitsjustjose.geolosys.common.api.config;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import com.oitsjustjose.geolosys.common.api.config.predicates.BlockStatePlacementPredicate;
import com.oitsjustjose.geolosys.common.api.config.predicates.IPlacementPredicate;
import com.oitsjustjose.geolosys.common.api.config.predicates.OreDictPlacementPredicate;
import com.oitsjustjose.geolosys.common.util.Utils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@JsonAdapter(AlternateDepositBlock.Deserializer.class)
public class AlternateDepositBlock {
    public IBlockState block;

    private List<IPlacementPredicate> predicates;

    /**
     * Checks if the block state at the given position can be replaced
     * with this alternative block
     * @param pos   The position to replace at
     * @param state The block state to replace
     * @return  True if it can, false otherwise.
     */
    public boolean canReplace(BlockPos pos, IBlockState state) {
        for (IPlacementPredicate p : predicates) {
            if (p.matches(state)) return true;
        }
        return false;
    }

    class Deserializer implements JsonDeserializer<AlternateDepositBlock> {
        @Override
        public AlternateDepositBlock deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            if (!json.isJsonObject()) throw new JsonParseException("Alternative must be an object");

            JsonObject obj = json.getAsJsonObject();
            if (!obj.has("block") || !obj.has("predicate")) throw new JsonParseException("Alternative must have both 'block' and 'predicate'");

            AlternateDepositBlock output = new AlternateDepositBlock();

            output.block = Utils.getBlockStateFromString(obj.get("block").getAsString());
            if (output.block == null) throw new JsonParseException("Unable to get block for alternate.");

            output.predicates = new ArrayList<>();
            JsonElement predicateElement = obj.get("predicate");

            if (predicateElement.isJsonPrimitive()) {
                output.predicates.add(getPredicate(predicateElement.getAsString()));
            } else if (predicateElement.isJsonArray()) {
                predicateElement.getAsJsonArray().forEach((predicate) -> {
                    output.predicates.add(getPredicate(predicate.getAsString()));
                });
            } else {
                throw new JsonParseException("Unable to parse predicate; it must be either an array or a single block.");
            }

            return output;
        }

        /**
         * Translates the input string into a placement predicate
         * @param string    The string to parse
         * @return          The parsed predicate
         * @throws JsonParseException   If the predicate could not be matched
         */
        private IPlacementPredicate getPredicate(String string) throws JsonParseException {
            if (string.startsWith("ore:")) {
                return new OreDictPlacementPredicate(string);
            }

            IBlockState b = Utils.getBlockStateFromString(string);
            if (b == null) throw new JsonParseException("Unknown predicate block " + string);
            return new BlockStatePlacementPredicate(b);
        }
    }
}
