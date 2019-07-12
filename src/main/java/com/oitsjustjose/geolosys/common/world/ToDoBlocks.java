package com.oitsjustjose.geolosys.common.world;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.oitsjustjose.geolosys.Geolosys;
import com.oitsjustjose.geolosys.common.api.GeolosysAPI;
import com.oitsjustjose.geolosys.common.api.world.DepositMultiOre;
import com.oitsjustjose.geolosys.common.api.world.IOre;
import com.oitsjustjose.geolosys.common.api.world.IOreWithState;
import com.oitsjustjose.geolosys.common.util.Utils;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.util.Constants;

/**
 * Created by Thiakil on 17/06/2018. Minor edits by PersonTheCat
 */
public class ToDoBlocks extends WorldSavedData
{
    private Map<ChunkPos, Map<BlockPos, IOre>> pendingBlocks = new HashMap<>();

    public ToDoBlocks(String name)
    {
        super(name);
    }

    public static ToDoBlocks getForWorld(World world, String dataID)
    {
        ToDoBlocks ret = (ToDoBlocks) world.loadData(ToDoBlocks.class, dataID);
        if (ret == null)
        {
            ret = new ToDoBlocks(dataID);
            world.setData(dataID, ret);
        }
        return ret;
    }

    public void storePending(BlockPos pos, IOre state)
    {
        Map<BlockPos, IOre> entries = pendingBlocks.computeIfAbsent(new ChunkPos(pos), k -> new HashMap<>());
        entries.put(pos.toImmutable(), state);
        markDirty();
    }

    public List<IBlockState> findMatcher(IBlockState state)
    {
        for (IOre ore : GeolosysAPI.oreBlocks)
        {
            if (ore instanceof DepositMultiOre)
            {
                DepositMultiOre multiOre = (DepositMultiOre) ore;
                for (IBlockState oreState : multiOre.getOres())
                {
                    if (Utils.doStatesMatch(oreState, state))
                    {
                        if (ore.getBlockStateMatchers() == null)
                        {
                            return GeolosysAPI.replacementMats;
                        }
                        else
                        {
                            return ore.getBlockStateMatchers();
                        }
                    }
                }
            }
            else if (Utils.doStatesMatch(state, ore.getOre()))
            {
                if (ore.getBlockStateMatchers() == null)
                {
                    return GeolosysAPI.replacementMats;
                }
                else
                {
                    return ore.getBlockStateMatchers();
                }
            }
        }
        return GeolosysAPI.replacementMats;
    }

    public void processPending(ChunkPos pos, World world)
    {
        Map<BlockPos, IOre> pending = pendingBlocks.get(pos);
        if (pending != null && !pending.isEmpty())
        {
            Iterator<Map.Entry<BlockPos, IOre>> iterator = pending.entrySet().iterator();
            while (iterator.hasNext())
            {
                Map.Entry<BlockPos, IOre> e = iterator.next();
                BlockPos blockPos = e.getKey();

                IBlockState state = world.getBlockState(blockPos);
                IOre ore = e.getValue();

                if (state != null && ore != null)
                {
                    if (ore instanceof IOreWithState) {
                        ((IOreWithState)ore).tryPlace(world, blockPos, state, world.rand);
                    } else {
                        world.setBlockState(blockPos, ore.getOre(), 2 | 16);
                    }
                }

                iterator.remove();
            }
            if (pending.isEmpty())
            {
                pendingBlocks.remove(pos);
            }
            markDirty();
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        pendingBlocks.clear();
        for (String key : nbt.getKeySet())
        {
            NBTBase val = nbt.getTag(key);
            if (val instanceof NBTTagList && ((NBTTagList) val).getTagType() == Constants.NBT.TAG_COMPOUND
                    && !val.hasNoTags())
            {
                try
                {
                    Long asLong = Long.parseLong(key);
                    NBTTagList list = (NBTTagList) val;
                    ChunkPos chunkPos = new ChunkPos((int) (asLong & 4294967295L),
                            (int) ((asLong >> 32) & 4294967295L));
                    Map<BlockPos, IOre> entries = new HashMap<>();
                    for (NBTBase b : list)
                    {
                        if (b instanceof NBTTagCompound && ((NBTTagCompound) b).hasKey("pos")
                                && ((NBTTagCompound) b).hasKey("state"))
                        {
                            NBTTagCompound e = (NBTTagCompound) b;

                            if(!e.hasKey("oreId")) continue; // Strip out legacy entries

                            String id = e.getString("oreId");
                            IOre ore = BaseGenerator.getGeneratorById(id);

                            if (ore == null) {
                                Geolosys.getInstance().LOGGER.warn("Unable to find OreGen with ID " + id);
                            }

                            entries.put(NBTUtil.getPosFromTag(e.getCompoundTag("pos")), ore);
                        }
                    }
                    if (!entries.isEmpty())
                    {
                        pendingBlocks.put(chunkPos, entries);
                    }
                }
                catch (NumberFormatException e)
                {
                    // bad key, carry on
                }
            }
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound)
    {
        for (Map.Entry<ChunkPos, Map<BlockPos, IOre>> chunkEntry : pendingBlocks.entrySet())
        {
            if (!chunkEntry.getValue().isEmpty())
            {
                NBTTagList chunkEntries = new NBTTagList();
                compound.setTag(Long.toString(ChunkPos.asLong(chunkEntry.getKey().x, chunkEntry.getKey().z)),
                        chunkEntries);
                for (Map.Entry<BlockPos, IOre> blockEntry : chunkEntry.getValue().entrySet())
                {
                    NBTTagCompound entry = new NBTTagCompound();
                    entry.setTag("pos", NBTUtil.createPosTag(blockEntry.getKey()));
                    entry.setString("oreId", blockEntry.getValue().getId());
                    chunkEntries.appendTag(entry);
                }
            }
        }
        return compound;
    }
}
