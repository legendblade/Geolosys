package com.oitsjustjose.geolosys.common.util;

import java.util.ArrayList;

import com.oitsjustjose.geolosys.Geolosys;
import com.oitsjustjose.geolosys.common.api.world.IOre;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import org.apache.commons.codec.digest.Md5Crypt;

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

    public static String getDefaultIdForOre(IOre ore) {
        Geolosys.getInstance().LOGGER.warn(ore.getFriendlyName() + " ore entry is using a default ID. This will " +
            "cause ore veins to stop abruptly when loading worlds. Packmakers should add 'id' to their configs, modmakers " +
            "should ensure they override getId().");
        return Md5Crypt.apr1Crypt(ore.getFriendlyName() + ore.hashCode());
    }
}
