package com.oitsjustjose.geolosys.common.api.config.predicates;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.annotations.JsonAdapter;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.oredict.OreDictionary;

import java.lang.reflect.Type;

@JsonAdapter(OreDictPlacementPredicate.Serializer.class)
public class OreDictPlacementPredicate implements IPlacementPredicate {
    private final String dict;

    public OreDictPlacementPredicate(String dict) {
        this.dict = dict.startsWith("ore:") ? dict.substring(4) : dict;
    }

    @Override
    public boolean matches(IBlockState block) {
        NonNullList<ItemStack> ores = OreDictionary.getOres(dict);

        if (ores.isEmpty()) return false;

        ItemStack blockAsStack = new ItemStack(block.getBlock(), 1, block.getBlock().getMetaFromState(block));

        for (ItemStack ore : ores) {
            if (OreDictionary.itemMatches(blockAsStack, ore, false)) return true;
        }

        return false;
    }

    class Serializer implements JsonSerializer<OreDictPlacementPredicate> {
        @Override
        public JsonElement serialize(OreDictPlacementPredicate src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive("ore:" + src.dict);
        }
    }
}
