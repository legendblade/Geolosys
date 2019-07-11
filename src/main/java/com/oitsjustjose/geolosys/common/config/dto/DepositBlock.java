package com.oitsjustjose.geolosys.common.config.dto;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import com.oitsjustjose.geolosys.common.util.Utils;
import net.minecraft.block.state.IBlockState;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@JsonAdapter(DepositBlock.Deserializer.class)
public class DepositBlock {
    public IBlockState block;

    public int chance;

    public List<IBlockState> predicate;

    /**
     * Initialize a 100% chance block that will spawn in anything
     * @param block     The block to place
     */
    public DepositBlock(IBlockState block) {
        this(block, 100);
    }

    /**
     * Initialize a block that will spawn in anything with the given chance
     * @param block     The block to place
     * @param chance    The chance to place it
     */
    public DepositBlock(IBlockState block, int chance) {
        this(block, chance, new ArrayList<>());
    }

    /**
     * Initialize a block that will spawn in the given block predicates at the given chance
     * @param block     The block to place
     * @param chance    The chance to place it
     * @param predicate The list of blocks to place this in
     */
    public DepositBlock(IBlockState block, int chance, List<IBlockState> predicate) {
        this.block = block;
        this.chance = chance;
        this.predicate = predicate;
    }

    class Deserializer implements JsonDeserializer<DepositBlock> {
        @Override
        public DepositBlock deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            // Lots of boilerplate to deal with possible bad user input
            if (!json.isJsonObject()) throw new JsonParseException("Block should be an object: " + json.toString());

            JsonObject obj = json.getAsJsonObject();
            if (!obj.has("block")) throw new JsonParseException("Block must contain a 'block' property: " + json.toString());

            // Get the block itself:
            IBlockState blockState = Utils.getBlockStateFromString(obj.get("block").getAsString());
            if (blockState == null) throw new JsonParseException("Unknown block " + obj.get("block").toString());

            // And the chance, if one is set; otherwise make it 100%
            int chance = 100;
            if (obj.has("chance")) chance = obj.get("chance").getAsInt();

            // Finally, set up our predicates
            List<IBlockState> predicates = new LinkedList<>();
            if (obj.has("predicate")) {
                JsonElement element = obj.get("predicate");

                if (element.isJsonPrimitive()) {
                    // Which can be a short string:
                    IBlockState b = Utils.getBlockStateFromString(element.getAsString());
                    if (b == null) throw new JsonParseException("Unknown predicate block " + element.toString());
                    predicates.add(b);
                } else if (element.isJsonArray()) {
                    // Or an array of strings
                    element.getAsJsonArray().forEach((item) -> {
                        IBlockState b = Utils.getBlockStateFromString(item.getAsString());
                        if (b == null) throw new JsonParseException("Unknown predicate block " + item.toString());
                        predicates.add(b);
                    });
                } else {
                    throw new JsonParseException("Block predicate was not a string or an array");
                }
            }

            return new DepositBlock(blockState, chance, predicates);
        }
    }
}
