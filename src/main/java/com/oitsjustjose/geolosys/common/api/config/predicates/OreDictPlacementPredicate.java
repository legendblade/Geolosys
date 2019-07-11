package com.oitsjustjose.geolosys.common.api.config.predicates;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.oredict.OreDictionary;

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
}
