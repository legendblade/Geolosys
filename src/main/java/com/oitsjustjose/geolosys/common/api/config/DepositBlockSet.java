package com.oitsjustjose.geolosys.common.api.config;


import com.google.gson.*;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.JsonAdapter;
import com.oitsjustjose.geolosys.Geolosys;
import com.oitsjustjose.geolosys.common.util.Utils;
import net.minecraft.block.state.IBlockState;

import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;

/**
 * Wrapper implementation to gracefully handle v3 configs
 */
@JsonAdapter(DepositBlockSet.Deserializer.class)
public class DepositBlockSet {
    @Expose
    public List<DepositBlock> blocks;

    public DepositBlockSet(List<DepositBlock> blocks) {
        this.blocks = blocks;
    }

    class Deserializer implements JsonDeserializer<DepositBlockSet> {

        @Override
        public DepositBlockSet deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            if (!json.isJsonArray()) throw new JsonParseException("Deposit must have an array of blocks.");

            List<DepositBlock> blocks = new LinkedList<>();

            IBlockState prev = null;

            for(JsonElement b : json.getAsJsonArray()) {
                if (b.isJsonPrimitive()) {
                    // We're dealing with an old config entry, or a block by itself
                    JsonPrimitive primitive = b.getAsJsonPrimitive();

                    if(primitive.isString()) {
                        // If it's a block by itself, just add it at 100; this is probably a bad plan:
                        if(prev != null) blocks.add(new DepositBlock(prev));
                        prev = Utils.getBlockStateFromString(primitive.getAsString());
                    } else if (primitive.isNumber() && prev != null) {
                        // If we have a block and have found a number, consider it a block/chance pair
                        blocks.add(new DepositBlock(prev, primitive.getAsInt()));
                        prev = null;
                    } else {
                        Geolosys.getInstance().LOGGER.warn("Unexpected value " + b.toString() + " while reading blocks");
                    }
                    continue;
                } else if (!b.isJsonObject()) {
                    Geolosys.getInstance().LOGGER.warn("Unexpected value " + b.toString() + " while reading blocks");
                    continue;
                }

                // Add the previous block in if we haven't already:
                if(prev != null) blocks.add(new DepositBlock(prev));
                prev = null;

                try {
                    blocks.add(context.deserialize(b, DepositBlock.class));
                } catch (JsonParseException e) {
                    // Use the underlying message as our warning
                    Geolosys.getInstance().LOGGER.warn(e.getMessage());
                }
            }

            return new DepositBlockSet(blocks);
        }
    }
}
