package com.oitsjustjose.geolosys.common.api.config;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import com.oitsjustjose.geolosys.common.util.Utils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@JsonAdapter(DepositBlock.Deserializer.class)
public class DepositBlock {
    public IBlockState block;

    public int chance;

    public List<AlternateDepositBlock> alternatives;

    /**
     * Initialize a 100% chance block that will always spawn
     * @param block     The block to place
     */
    public DepositBlock(IBlockState block) {
        this(block, 100);
    }

    /**
     * Initialize a block that will spawn with the given chance
     * @param block     The block to place
     * @param chance    The chance to place it
     */
    public DepositBlock(IBlockState block, int chance) {
        this(block, chance, new ArrayList<>());
    }

    /**
     * Initialize a block that will spawn at the given chance, picking from a list of options first
     * @param block         The block to place
     * @param chance        The chance to place it
     * @param alternatives  Optional alternative blocks to use based on block state
     */
    public DepositBlock(IBlockState block, int chance, List<AlternateDepositBlock> alternatives) {
        this.block = block;
        this.chance = chance;
        this.alternatives = alternatives;
    }

    class Deserializer implements JsonDeserializer<DepositBlock> {
        @Override
        public DepositBlock deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            // Lots of boilerplate to deal with possible bad user input
            if (!json.isJsonObject()) throw new JsonParseException("Block should be an object: " + json.toString());

            JsonObject obj = json.getAsJsonObject();
            IBlockState blockState;
            if (!obj.has("block")) {
                blockState = Blocks.AIR.getDefaultState();
            } else {
                // Get the block itself:
                blockState = Utils.getBlockStateFromString(obj.get("block").getAsString());
                if (blockState == null) throw new JsonParseException("Unknown block " + obj.get("block").toString());
            }

            // And the chance, if one is set; otherwise make it 100%
            int chance = 100;
            if (obj.has("chance")) chance = obj.get("chance").getAsInt();

            // Finally, set up our predicates
            List<AlternateDepositBlock> alternatives = new LinkedList<>();
            if (obj.has("alternatives")) {
                JsonElement element = obj.get("alternatives");

                if (element.isJsonArray()) {
                    // Or an array of strings
                    element.getAsJsonArray().forEach((item) -> {
                        alternatives.add(context.deserialize(item, AlternateDepositBlock.class));
                    });
                } else {
                    throw new JsonParseException("Block alternatives is not an array");
                }
            }

            return new DepositBlock(blockState, chance, alternatives);
        }
    }
}
