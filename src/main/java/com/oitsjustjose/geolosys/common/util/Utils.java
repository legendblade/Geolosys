package com.oitsjustjose.geolosys.common.util;

import java.util.ArrayList;

import com.oitsjustjose.geolosys.Geolosys;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class Utils
{
    public static IBlockState getStateFromMeta(Block block, int meta)
    {
        try
        {
            return block.getStateForPlacement(null, null, EnumFacing.UP, 0F, 0F, 0F, meta, null, null);
        }
        catch (NullPointerException e)
        {
            return null;
        }
    }

    public static ItemStack blockStateToStack(IBlockState state)
    {
        return new ItemStack(Item.getItemFromBlock(state.getBlock()), 1, state.getBlock().getMetaFromState(state));
    }

    public static boolean doStatesMatch(IBlockState state1, IBlockState state2)
    {
        return (state1.getBlock() == state2.getBlock()
                && state1.getBlock().getMetaFromState(state1) == state2.getBlock().getMetaFromState(state2));
    }

    /**
     * Converts a string representation of a blockstate to an actual block state
     * @param iBlockState   The block state string (eg: minecraft:iron_ore, or minecraft:wool:1)
     * @return  The matching block state, or null if none was found
     */
    public static IBlockState getBlockStateFromString(String iBlockState)
    {
        String[] parts = iBlockState.split(":");
        if (parts.length == 2)
        {
            Block b = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(parts[0], parts[1]));
            return b.getDefaultState();
        }
        else if (parts.length == 3)
        {
            Block b = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(parts[0], parts[1]));
            return Utils.getStateFromMeta(b, Integer.parseInt(parts[2]));
        }
        else
        {
            Geolosys.getInstance().LOGGER.info(
                    "String " + iBlockState + " is not a valid block with or without metadata. It has been skipped");
            return null;
        }
    }
}
